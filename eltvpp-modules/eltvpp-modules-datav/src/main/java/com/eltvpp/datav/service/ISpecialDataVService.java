package com.eltvpp.datav.service;

import com.eltvpp.datav.domain.vo.AssignDataVo;

/**
 * 大屏专用数据（集合）需要直接访问采集数据
 *
 * @Author: JUNFU.WANG
 * @Date: 2024/6/26 16:54
 */
public interface ISpecialDataVService {
    /**
     * 获取指定接口数据（第2部分）
     * 第1部分在System模块中
     *
     * @param assignType 指定类型的key
     * @param createDate 项目创建日期
     * @return 结果
     */
    public AssignDataVo getAssignedData(String assignType, String createDate);
}
