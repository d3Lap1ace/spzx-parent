package com.spzx.product.service.impl;

import com.spzx.product.service.TestService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

   @Autowired
   private StringRedisTemplate stringRedisTemplate;
    




   public synchronized void testLock1() {
      String values = stringRedisTemplate.opsForValue().get("num");
      if(StringUtils.isBlank(values)){
         return;
      }
      int nums = Integer.parseInt(values);
      this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++nums));
   }

   public void testLock2() {

      // 先尝试获取锁 setnx key val
      String uuid = UUID.randomUUID().toString();
      Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,3, TimeUnit.SECONDS);
      //获取锁成功，执行业务代码
      if(flag){
         //1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
         String values = stringRedisTemplate.opsForValue().get("num");
         //2.如果值为空则非法直接返回即可
         if(StringUtils.isBlank(values)) return;
         //3.对num值进行自增加一
         int nums = Integer.parseInt(values);
         stringRedisTemplate.opsForValue().set("num", String.valueOf(++nums));
         //4.将锁释放
         stringRedisTemplate.delete("flog");
      }else {
         try {
            Thread.sleep(100);
            this.testLock();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void testLock() {
      //0.先尝试获取锁 setnx key val
      //问题：锁可能存在线程间相互释放
      //Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lock", 10, TimeUnit.SECONDS);
      //解决：锁值设置为uuid
      String uuid = UUID.randomUUID().toString();
      Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);

      //获取锁成功，执行业务代码
      //1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
      if(flag){
         String values = stringRedisTemplate.opsForValue().get("num");
         //2.如果值为空则非法直接返回即可
         if(StringUtils.isBlank(values)) return;
         //3.对num值进行自增加一
         int number = Integer.parseInt(values);
         stringRedisTemplate.opsForValue().set("num",String.valueOf(++number));
         //4.将锁释放 判断uuid
         //问题：删除操作缺乏原子性。
         //if(uuid.equals(stringRedisTemplate.opsForValue().get("lock"))){ //线程一：判断是满足是当前线程锁的值
         //    //条件满足，此时锁正好到期，redis锁自动释放了线程2获取锁成功，线程1将线程2的锁删除
         //    stringRedisTemplate.delete("lock");
         //}
         //解决：redis执行lua脚本保证原子，lua脚本执行会作为一个整体执行
         DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

         String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n"+
                 "then\n" +
                 "    return redis.call(\"del\",KEYS[1])\n" +
                 "else\n" +
                 "    return 0\n" +
                 "end";
         redisScript.setScriptText(script);
         stringRedisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
      }else{
         try {
            Thread.sleep(100);
            this.testLock();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }





      //4.将锁释放 判断uuid
      //问题：删除操作缺乏原子性。
      //if(uuid.equals(stringRedisTemplate.opsForValue().get("lock"))){ //线程一：判断是满足是当前线程锁的值
      //    //条件满足，此时锁正好到期，redis锁自动释放了线程2获取锁成功，线程1将线程2的锁删除
      //    stringRedisTemplate.delete("lock");
      //}
      //解决：redis执行lua脚本保证原子，lua脚本执行会作为一个整体执行

      //执行脚本参数 参数1：脚本对象封装lua脚本，参数二：lua脚本中需要key参数（KEYS[i]）  参数三：lua脚本中需要参数值 ARGV[i]
      //4.1 先创建脚本对象 DefaultRedisScript泛型脚本语言返回值类型 Long 0：失败 1：成功

      //4.2设置脚本文本


      //4.3 设置响应类型
   }
}