package com.spzx.product.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.spzx.product.service.TestService;
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

   /**
    * 不能保证原子性
    */
   public void testLock1() {
      String values = stringRedisTemplate.opsForValue().get("num");
      if(StringUtils.isBlank(values)){
         return;
      }
      int nums = Integer.parseInt(values);
      this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++nums));
   }


   /**
    * 本地锁只能锁住同一工程内的资源，在分布式系统里面都存在局限性。
    */
   public synchronized void testLock2() {
      String values = stringRedisTemplate.opsForValue().get("num");
      if(StringUtils.isBlank(values)){
         return;
      }
      int nums = Integer.parseInt(values);
      this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++nums));
   }

   /**
    * 采用SpringDataRedis实现分布式锁
    * 原理：执行业务方法前先尝试获取锁（setnx存入key val），如果获取锁成功再执行业务代码，业务执行完毕后将锁释放(del key)
    *
    * 问题：setnx刚好获取到锁，业务逻辑出现异常，导致锁无法释放
    * 解决：设置过期时间，自动释放锁。 testLock4
    */
   public void testLock3(){
      // 先尝试获取锁 setnx key val
      Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lock");
      // 获取锁成功，执行业务代码
      if(flag){
         //1.先从redis中通过key num获取值
         String numValue = stringRedisTemplate.opsForValue().get("num");
         //2.如果值为空则非法直接返回即可
         if(StringUtils.isBlank(numValue)) return;
         //3.对num值进行自增加一
         int nums = Integer.parseInt(numValue);
         stringRedisTemplate.opsForValue().set("num", String.valueOf(++nums));
      }else {
         try {
            Thread.sleep(300);
            // 自旋函数
            this.testLock();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
   }


   /**
    * 问题：可能会释放其他服务器的锁。
    * 解决：setnx获取锁时，设置一个指定的唯一值（例如：uuid）；释放前获取这个值，判断是否自己的锁
    *
    * 问题: 删除操作缺乏原子性。
    * 解决: 使用lua脚本保证原子性  testLock5
    */
   public void testLock4() {

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


   /**
    * 问题 不可重入性
    * 解决 加锁 解锁 lua脚本
    */
   public void  testLock5() {
      //0.先尝试获取锁 setnx key val
      String uuid = UUID.randomUUID().toString();
      Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);

      //获取锁成功，执行业务代码
      //1.先从redis中通过key num获取值
      if(flag){
         String values = stringRedisTemplate.opsForValue().get("num");
         //2.如果值为空则非法直接返回即可
         if(StringUtils.isBlank(values)) return;
         //3.对num值进行自增加一
         int number = Integer.parseInt(values);
         stringRedisTemplate.opsForValue().set("num",String.valueOf(++number));
         //4.将锁释放 判断uuid

         //4.1 先创建脚本对象 DefaultRedisScript泛型脚本语言返回值类型 Long 0：失败 1：成功
         DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
         //4.2设置脚本文本
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
            //自旋重试
            this.testLock();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
   }

   private boolean  tryLock(String lockName,String uuid,Long expire){
      String script = "if (redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1) " +
              "then" +
              "    redis.call('hincrby', KEYS[1], ARGV[1], 1);" +
              "    redis.call('expire', KEYS[1], ARGV[2]);" +
              "    return 1;" +
              "else" +
              "   return 0;" +
              "end";
      if (!this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockName), uuid, expire.toString())){
         try {
            // 没有获取到锁，重试
            Thread.sleep(200);
            tryLock(lockName, uuid, expire);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      // 锁续期
      this.renewTime(lockName, uuid, expire * 1000);
      return true;
   }

   private void unlock(String lockName, String uuid){
      String script = "if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) then" +
              "    return nil;" +
              "end;" +
              "if (redis.call('hincrby', KEYS[1], ARGV[1], -1) > 0) then" +
              "    return 0;" +
              "else" +
              "    redis.call('del', KEYS[1]);" +
              "    return 1;" +
              "end;";
      // 这里之所以没有跟加锁一样使用 Boolean ,这是因为解锁 lua 脚本中，三个返回值含义如下：
      // 1 代表解锁成功，锁被释放
      // 0 代表可重入次数被减 1
      // null 代表其他线程尝试解锁，解锁失败
      Long result = this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Lists.newArrayList(lockName), uuid);
      // 如果未返回值，代表尝试解其他线程的锁
      if (result == null) {
         throw new IllegalMonitorStateException("attempt to unlock lock, not locked by lockName: "
                 + lockName + " with request: "  + uuid);
      }
   }

   /**
    * 不满足排他性
    * 场景:A线程超时时间设为10s（为了解决死锁问题），但代码执行时间可能需要30s，然后redis服务端10s后将锁删除。
    * 此时，B线程恰好申请锁，redis服务端不存在该锁，可以申请，也执行了代码。
    * 那么问题来了， A、B线程都同时获取到锁并执行业务逻辑，这与分布式锁最基本的性质相违背
    *
    * 解决方案 加入自动续期
    */
   public void testLock6() {
      // 加锁
      String uuid = UUID.randomUUID().toString();
      boolean lock = this.tryLock("lock", uuid, 300l);

      if(lock){
         String num = stringRedisTemplate.opsForValue().get("num");
         if(StringUtils.isBlank(num)) return;
         int values = Integer.parseInt(num);
         stringRedisTemplate.opsForValue().set("num", String.valueOf(++values));
         // 测试可重入性
         this.testSubLock(uuid);

         // 释放锁
         this.unlock("lock",uuid);
      }
   }

   // 测试可重入性
   private void testSubLock(String uuid) {
      boolean lock = this.tryLock("lock", uuid, 300l);
      if (lock) {
         System.out.println("分布式可重入锁。。。");

         this.unlock("lock", uuid);
      }
   }


   /**
    * 锁延期
    * 线程等待超时时间的2/3时间后,执行锁延时代码,直到业务逻辑执行完毕,因此在此过程中,其他线程无法获取到锁,保证了线程安全性
    * @param lockName
    * @param expire 单位：毫秒
    */
   private void renewTime(String lockName, String uuid, Long expire){
      String script = "if(redis.call('hexists', KEYS[1], ARGV[1]) == 1) then redis.call('expire', KEYS[1], ARGV[2]); return 1; else return 0; end";
      new Thread(() -> {
         while (this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Lists.newArrayList(lockName), uuid, expire.toString())){
            try {
               // 到达过期时间的2/3时间，自动续期
               Thread.sleep(expire / 3);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }).start();
   }



   @Override
   public void testLock() {
      // 加锁
      String uuid = UUID.randomUUID().toString();
      Boolean lock = this.tryLock("lock", uuid, 30l);
      if (lock) {
         // 读取redis中的num值
         String numString = this.stringRedisTemplate.opsForValue().get("num");
         if (StringUtils.isBlank(numString)) {
            return;
         }

         // ++操作
         Integer num = Integer.parseInt(numString);
         num++;

         // 放入redis
         this.stringRedisTemplate.opsForValue().set("num", String.valueOf(num));

         // 睡眠60s，锁过期时间30s。每隔20s自动续期
         try {
            TimeUnit.SECONDS.sleep(60);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         // 测试可重入性
         // this.testSubLock(uuid);

         // 释放锁
         this.unlock("lock", uuid);
      }
   }
}