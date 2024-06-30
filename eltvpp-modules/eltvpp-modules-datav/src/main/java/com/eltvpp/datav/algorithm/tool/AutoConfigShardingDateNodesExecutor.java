package com.eltvpp.datav.algorithm.tool;

import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.system.api.RemoteCommonCreateService;
import com.eltvpp.system.api.domain.SysCommonCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @title: 更新节点配置
 * @author: JUNFU.WANG
 * @date: 2024-03-05 16:20
 * @description:
 */
public class AutoConfigShardingDateNodesExecutor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoConfigShardingDateNodesExecutor.class);

    // 数据采集库：配置别名（如：ds0）
    private final String aliasName;

    // 动态更新 ShardingSphere 节点配置
    private final RefreshActualDataNodesAO refreshActualDataNodesAO;

    // 采集数据库表名存储
    private final RemoteCommonCreateService remoteCommonCreateService;

    // 初始化
    public AutoConfigShardingDateNodesExecutor(String aliasName, RefreshActualDataNodesAO refreshActualDataNodesAO, RemoteCommonCreateService remoteCommonCreateService) {
        this.aliasName = aliasName;
        this.refreshActualDataNodesAO = refreshActualDataNodesAO;
        this.remoteCommonCreateService = remoteCommonCreateService;
    }

    @Override
    public void run() {
        try {
            List<SysCommonCreate> tableList= remoteCommonCreateService.list(new SysCommonCreate(), SecurityConstants.INNER);
            if(StringUtils.isEmpty(tableList)){
                return;
            }
            //初始化Map
            Map<Integer, List<SysCommonCreate>> typeToListMap = new HashMap<>();

            // 分组
            for (SysCommonCreate item : tableList) {
                typeToListMap.computeIfAbsent(item.getCreateType(), k -> new ArrayList<>()).add(item);
            }
            //天
            List<SysCommonCreate> dayTableList = typeToListMap.getOrDefault(1, Collections.emptyList());
            //月
            List<SysCommonCreate> monthTableList = typeToListMap.getOrDefault(2, Collections.emptyList());

            //region //日生成表
            if (dayTableList.isEmpty()) {
                return;
            }

            StringBuilder dayNodes = new StringBuilder();
            int i = 0;
            for (SysCommonCreate table : dayTableList) {
                i++;
                dayNodes.append(aliasName).append(".day_").append(table.getCreateTable());
                if (i != dayTableList.size()) {
                    dayNodes.append(",");
                }
            }

            refreshActualDataNodesAO.generateActualDataNodes("sharding_day", dayNodes.toString());
            //endregion

            //region //月生成表
            if (monthTableList.isEmpty()) {
                return;
            }

            StringBuilder monthNodes = new StringBuilder();
            int j = 0;
            for (SysCommonCreate table : monthTableList) {
                j++;
                monthNodes.append(aliasName).append(".month_").append(table.getCreateTable());
                if (j != monthTableList.size()) {
                    monthNodes.append(",");
                }
            }

            refreshActualDataNodesAO.generateActualDataNodes("sharding_month", monthNodes.toString());
            //endregion

            //region //月累计值表
            StringBuilder monthNodesAccu = new StringBuilder();
            int k = 0;
            for (SysCommonCreate table : monthTableList) {
                k++;
                monthNodesAccu.append(aliasName).append(".month_accumulate_").append(table.getCreateTable());
                if (k != monthTableList.size()) {
                    monthNodesAccu.append(",");
                }
            }

            refreshActualDataNodesAO.generateActualDataNodes("sharding_month_accumulate", monthNodesAccu.toString());
            //endregion

            LOGGER.debug("节点已更新：" + DateUtils.getTime());
            System.out.println("节点已更新：" + DateUtils.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
