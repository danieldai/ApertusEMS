package com.eltvpp.modules.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

/**
 * 监控中心
 * 
 * @author eltvpp
 */
@EnableAdminServer
@SpringBootApplication
public class EltvppMonitorApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppMonitorApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  监控中心启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ______  _    _______     \n" +
                " |  ____|| |  |__   __|    \n" +
                " | |__   | |     | |       \n" +
                " |  __|  | |     | |       \n" +
                " | |____ | |____ | |       \n" +
                " |______||______||_|       ");
    }
}
