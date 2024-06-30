package com.eltvpp.datav.service.impl;

import com.eltvpp.datav.domain.vo.AssignDataVo;
import com.eltvpp.datav.service.ISpecialDataVService;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.datav.domain.ShardingQuery;
import com.eltvpp.datav.service.ShardingCommonService;
import com.eltvpp.system.api.RemoteDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 大屏专用数据（集合）需要直接访问采集数据
 *
 * @Author: JUNFU.WANG
 * @Date: 2024/6/26 16:58
 */
@Service
public class SpecialDataVServiceImpl implements ISpecialDataVService {

    @Autowired
    private ShardingCommonService shardingCommonService;

    @Autowired(required = false)
    private RemoteDictDataService remoteDictDataService;

    //region 获取指定接口数据

    /**
     * 获取指定接口数据（第2部分）
     * 第1部分在System模块中
     *
     * @param assignType 指定类型的key
     * @param createDate 项目创建日期
     * @return 结果
     */
    @Override
    public AssignDataVo getAssignedData(String assignType, String createDate) {
        switch (assignType) {
            //年减排量
            case "yearEmissionReduction":
                return getYearEmissionReduction(assignType);
            //总减排量
            case "total_emission_reduction":
                return getTotalEmissionReduction(assignType, createDate);
            //总用电量
            case "total_electric_use":
                return getTotalData(assignType, createDate, "总用电量", "kWh");
            //总发电量
            case "total_pv_power":
                return getTotalData(assignType, createDate, "总发电量", "kWh");
            //总充电量
            case "total_charge_power":
                return getTotalData(assignType, createDate, "总充电量", "kWh");
            //总储能量
            case "total_storage_power":
                return getTotalData(assignType, createDate, "总储能量", "kWh");
            //年发电量
            case "year_pv_power":
                return getTotalData(assignType, null, "年发电量", "kWh");
            //年充电量
            case "year_charge_power":
                return getTotalData(assignType, null, "年充电量", "kWh");
            //年储能量
            case "year_storage_power":
                return getTotalData(assignType, null, "年储能量", "kWh");
        }
        return null;
    }

    /**
     * 年减排量
     * -- 过去一年减少碳排放：23 CO₂吨，相当于植树 293 棵
     */
    private AssignDataVo getYearEmissionReduction(String assignType) {
        // 获取年发电量
        float total = packageTotalData(assignType, null);

        // 计算年减排量
        String coalValue = remoteDictDataService.getDictValue("energy_unit_convert", "electric_to_coal", SecurityConstants.INNER);
        float coal = coalValue == null ? 0f : Float.parseFloat(coalValue);
        float C02 = total * coal;

        // 计算植树量
        String treeValue = remoteDictDataService.getDictValue("energy_unit_convert", "tree_to_co2", SecurityConstants.INNER);
        float tree = treeValue == null ? 0f : Float.parseFloat(treeValue);
        float treeNum = C02 / tree;

        // 组装
        String value = StringUtils.format("过去一年减少碳排放：{} CO₂吨，相当于植树 {} 棵", C02, treeNum);

        return new AssignDataVo(assignType, value, "年减排量", "t/y");
    }

    /**
     * 总减排量
     */
    private AssignDataVo getTotalEmissionReduction(String assignType, String createDate) {
        // 获取总发电量
        float total = packageTotalData(assignType, createDate);

        // 计算总减排量
        String dictValue = remoteDictDataService.getDictValue("energy_unit_convert", "electric_to_coal", SecurityConstants.INNER);
        float coal = dictValue == null ? 0f : Float.parseFloat(dictValue);

        return new AssignDataVo(assignType, total * coal, "总减排量", "t");
    }

    /**
     * 总用电量
     * 总发电量
     * 总充电量
     * 总储能量
     * 年发电量
     * 年充电量
     * 年储能量
     */
    private AssignDataVo getTotalData(String assignType, String createDate, String label, String unit) {
        float total = packageTotalData(assignType, createDate);
        return new AssignDataVo(assignType, total, label, unit);
    }

    // 封装数据
    private Float packageTotalData(String assignType, String createDate) {
        if (createDate == null) {
            createDate = DateUtils.getCurrYear() + "-01-01";
        }
        Map<String, Object> dateToParamForYear = DateUtils.dateToParamForMonth(createDate, DateUtils.getDate());
        ShardingQuery query = new ShardingQuery(assignType, 2, 5, dateToParamForYear);
        Float total = shardingCommonService.selectTotalDataForYear(query);
        return total == null ? 0 : total;
    }
    //endregion
}
