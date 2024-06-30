package com.eltvpp.system.service;

import com.eltvpp.system.domain.vo.time.TimesVo;
import com.eltvpp.system.domain.vo.AssignDataVo;

/**
 * 大屏专用数据（集合）
 *
 * @Author: JUNFU.WANG
 * @Date: 2024/6/23 10:16
 */
public interface ISpecialDataProvideService {
    /**
     * 获取报警列表
     *
     * @param headType   表头类型：1长表头 2短表头
     * @param staticType 数据类型：记录状态
     * @return 结果
     */
    public Object getAlarmTriggerList(Integer headType, Integer staticType, TimesVo timesVo);

    /**
     * 获取工单列表
     *
     * @param headType   表头类型：1长表头 2短表头
     * @param staticType 数据类型：记录状态
     * @return 结果
     */
    public Object getDevopsOrderList(Integer headType, Integer staticType, TimesVo timesVo);

    /**
     * 获取报警统计数据
     *
     * @param staticType 统计类型（1按类型 2按级别）（1按类型 2按日期）
     * @return 结果
     */
    public Object getAlarmTriggerStatic(Integer staticType, TimesVo timesVo);

    /**
     * 获取工单统计数据
     *
     * @param staticType 统计类型（1按类型 2按级别）（1按类型 2按日期）
     * @return 结果
     */
    public Object getDevopsOrderStatic(Integer staticType, TimesVo timesVo);

    /**
     * 获取指定接口数据（第1部分）
     * 第2部分在DataV模块中
     *
     * @param deptId     站点（部门）ID
     * @param assignType 指定类型的key
     * @return 结果
     */
    public AssignDataVo getAssignedData(Long deptId, String assignType);
}
