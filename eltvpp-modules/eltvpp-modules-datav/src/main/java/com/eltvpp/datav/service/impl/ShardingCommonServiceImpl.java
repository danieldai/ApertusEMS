package com.eltvpp.datav.service.impl;

import com.eltvpp.datav.domain.ShardingMonth;
import com.eltvpp.datav.mapper.ShardingMonthMapper;
import com.eltvpp.common.datasource.annotation.ShardingDataSource;
import com.eltvpp.datav.domain.ShardingQuery;
import com.eltvpp.datav.service.ShardingCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 公共接口
 *
 * @author: XIAOTONG.CAO
 */
@ShardingDataSource
@Service
public class ShardingCommonServiceImpl implements ShardingCommonService {

    /**
     * 初始化
     * <pre>
     * valueType:数据类型（1单值 2连续值）
     * storageType:存储类型（1变化值 2累积值）
     * dateDim:日期维度（5、10 15 30 1 月 年）
     * </pre>
     */
    @PostConstruct
    public void init() {
        //单值-变化值+日维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:1|dateDim:70", this::singleChangeDayMonth);
        //单值+变化值+月维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:1|dateDim:80", this::singleChangeMonthMonth);
        //单值+变化值+年维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:1|dateDim:90", this::singleChangeMonthMonth);

        //单值-累计值+日维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:2|dateDim:70", this::singleChangeDayMonth);
        //单值+累计值+月维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:2|dateDim:80", this::singleChangeMonthMonth);
        //单值+累计值+年维度
        SHARDING_TYPE_MAP.put("valueType:1|storageType:2|dateDim:90", this::singleChangeMonthMonth);

        //连续值+变化值+日维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:1|dateDim:70", this::continuityChangeDayDay);
        //连续值+变化值+月维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:1|dateDim:80", this::continuityChangeMonthDay);
        //连续值+变化值+年维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:1|dateDim:90", this::continuityChangeYearDay);

        //连续值+累计值+日维度 todo
        SHARDING_TYPE_MAP.put("valueType:2|storageType:2|dateDim:70", this::continuityChangeDayDay);
        //连续值+累计值+月维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:2|dateDim:80", this::continuityChangeMonthDay);
        //连续值+累计值+年维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:2|dateDim:90", this::continuityChangeYearDay);


        //连续值+峰谷平+月维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:3|dateDim:80", this::continuityFengGuMonthDay);
        //连续值+峰谷平+年维度
        SHARDING_TYPE_MAP.put("valueType:2|storageType:3|dateDim:90", this::continuityFengGuYearDay);
    }
    @Autowired
    private ShardingMonthMapper shardingMonthMapper;

    /**
     * 查询最新一条数据
     * <pre>
     * single-change-day-day 单值 + 变化值 + 日维度
     * </pre>
     *
     * @param shardingQuery 查询条件 变量、数值类型（max,min,avg,acc,runtime）
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> singleChangeDayMonth(ShardingQuery shardingQuery) {
        return shardingMonthMapper.singleChangeDayMonth(shardingQuery);
    }

    /**
     * 月表-查询指定字段数据
     * <pre>
     * single-change-day-month 单值+日维度
     * </pre>
     *
     * @param shardingQuery 查询条件
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> singleChangeMonthMonth(ShardingQuery shardingQuery){
        return shardingMonthMapper.singleChangeMonthMonth(shardingQuery);
    }

    /**
     * 日表-查询指定时间段内数据
     * <pre>
     * continuity-change-day-day 连续值+变化值+日维度 (5 10 15 30分钟 1时)
     * </pre>
     *
     * @param shardingQuery 查询条件
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityChangeDayDay(ShardingQuery shardingQuery) {
        return shardingMonthMapper.continuityChangeDayDay(shardingQuery);
    }

    /**
     * 日表-查询指定时间段内数据
     * <pre>
     * continuity-change-day-day 连续值+累计值+日维度(日期维度 5 10 15 30分钟 1时)
     * </pre>
     *
     * @param shardingQuery 查询条件
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityChangeDayDayForAccu(ShardingQuery shardingQuery){
//        String endTime = String.valueOf(shardingQuery.getParams().get("endTime"));
//        LocalDateTime endDate = LocalDateTimeUtils.strToLocalDateTime(endTime);
//        endDate = endDate.plusHours(1);
//        shardingQuery.getParams().put("endTime", endDate.format(dateTimeFormat));
//        List<ShardingMonth> =
        return null;
    }

    /**
     * 月表-查询指定时间段内数据
     * <pre>
     * continuity-change-day-day 连续值+变化值+月维度
     * </pre>
     *
     * @param shardingQuery 查询条件
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityChangeMonthDay(ShardingQuery shardingQuery){
        return shardingMonthMapper.continuityChangeMonthDay(shardingQuery);
    }

    /**
     * 月表-查询指定时间段内数据
     * <pre>
     * continuity-change-day-day 连续值+变化值+年维度
     * </pre>
     *
     * @param shardingQuery 查询条件
     * @return 最新数据
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityChangeYearDay(ShardingQuery shardingQuery){
        return shardingMonthMapper.continuityChangeYearDay(shardingQuery);
    }

    /**
     * 峰平谷数据 - 月（日）维度
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityFengGuMonthDay(ShardingQuery shardingQuery) {
        return shardingMonthMapper.continuityFengGuMonthDay(shardingQuery);
    }

    /**
     * 峰平谷数据 - 年（月）维度
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShardingMonth> continuityFengGuYearDay(ShardingQuery shardingQuery) {
        return shardingMonthMapper.continuityFengGuYearDay(shardingQuery);
    }

    /**
     * 获取今年年度数据
     */
    @Override
    public Float selectTotalDataForYear(ShardingQuery shardingQuery) {
        return shardingMonthMapper.selectTotalDataForYear(shardingQuery);
    }
}
