package com.example.demo.utils;

import com.alibaba.dubbo.common.URL;
import com.example.demo.utils.Constants;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
public abstract class HystrixTool {

  //定义全局Map用于存放HystrixCommand,Setter,便于下次直接获取
  private static ConcurrentHashMap<String, HystrixCommand.Setter> concurrentHashMap = new ConcurrentHashMap<>();

  /**
   * 获取setter.
   *
   * @param interfaceName
   * @param methodName
   * @param url
   * @return
   */
  public static HystrixCommand.Setter getSetter(String interfaceName, String methodName, URL url){

    String key = String.format("%s.%s", interfaceName, methodName);
    if (!concurrentHashMap.contains(key)){
      concurrentHashMap.put(key, createSetter(interfaceName, methodName, url));
    }
    return concurrentHashMap.get(key);

  }

  /**
   * 获取HystrixCommand.Setter.
   *
   * @param interfaceName
   * @param methodName
   * @param url
   * @return
   */
  private static HystrixCommand.Setter createSetter(String interfaceName, String methodName, URL url){
    //线程池按照class进行划分，一个class可以理解为一个领域服务，熔断保护按照方法方法唯独提供
    return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(interfaceName))
            .andCommandKey(HystrixCommandKey.Factory.asKey(methodName))
            .andCommandPropertiesDefaults(getCommandPropertiesSetter(url, methodName))
            .andThreadPoolPropertiesDefaults(getPoolPropertiesSetter(url));
  }

  /**
   * 获取隔离策略.
   *
   * @param url
   * @return
   */
  private static HystrixCommandProperties.ExecutionIsolationStrategy getIsolationStrategy(URL url){

    String isolation = url.getParameter("isolation", Constants.THREAD);

    if (!isolation.equalsIgnoreCase(Constants.THREAD) && !isolation.equalsIgnoreCase(Constants.SEMAPHORE)){
      isolation = Constants.THREAD; //默认线程隔离策略
    }

    if (isolation.equalsIgnoreCase(Constants.THREAD)){
      return HystrixCommandProperties.ExecutionIsolationStrategy.THREAD; //线程隔离策略
    } else {
      return HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE; //信号隔离策略
    }

  }

  /**
   * 熔断器相关设置.
   *
   * @param url
   * @param method
   * @return
   */
  private static HystrixCommandProperties.Setter getCommandPropertiesSetter(URL url, String method){
    return HystrixCommandProperties.Setter().withCircuitBreakerSleepWindowInMilliseconds(url.getMethodParameter(method, "sleepWindowInMillseconds", 5000))
            .withCircuitBreakerEnabled(true)
            .withCircuitBreakerErrorThresholdPercentage(url.getMethodParameter(method, "errorThresholdPercentage", 50))
            .withCircuitBreakerRequestVolumeThreshold(url.getMethodParameter(method, "requestVolumeThreshold",20))
            .withExecutionIsolationThreadInterruptOnTimeout(true)
            .withExecutionTimeoutInMilliseconds(url.getMethodParameter(method, "timeoutInMilliseconds", 1000))
            .withFallbackIsolationSemaphoreMaxConcurrentRequests(url.getMethodParameter(method, "fallbackIsolationSemaphoreMaxConcurrentRequests", 50))
            .withExecutionIsolationStrategy(getIsolationStrategy(url))
            .withExecutionIsolationSemaphoreMaxConcurrentRequests(url.getMethodParameter(method, "maxConcurrentRequests", 10));
  }

  /**
   * 线程池相关配置.
   *
   * @param url
   * @return
   */
  private static HystrixThreadPoolProperties.Setter getPoolPropertiesSetter(URL url){
    return HystrixThreadPoolProperties.Setter().withCoreSize(url.getParameter("coreSize", 10))
            .withAllowMaximumSizeToDivergeFromCoreSize(true)
            .withMaximumSize(url.getParameter("maximumSize", 20))
            .withMaxQueueSize(-1)
            .withKeepAliveTimeMinutes(url.getParameter("keepAliveTimeMinutes", 1));
  }

















































}
