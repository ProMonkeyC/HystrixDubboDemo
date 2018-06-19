package com.example.demo.dubbo;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcResult;
import com.example.demo.fallback.Fallback;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
public class DubboCommand extends HystrixCommand{

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Invoker<?> invoker;

  private Invocation invocation;

  private String fallbackName;

  public DubboCommand(Setter setter, Invoker<?> invoker, Invocation invocation, String fallbackName) {
    super(setter);
    this.invoker = invoker;
    this.invocation = invocation;
    this.fallbackName = fallbackName;
  }

  @Override
  protected Object run() throws Exception {

    Result result = invoker.invoke(invocation);

    if (result.hasException()){
      throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, DubboCommand.class,
              result.getException().getMessage(), result.getException(), null);
    }

    return result;
  }

  @Override
  protected Object getFallback() {

    if (StringUtils.isEmpty(fallbackName)){
      return super.getFallback(); //抛出原本异常.
    }

    try {
      //基于SPI扩展加载fallback实现
      ExtensionLoader<Fallback> loader = ExtensionLoader.getExtensionLoader(Fallback.class);
      Fallback fallback = loader.getExtension(fallbackName);
      Object value = fallback.invoke();
      return new RpcResult(value);
    } catch (RuntimeException e){
      logger.error("fallback failed, exceptionMsg = {}, case = {}", e.getMessage(), e.getCause());
      throw e;
    }

  }
}
