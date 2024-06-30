package com.eltvpp.datav.algorithm.tool;

import com.eltvpp.system.api.RemoteCommonCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @title: 项目启动时执行更新节点任务
 * @author: JUNFU.WANG
 * @date: 2023-10-20 8:55
 * @description: 如果是定时任务动态创建表，还需要在定时任务中执行该方法更新节点配置
 */
@Component
public class AutoConfigShardingDateNodesListener implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoConfigShardingDateNodesListener.class);

    // 数据采集库：配置别名（如：ds0）
    @Value("${app.database.collect.db0.alias}")
    private String aliasName;

    // 动态更新 ShardingSphere 节点配置
    @Autowired
    private RefreshActualDataNodesAO refreshActualDataNodesAO;

    // 采集数据库表名存储
    @Autowired
    private RemoteCommonCreateService remoteCommonCreateService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 每小时执行一次
        LOGGER.debug("已启动节点更新任务");
        System.out.println("已启动节点更新任务");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleWithFixedDelay(new AutoConfigShardingDateNodesExecutor(aliasName, refreshActualDataNodesAO, remoteCommonCreateService), 0, 1, TimeUnit.HOURS);
    }
}