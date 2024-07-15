package com.spzx.product.service.impl;

import com.spzx.product.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

   @Autowired
   private StringRedisTemplate stringRedisTemplate;
    
   @Override
   public void testLock() {
      // 查询Redis中的num值
      String getNum = stringRedisTemplate.opsForValue().get("num");
      // 没有该值return
      if(StringUtils.isBlank(getNum)){
         return;
      }
      // 有值就转成成int
      int intNum = Integer.parseInt(getNum);
      // 把Redis中的num值+1
      this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++intNum));
   }
}