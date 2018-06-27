package com.example.demo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.example.demo.dubbo.DubboCommand;
import com.example.demo.utils.HystrixTool;
import com.netflix.hystrix.HystrixCommand;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
@Activate(group = Constants.CONSUMER, before = "future") //Activate注解表示一个扩展是否被激活(使用),可以放在类定义和方法上，dubbo用它在spi扩展类定义上，表示这个扩展实现激活条件和时机
public class HystrixFilter implements Filter {


  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

    URL url = invoker.getUrl();
    String methodName = invocation.getMethodName();
    String interfaceName = invoker.getInterface().getName();

    //获取熔断设置
    HystrixCommand.Setter setter = HystrixTool.getSetter(interfaceName, methodName, url);

    //获取降级方法
    String fallback = url.getMethodParameter(methodName, "fallback");

    DubboCommand command = new DubboCommand(setter, invoker, invocation, fallback);

    Result result = (Result)command.execute(); //同步

    return result;
  }
}




























