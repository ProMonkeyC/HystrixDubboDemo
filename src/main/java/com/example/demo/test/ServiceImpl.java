package com.example.demo.test;

import java.util.concurrent.TimeUnit;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
public class ServiceImpl implements Service {

  @Override
  public String connWorld() {
    return "Hello World";
  }

  @Override
  public String connTimeOut() {

    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e){
      e.printStackTrace();
    }

    return "Hello World";
  }

  @Override
  public String connWithException() {
    return String.format("%s", 1/0);
  }
}
