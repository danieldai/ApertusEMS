package com.eltvpp.system.service.impl;

import com.eltvpp.system.service.*;
import com.eltvpp.system.api.domain.SysStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @title: 自动执行，异步完成的业务
 * @author: JUNFU.WANG
 * @date: 2024-02-23 14:06
 * @description:
 */
@Service
public class AutoGenSyncServiceImpl implements IAutoGenSyncService {
    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysGroupService groupService;

    @Autowired
    private ISysStationService stationService;

    /**
     * 创建站点完成后完成的业务（异步执行）
     * 将来此处要放到MQ中，防止多人同时操作时发生冲突
     */
    @Async
    @Override
    public void autoGenSyncForStation(String stationSn) {

        // 自动生成部门表
        boolean isNext = deptService.autoGenDeptSync();
        if (!isNext) {
            return;
        }

        // 刚刚创建的站点
        SysStation currStation = stationService.selectSysStationBySn(stationSn);

        if (currStation.getStationType() != 2) {
            return;
        }

        // 自动生成光伏设备目录
        groupService.autoGenPvGroupSync(currStation.getParentId(), currStation.getDeptId());
    }
}
