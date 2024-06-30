package com.eltvpp.system.service;

/**
 * @title: 自动执行，异步完成的业务
 * @author: JUNFU.WANG
 * @date: 2024-02-23 14:05
 * @description:
 */
public interface IAutoGenSyncService {

    /**
     * 创建站点完成后完成的业务
     *
     * @param stationSn 站点SN
     */
    public void autoGenSyncForStation(String stationSn);
}
