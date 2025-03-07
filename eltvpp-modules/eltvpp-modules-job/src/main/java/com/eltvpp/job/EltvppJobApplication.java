package com.eltvpp.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.eltvpp.common.security.annotation.EnableCustomConfig;
import com.eltvpp.common.security.annotation.EnableRyFeignClients;
import com.eltvpp.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 定时任务
 * 
 * @author eltvpp
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients   
@SpringBootApplication
public class EltvppJobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppJobApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  定时任务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ______  _    _______     \n" +
                " |  ____|| |  |__   __|    \n" +
                " | |__   | |     | |       \n" +
                " |  __|  | |     | |       \n" +
                " | |____ | |____ | |       \n" +
                " |______||______||_|       ");
    }
}
