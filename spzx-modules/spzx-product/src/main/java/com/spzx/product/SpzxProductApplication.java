package com.spzx.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;

import java.io.IOException;

/**
 * 商品模块
 *
 * @author spzx
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SpzxProductApplication
{
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
}