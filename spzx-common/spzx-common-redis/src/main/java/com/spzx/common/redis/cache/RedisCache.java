package com.spzx.common.redis.cache;

import java.lang.annotation.*;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 16/7/2024 16:44 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {
    /**
     * 增加注解中方法（注解属性）通过注解属性指定放入缓存业务数据前缀、后缀
     */
    String prefix() default "data:";
}
