package com.eltvpp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 网关启动程序
 * 
 * @author eltvpp
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EltvppGatewayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppGatewayApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  阿帕图网关启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ______  _    _______     \n" +
                " |  ____|| |  |__   __|    \n" +
                " | |__   | |     | |       \n" +
                " |  __|  | |     | |       \n" +
                " | |____ | |____ | |       \n" +
                " |______||______||_|       ");
    }
}
