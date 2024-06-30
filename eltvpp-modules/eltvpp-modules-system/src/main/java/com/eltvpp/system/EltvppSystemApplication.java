package com.eltvpp.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.eltvpp.common.security.annotation.EnableCustomConfig;
import com.eltvpp.common.security.annotation.EnableRyFeignClients;
import com.eltvpp.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 系统模块
 * 
 * @author eltvpp
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class EltvppSystemApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppSystemApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  主系统模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ______  _    _______     \n" +
                " |  ____|| |  |__   __|    \n" +
                " | |__   | |     | |       \n" +
                " |  __|  | |     | |       \n" +
                " | |____ | |____ | |       \n" +
                " |______||______||_|       ");
    }
}
