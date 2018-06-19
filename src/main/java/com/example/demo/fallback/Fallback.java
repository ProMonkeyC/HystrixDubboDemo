package com.example.demo.fallback;

import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author chenlong
 * Created on 2018/6/13
 */
@SPI
public interface Fallback {

  /**
   * 业务失败降级处理.
   * @return
   */
  Object invoke();

}
