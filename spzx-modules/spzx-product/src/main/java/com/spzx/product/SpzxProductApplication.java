package com.spzx.product;

import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.mapper.ProductSkuMapper;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;

import java.io.IOException;
import java.util.List;

/**
 * 商品模块
 *
 * @author spzx
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SpzxProductApplication implements CommandLineRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private ProductSkuMapper productSkuMapper;

    public static void main(String[] args)
    {
        SpringApplication.run(SpzxProductApplication.class, args);



        System.out.println("/***\n" +
                " *                  ___====-_  _-====___\n" +
                " *            _--^^^#####//      \\\\#####^^^--_\n" +
                " *         _-^##########// (    ) \\\\##########^-_\n" +
                " *        -############//  |\\^^/|  \\\\############-\n" +
                " *      _/############//   (@::@)   \\\\############\\_\n" +
                " *     /#############((     \\\\//     ))#############\\\n" +
                " *    -###############\\\\    (oo)    //###############-\n" +
                " *   -#################\\\\  / VV \\  //#################-\n" +
                " *  -###################\\\\/      \\//###################-\n" +
                " * _#/|##########/\\######(   /\\   )######/\\##########|\\#_\n" +
                " * |/ |#/\\#/\\#/\\/  \\#/\\##\\  |  |  /##/\\#/  \\/\\#/\\#/\\#| \\|\n" +
                " * `  |/  V  V  `   V  \\#\\| |  | |/#/  V   '  V  V  \\|  '\n" +
                " *    `   `  `      `   / | |  | | \\   '      '  '   '\n" +
                " *                     (  | |  | |  )\n" +
                " *                    __\\ | |  | | /__\n" +
                " *                   (vvv(VVV)(VVV)vvv)                \n" +
                " *                        \n" +
                " *                       \n" +
                "                             勇者斗恶龙\n   "+
                " */");
        System.out.println();
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "            ————\n"+
                " ——————————/~ ~/\n"+
                " ——---————/´ ¯/)\n" +
                " —— ———--/—-/\n" +
                " — ————-/—-/\n" +
                " ———--/´¯/'--'/´¯`•_\n" +
                " ———-/'/--/—-/—--/¨¯\\\n" +
                " ——--('(———- ¯~/'--')\n" +
                " — ——\\————-'—--/\n" +
                " ———-'\\'————_-•´\n" +
                " — ———\\———--(\n" +
                " —— ——-\\———-");
    }

    @Override
    public void run(String... args) throws Exception {
        //初始化布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
        //设置数据规模 误判率 预计统计元素数量为100000，期望误差率为0.01
        bloomFilter.tryInit(100000,0.01);
        //测试使用，快速自动加入
        List<ProductSku> list = productSkuMapper.selectProductSkuList(null);
        list.forEach(it->{
            bloomFilter.add(it.getId());
        });
    }
}