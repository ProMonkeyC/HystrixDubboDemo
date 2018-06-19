/**
 * Copyright 2018 asiainfo Inc.
 **/
package com.example.demo;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
public class ProviderTest{

  @Test
  public void test() throws IOException{
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"provider.xml"});
    context.start();

    System.in.read();

    context.close();
  }

}
