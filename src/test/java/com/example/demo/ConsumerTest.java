/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.example.demo;

import com.example.demo.test.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
public class ConsumerTest {

  Service service;

  @Before
  public void before() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"consumer.xml"});
    context.start();
    service = (Service) context.getBean("service", Service.class);
  }

  @Test
  public void test(){
    Assert.assertEquals("Hello World", service.connWorld());
  }

  @Test
  public void testTimeOut(){
    Assert.assertEquals("Hello World", service.connTimeOut());
  }

  @Test
  public void testException(){
    service.connWithException();
  }

}
