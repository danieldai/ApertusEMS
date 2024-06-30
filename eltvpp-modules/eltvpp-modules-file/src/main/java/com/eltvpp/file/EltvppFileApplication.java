package com.eltvpp.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.eltvpp.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 文件服务
 * 
 * @author eltvpp
 */
@EnableCustomSwagger2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EltvppFileApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EltvppFileApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  文件服务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ______  _    _______     \n" +
                " |  ____|| |  |__   __|    \n" +
                " | |__   | |     | |       \n" +
                " |  __|  | |     | |       \n" +
                " | |____ | |____ | |       \n" +
                " |______||______||_|       ");
    }
}
