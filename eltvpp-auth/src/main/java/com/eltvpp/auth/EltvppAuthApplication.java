package com.eltvpp.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.eltvpp.common.security.annotation.EnableRyFeignClients;

/**
 * 认证授权中心
 * 
 * @author eltvpp
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EltvppAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppAuthApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
               "  ______  _    _______     \n" +
               " |  ____|| |  |__   __|    \n" +
               " | |__   | |     | |       \n" +
               " |  __|  | |     | |       \n" +
               " | |____ | |____ | |       \n" +
               " |______||______||_|       ");
    }
}
