package com.example.demo.fallback.impl;

import com.example.demo.fallback.Fallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenlong
 * Created on 2018/6/14
 */
public class FallbackImpl implements Fallback {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Object invoke() {

    logger.debug("------------- FallbackImpl invoke -----------------");

    return "Hello World";
  }
}
