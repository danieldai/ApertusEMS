package com.eltvpp.datav.service.impl;

import com.alibaba.fastjson2.JSON;
import com.eltvpp.common.core.constant.CacheConstants;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.enums.vo.EnumSOTVO;
import com.eltvpp.common.core.exception.ServiceException;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.EchartsUtil;
import com.eltvpp.common.core.utils.FormatUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.common.datasource.annotation.ShardingDataSource;
import com.eltvpp.common.redis.service.RedisService;
import com.eltvpp.datav.domain.ShardingMonth;
import com.eltvpp.datav.domain.ShardingQuery;
import com.eltvpp.datav.domain.dto.ChartConfigDto;
import com.eltvpp.datav.domain.vo.ChartCardInfoVo;
import com.eltvpp.datav.domain.vo.ChartQueryVo;
import com.eltvpp.datav.domain.vo.echarts.EchartsAxisVo;
import com.eltvpp.datav.domain.vo.echarts.EchartsOption;
import com.eltvpp.datav.domain.vo.echarts.EchartsSeriesVo;
import com.eltvpp.datav.domain.vo.report.TableHeader;
import com.eltvpp.datav.enums.*;
import com.eltvpp.datav.service.CommonChartService;
import com.eltvpp.datav.service.ShardingCommonService;
import com.eltvpp.datav.utils.EnergyConverter;
import com.eltvpp.datav.utils.GenerateTimeUtils;
import com.eltvpp.system.api.*;
import com.eltvpp.system.api.domain.FeignMonitorDeviceVar;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 通用图表服务实现
 *
 * @author: XIAOTONG.CAO
 */
@Service
@ShardingDataSource
public class CommonChartServiceImpl implements CommonChartService {

    @Autowired
    private ShardingCommonService shardingCommonService;

    @Autowired(required = false)
    private RemoteMonitorDeviceService remoteMonitorDeviceService;

    @Autowired(required = false)
    private RemoteCommunicationDeviceService remoteCommunicationDeviceService;

    @Autowired(required = false)
    private RemoteMonitorDeviceVarService remoteMonitorDeviceVarService;

    @Autowired(required = false)
    private RemoteElectricPriceSchemeConfigService electricPriceSchemeConfigService;


    @Autowired(required = false)
    private RemoteSpecialDataVService remoteSpecialDataVService;

    @Autowired
    private RedisService redisService;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //LINE_CHART(1, "折线图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.LINE_CHART.getKey(), this::handleCommonChartData);
        //BAR_CHART(2, "柱状图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.BAR_CHART.getKey(), this::handleCommonChartData);
        //BAR_LINE_CHART(3, "条形图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.BAR_LINE_CHART.getKey(), this::handleCommonChartData);
        //STACKED_CHART(4, "堆叠图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.STACKED_CHART.getKey(), this::handleStackedChartData);
        //AREA_CHART(5, "面积图"),
        CHART_TYPE_MAP.put(SysChartTypeEnum.AREA_CHART.getKey(), this::handleCommonChartData);
        //PIE_CHART(6, "饼图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.PIE_CHART.getKey(), this::handlePieChartData);
        //K_LINE_CHART(7, "K线图"),todo
        CHART_TYPE_MAP.put(SysChartTypeEnum.K_LINE_CHART.getKey(), this::handleKLineChartData);
        //DROP_CHART(8, "水滴图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.DROP_CHART.getKey(), this::handleDashboardChartData);
        //DASHBOARD(9, "仪表盘"),
        CHART_TYPE_MAP.put(SysChartTypeEnum.DASHBOARD.getKey(), this::handleDashboardChartData);
        //SANKEY_CHART(10, "桑基图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.SANKEY_CHART.getKey(), this::handleSankeyChartData);
        //SEGMENTED_CHART(11, "分段图")
        CHART_TYPE_MAP.put(SysChartTypeEnum.SEGMENTED_CHART.getKey(), this::handleSegmentedChartData);
        //RANKING_CHART(12, "排行图")o
        CHART_TYPE_MAP.put(SysChartTypeEnum.RANKING_CHART.getKey(), this::handleRankingChartData);
        //STATUS_VALUE(13,"状态量")
        CHART_TYPE_MAP.put(SysChartTypeEnum.STATUS_VALUE.getKey(), this::handleStatusChartData);
        //SINGLE_VALUE(14, "单值"),
        CHART_TYPE_MAP.put(SysChartTypeEnum.SINGLE_VALUE.getKey(), this::handleSingleChartData);
        //ALARM_LIST(15, "报警列表"),
        CHART_TYPE_MAP.put(SysChartTypeEnum.ALARM_LIST.getKey(), this::handleCommonTable);
        //WORK_ORDER_LIST(16, "工单列表"),todo
        CHART_TYPE_MAP.put(SysChartTypeEnum.WORK_ORDER_LIST.getKey(), this::handleCommonTable);
        //ALARM_STATISTICS(17, "报警统计"),todo
        CHART_TYPE_MAP.put(SysChartTypeEnum.ALARM_STATISTICS.getKey(), this::handleCommonStatic);
        //WORK_ORDER_STATISTICS(18, "工单统计"),todo
        CHART_TYPE_MAP.put(SysChartTypeEnum.WORK_ORDER_STATISTICS.getKey(), this::handleCommonStatic);
        //POLAR_COORDINATE_CHART(19, "极坐标图"),todo
        //POLAR_STACKED_CHART(20, "极坐标堆叠图");todo
    }

    /**
     * 获取图表数据
     *
     * @param queryVo 查询参数
     * @return 对象
     **/
    @Override
    public Object handleChartData(ChartQueryVo queryVo) {
        //组装时间
        packageTime(queryVo);
        //组装通用变量值
        packageDeviceVar(queryVo);
        //处理图表
        return CHART_TYPE_MAP.get(queryVo.getDashboardConfigCard().getChartType()).apply(queryVo);
    }

    //region 处理通用图表

    /**
     * 处理通用图表
     * <pre>
     * 折线图、柱状图、条形图、面积图
     * </pre>
     */
    private ChartCardInfoVo handleCommonChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            throw new ServiceException("卡片未配置变量");
        }
        //初始图表数据
        ChartCardInfoVo chartCardInfoVo = new ChartCardInfoVo();
        //设置卡片名称
        chartCardInfoVo.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //图表数据
        EchartsOption echartsOption = new EchartsOption();
        //Y轴数据（只能以第一组变量为准）
        List<EchartsAxisVo> yAxisVoList = new ArrayList<>();
        AtomicBoolean hasYAxis = new AtomicBoolean(false);
        AtomicBoolean hasYAxisTwo = new AtomicBoolean(false);
        //图例名称集合
        List<String> legendNameList = new ArrayList<>();
        //单位集合
        List<String> unitList = new ArrayList<>();
        //遍历变量
        chartConfig.getVarList().forEach(item -> {
            if (item.getLegend() == null) {
                item.setLegend(item.getVarName());
            }
            if (StringUtils.isNotEmpty(item.getVarSn())) {
                //获取主数据
                EchartsSeriesVo seriesVo = handleCommonChartComputePercentData(chartQueryVo, item, false, false, new ArrayList<>());

                //面积图图需要增加区域属性
                if (SysChartTypeEnum.AREA_CHART.getKey().equals(chartQueryVo.getDashboardConfigCard().getChartType())) {
                    seriesVo.setType(SysChartTypeEnum.LINE_CHART.getType());
                    seriesVo.setAreaStyle(new HashMap<>());
                }
                echartsOption.getSeries().add(seriesVo);
                legendNameList.add(seriesVo.getName());

                //添加y轴数据
                boolean isBarLineChart = SysChartTypeEnum.BAR_LINE_CHART.getKey().equals(chartQueryVo.getDashboardConfigCard().getChartType());
                unitList.add(item.getUnit());

                //（只能以第一组变量为准）
                if (!hasYAxis.get()) {
                    yAxisVoList.add(new EchartsAxisVo("value", null, item.getUnit(), isBarLineChart ? "top" : "left", false, false));
                    hasYAxis.set(true);
                }
                if ((item.getChain() || item.getYoy()) && !hasYAxisTwo.get()) {
                    yAxisVoList.add(new EchartsAxisVo("value", null, "%", isBarLineChart ? "bottom" : "right", false, false));
                    hasYAxisTwo.set(true);
                }
                //环比
                if (item.getChain()) {
                    EchartsSeriesVo chainSeriesVo = handleCommonChartComputePercentData(chartQueryVo, item, true, false, seriesVo.getData());
                    echartsOption.getSeries().add(chainSeriesVo);
                    legendNameList.add(chainSeriesVo.getName());
                    unitList.add("%");
                }

                //同比
                if (item.getYoy()) {
                    EchartsSeriesVo yoySeriesVo = handleCommonChartComputePercentData(chartQueryVo, item, false, true, seriesVo.getData());
                    echartsOption.getSeries().add(yoySeriesVo);
                    legendNameList.add(yoySeriesVo.getName());
                    unitList.add("%");
                }
                //显示类型（1数值 2百分比 3比值）计算比值
                if (item.getPercentage() != null && item.getPercentage() > 1 && item.getBaseValue() != null) {
                    List<Float> newData = new ArrayList<>();
                    if (DataVPercentageEnum.PERCENTAGE.getKey().equals(item.getPercentage())) {
                        seriesVo.getData().forEach(i -> newData.add(FormatUtils.calculateRatioPercent(i, item.getBaseValue())));
                    } else {
                        seriesVo.getData().forEach(i -> newData.add(FormatUtils.calculateRatio(i, item.getBaseValue())));
                    }
                    if (StringUtils.isNotEmpty(newData)) {
                        seriesVo.setData(newData);
                    }
                }

            }
        });
        echartsOption.setYAxis(yAxisVoList);
        echartsOption.setUnitList(unitList);
        chartCardInfoVo.setEchartsOption(echartsOption);
        //处理辅助线
        handleAuxiliaryLine(chartQueryVo, chartCardInfoVo.getEchartsOption());
        //处理图例位置
        handleLegendPosition(chartQueryVo, chartCardInfoVo.getEchartsOption(), legendNameList);
        //处理X轴
        handleXAxis(chartQueryVo, chartCardInfoVo.getEchartsOption());

        if (SysChartTypeEnum.BAR_LINE_CHART.getKey().equals(chartQueryVo.getDashboardConfigCard().getChartType())) {
            List<EchartsAxisVo> xAxis = chartCardInfoVo.getEchartsOption().getXAxis();
            List<EchartsAxisVo> yAxis = chartCardInfoVo.getEchartsOption().getYAxis();
            chartCardInfoVo.getEchartsOption().setXAxis(yAxis);
            chartCardInfoVo.getEchartsOption().setYAxis(xAxis);
        }
        //处理右数据区
        handleCommonChartRightData(chartQueryVo, chartCardInfoVo);
        handleTable(chartQueryVo, chartCardInfoVo);
        return chartCardInfoVo;
    }

    private void handleTable(ChartQueryVo chartQueryVo, ChartCardInfoVo chartCardInfoVo) {
        if (chartQueryVo.getIsTable() == 0) {
            return;
        }
        //将数据转成表格
        List<TableHeader> tableHeaders = new ArrayList<>();
        List<Map<String, Object>> tableData = new ArrayList<>();
        tableHeaders.add(new TableHeader("时间", "时间", null));
        Map<String, List<Float>> seriesNameMap = new HashMap<>();
        for (int i = 0; i < chartCardInfoVo.getEchartsOption().getSeries().size(); i++) {
            EchartsSeriesVo item = chartCardInfoVo.getEchartsOption().getSeries().get(i);
            if(item.getName().equals("辅助线")){
                continue;
            }
            String name = String.valueOf(item.getName().hashCode());
            tableHeaders.add(new TableHeader(item.getName() + "(" + chartCardInfoVo.getEchartsOption().getUnitList().get(i) + ")", name, null));
            seriesNameMap.put(name, item.getData());
        }

        for (int i = 0; i < chartQueryVo.getTimeList().size(); i++) {
            ChartQueryVo.TimeInfo item = chartQueryVo.getTimeList().get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("时间", item.getKey());
            int finalI = i;
            tableHeaders.forEach(header -> {
                if (!Objects.equals(header.getDataIndex(), "时间")) {
                    if (seriesNameMap.containsKey(header.getDataIndex())) {
                        map.put(header.getDataIndex(), seriesNameMap.get(header.getDataIndex()).get(finalI));
                    }
                }
            });
            tableData.add(map);
        }
        Map<String, Object> tableMap = new HashMap<>();
        tableMap.put("header", tableHeaders);
        tableMap.put("tableData", tableData);
        chartCardInfoVo.setTableInfo(tableMap);
    }

    /**
     * 处理通用图表右数据去数据
     */
    private void handleCommonChartRightData(ChartQueryVo chartQueryVo, ChartCardInfoVo chartCardInfoVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (ObjectUtils.isEmpty(chartConfig.getChildren())) {
            return;
        }
        List<ChartCardInfoVo.SingleInfo> singleRightList = new ArrayList<>();
        chartConfig.getChildren().forEach(item -> {
            //按照单值的方式去处理
            singleRightList.add(getSingleInfo(chartQueryVo, item));
        });
        chartCardInfoVo.setSingleRightList(singleRightList);
    }

    //endregion

    //region 处理堆叠图表

    /**
     * 处理堆叠图表
     */
    private ChartCardInfoVo handleStackedChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            throw new ServiceException("卡片未配置变量");
        }

        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //判断变量是否为空
        if (ObjectUtils.isEmpty(chartConfig.getVarList())) {
            return result;
        }
//        //单值只有一个变量
//        ChartConfigDto.Var varInfo = chartConfig.getVarList().get(0);
//        if (StringUtils.isBlank(varInfo.getVarSn())) {
//            throw new ServiceException("卡片配置的变量信息错误");
//        }
//        if (StringUtils.isEmpty(varInfo.getLegend())) {
//            varInfo.setLegend(varInfo.getVarName());
//        }
        //定义echarts图表
        EchartsOption echartsOption = new EchartsOption();
        result.setEchartsOption(echartsOption);
        //图例名称集合
        List<String> legendNameList = new ArrayList<>();
        //单位集合
        List<String> unitList = new ArrayList<>();
        //是否有第一个Y轴
        AtomicBoolean hasYAxis = new AtomicBoolean(false);
        //处理Y轴
        List<EchartsAxisVo> yAxisVoList = new ArrayList<>();
        List<EchartsSeriesVo> series = new ArrayList<>();
        chartConfig.getVarList().forEach(varInfo -> {
            if (StringUtils.isEmpty(varInfo.getLegend())) {
                varInfo.setLegend(varInfo.getVarName());
            }
            //获取时间集合
            List<ChartQueryVo.TimeInfo> timeList = chartQueryVo.getTimeList();

            //按日查询是折线图的电量
            if (DatavDateDimEnum.DAY.getKey().equals(chartQueryVo.getDashboardConfigCard().getDateDim())) {
                varInfo.setStorageType(1);
                varInfo.setChangeType(1);
                //todo
//                chartQueryVo.getDashboardConfigCard().setSecondDateDim(DatavDateDimSecondEnum.HOUR_1.getKey());
                chartQueryVo.getDashboardConfigCard().setSecondDateDim(DatavDateDimSecondEnum.MINUTE_5.getKey());
            } else {
                //设置为峰平谷
                varInfo.setStorageType(3);
            }
            //获取key
            String key = getShardingKey(chartQueryVo, varInfo);
            //查询选择的时间数据
            ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
            //todo
            //query.getParams().put("hour", "0");
            query.getParams().put("minute", "5");
            query.setIsCharge(varInfo.getShowCharge());
            List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
            if (StringUtils.isNotEmpty(monthList)) {
                String unit = varInfo.getShowCharge() ? "元" : varInfo.getUnit();
                unitList.add(unit);
                if (!hasYAxis.get()) {
                    hasYAxis.set(false);
                    yAxisVoList.add(new EchartsAxisVo("value", null, unit, "left", false, false));
                }
                //按日查询是折线图的电量
                if (DatavDateDimEnum.DAY.getKey().equals(chartQueryVo.getDashboardConfigCard().getDateDim())) {
                    //依次补充值 数据库中的值肯会中断，中断的值默认是0
                    Map<String, Float> shardingMonthMap = monthList.stream()
                            .collect(Collectors.toMap(ShardingMonth::getFormattedDatetime, ShardingMonth::getDataValue));
                    //获取时间集合内数据
                    List<Float> seriesData = timeList.stream().map(ChartQueryVo.TimeInfo::getValue).collect(Collectors.toList());
                    for (int i = 0; i < timeList.size(); i++) {
                        ChartQueryVo.TimeInfo timeInfo = timeList.get(i);
                        if (shardingMonthMap.containsKey(timeInfo.getKey())) {
                            if (seriesData.size() >= i) {
                                seriesData.set(i, shardingMonthMap.get(timeInfo.getKey()));
                            }
                        }
                    }
                    legendNameList.add(varInfo.getLegend());
                    series.add(new EchartsSeriesVo(varInfo.getLegend(), SysChartTypeEnum.LINE_CHART.getType(), seriesData, null));

                } else {
                    //尖 2024-06-01 50, 2024-06-02 50,2024-06-10 50
                    //峰 2024-06-01 50, 2024-06-02 50,2024-06-10 50
                    //平 2024-06-01 50, 2024-06-02 50,2024-06-10 50
                    //谷 2024-06-01 50, 2024-06-02 50,2024-06-10 50
                    //按照尖峰平谷标识分组 组装数据
                    Map<String, List<ShardingMonth>> seasonalTypeMap = monthList.stream()
                            .collect(Collectors.groupingBy(ShardingMonth::getSeasonalTypeName));

                    for (Map.Entry<String, List<ShardingMonth>> entry : seasonalTypeMap.entrySet()) {
                        String typeKey = entry.getKey();
                        if (StringUtils.isEmpty(typeKey)) {
                            continue;
                        }
                        unitList.add(unit);
                        legendNameList.add(varInfo.getLegend() + "-" + typeKey);
                        //依次补充值 数据库中的值肯会中断，中断的值默认是0
                        List<ShardingMonth> list = entry.getValue();
                        Map<String, Float> shardingMonthMap = list.stream()
                                .collect(Collectors.toMap(ShardingMonth::getFormattedDatetime, ShardingMonth::getDataValue));
                        //获取时间集合内数据
                        List<Float> seriesData = timeList.stream().map(ChartQueryVo.TimeInfo::getValue).collect(Collectors.toList());
                        for (int i = 0; i < timeList.size(); i++) {
                            ChartQueryVo.TimeInfo timeInfo = timeList.get(i);
                            if (shardingMonthMap.containsKey(timeInfo.getKey())) {
                                if (seriesData.size() >= i) {
                                    seriesData.set(i, shardingMonthMap.get(timeInfo.getKey()));
                                }
                            }
                        }
                        series.add(new EchartsSeriesVo(varInfo.getLegend() + "-" + typeKey, SysChartTypeEnum.BAR_CHART.getType(), seriesData, null, varInfo.getVarSn()));
                    }

                }
            }

        });
        //处理X轴
        handleXAxis(chartQueryVo, echartsOption);
        echartsOption.setSeries(series);
        result.getEchartsOption().setYAxis(yAxisVoList);
        echartsOption.setUnitList(unitList);
        //处理图例位置
        handleLegendPosition(chartQueryVo, result.getEchartsOption(), legendNameList);
        return result;
    }
    //endregion

    //region 处理饼图

    /**
     * 处理饼图
     */
    private ChartCardInfoVo handlePieChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            throw new ServiceException("卡片未配置变量");
        }
        //初始化数据
        ChartCardInfoVo chartCardInfoVo = new ChartCardInfoVo();
        //卡片名称
        chartCardInfoVo.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());

        //echarts信息
        EchartsOption echartsOption = getPieChartOption(chartQueryVo, 1);
        chartCardInfoVo.setEchartsOption(echartsOption);

        //兼容大屏页面 单独处理
        if (chartQueryVo.getDashboardConfigCard().getPageType() == 1) {
            List<EchartsOption> echartsOptionList = new ArrayList<>();
            echartsOptionList.add(echartsOption);
            ChartConfigDto.Var varInfo = chartConfig.getVarList().get(0);
            if (varInfo.getChain()) {
                echartsOptionList.add(getPieChartOption(chartQueryVo, 2));
            }
            if (varInfo.getYoy()) {
                echartsOptionList.add(getPieChartOption(chartQueryVo, 3));
            }
            chartCardInfoVo.setEchartsOptionList(echartsOptionList);
        }
        return chartCardInfoVo;
    }

    /**
     * 获取饼图option配置
     *
     * @param chartQueryVo 查询参数
     * @param type         1、正常值、2环比值 3、同比值
     */
    private EchartsOption getPieChartOption(ChartQueryVo chartQueryVo, Integer type) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();

        EchartsOption echartsOption = new EchartsOption();
        //饼图内容
        EchartsSeriesVo seriesVo = new EchartsSeriesVo();
        seriesVo.setType(SysChartTypeEnum.PIE_CHART.getType());
        seriesVo.setName(chartQueryVo.getDashboardConfigCard().getCardName());
        echartsOption.getSeries().add(seriesVo);
        //饼图数据
        List<EchartsSeriesVo.ObjDataInfo> objDataInfoList = new ArrayList<>();
        ChartConfigDto.Var firstVarInfo = null;
        for (int i = 0; i < chartConfig.getVarList().size(); i++) {
            ChartConfigDto.Var varInfo = chartConfig.getVarList().get(i);
            if (StringUtils.isEmpty(varInfo.getLegend())) {
                varInfo.setLegend(varInfo.getVarName());
            }
            if (i == 0) {
                firstVarInfo = varInfo;
            } else {
                handleNullValue(varInfo, firstVarInfo);
            }
            //获取key
            String key = getShardingKey(chartQueryVo, varInfo);
            ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
            //查询选择的时间数据
            switch (type) {
                case 2:
                    varInfo.setLegend(varInfo.getLegend() + "-环比");
                    //环比
                    query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getChainBeginTime(), chartQueryVo.getChainEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
                    break;
                case 3:
                    //同比
                    varInfo.setLegend(varInfo.getLegend() + "-同比");
                    query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getYoyBeginTime(), chartQueryVo.getYoyEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
                    break;
            }
            List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
            if (StringUtils.isNotEmpty(monthList) && StringUtils.isNotNull(monthList.get(0).getDataValue())) {
                objDataInfoList.add(new EchartsSeriesVo.ObjDataInfo(varInfo.getLegend(), monthList.get(0).getDataValue()));
            }
        }
        seriesVo.setObjData(objDataInfoList);
        return echartsOption;
    }


    private void handleNullValue(ChartConfigDto.Var target, ChartConfigDto.Var source) {
        if (target.getChangeType() == null) {
            target.setChangeType(source.getChangeType());
        }
        if (target.getStorageType() == null) {
            target.setStorageType(source.getStorageType());
        }
    }
    //endregion

    //region 处理K线图

    /**
     * 处理K线图
     */
    private ChartCardInfoVo handleKLineChartData(ChartQueryVo configVo) {
        return null;
    }
    //endregion

    //region 处理X轴

    /**
     * 处理X轴
     */
    private void handleXAxis(ChartQueryVo queryVo, EchartsOption echartsOption) {
        List<String> data = new ArrayList<>();
        queryVo.getTimeList().forEach(item -> {
            data.add(item.getTitle());
        });
        EchartsAxisVo xAxisVo = new EchartsAxisVo("category", data, null, null, true, true);
        List<EchartsAxisVo> xAxisVoList = new ArrayList<>();
        xAxisVoList.add(xAxisVo);
        echartsOption.setXAxis(xAxisVoList);
    }
    //endregion

    //region 处理辅助线

    /**
     * 处理K线图
     */
    private void handleAuxiliaryLine(ChartQueryVo queryVo, EchartsOption echartsOption) {
        ChartConfigDto chartConfig = queryVo.getChartConfig();
        //开启辅助线
        if (chartConfig.getIsShowLine() && StringUtils.isNotEmpty(chartConfig.getLineChildren())) {
            EchartsSeriesVo seriesVo = new EchartsSeriesVo();
            seriesVo.setName("辅助线");
            seriesVo.setType("line");
            List<EchartsSeriesVo.MarkLineData> markLineData = new ArrayList<>();
            chartConfig.getLineChildren().forEach(item -> {
                if (StringUtils.isNotNull(item.getLineDirection()) && item.getLineDirection() > 0) {
                    String xAxis = null;
                    String yAxis = null;
                    String formatter = item.getLineName();
                    //辅助线方向（0无 1横线 2竖线）
                    if (item.getLineDirection() == 1) {
                        yAxis = item.getLineValue();
                    } else {
                        xAxis = item.getLineValue();
                    }
                    markLineData.add(new EchartsSeriesVo.MarkLineData(xAxis, yAxis, formatter));
                }
            });
            seriesVo.setMarkLine(new EchartsSeriesVo.MarkLine(markLineData));
            echartsOption.getSeries().add(seriesVo);
        }
    }
    //endregion

    //region 处理图例位置

    /**
     * 处理图例位置
     * <pre>
     * 左上 右上 上中 下中
     * </pre>
     */
    private void handleLegendPosition(ChartQueryVo configVo, EchartsOption echartsOption, List<String> legendNameList) {
        Map<String, Object> position = EchartsUtil.getLegendPosition(configVo.getChartConfig().getLegendPosition());
        position.put("data", legendNameList);
        position.put("type", "scroll");
        echartsOption.setLegend(position);
    }
    //endregion

    //region 处理单值

    /**
     * 处理单值
     */
    public ChartCardInfoVo handleSingleChartData(ChartQueryVo chartQueryVo) {
        //初始化数据
        ChartCardInfoVo chartCardInfoVo = new ChartCardInfoVo();
        //设置卡片名称
        chartCardInfoVo.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());

        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            return chartCardInfoVo;
        }
        //单值只有一个变量
        ChartConfigDto.Var varInfo = chartConfig.getVarList().get(0);
        if (StringUtils.isBlank(varInfo.getVarSn())) {
            return chartCardInfoVo;
        }


        //设置单值数据
        chartCardInfoVo.setSingleInfo(getSingleInfo(chartQueryVo, varInfo));
        //兼容大屏 设置右侧区域
        if (chartQueryVo.getDashboardConfigCard().getPageType() == 1) {
            List<ChartCardInfoVo.SingleInfo> list = new ArrayList<>();
            chartConfig.getChildren().forEach(item -> {
                list.add(getSingleInfo(chartQueryVo, item));
            });
            chartCardInfoVo.setSingleRightList(list);
        }
        return chartCardInfoVo;
    }

    /**
     * 获取单值信息
     */
    private ChartCardInfoVo.SingleInfo getSingleInfo(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo) {
        if (StringUtils.isEmpty(varInfo.getLegend())) {
            varInfo.setLegend(varInfo.getVarName());
        }
        ChartCardInfoVo.SingleInfo singleInfo = new ChartCardInfoVo.SingleInfo(varInfo.getLegend(), varInfo.getVarSn(), varInfo.getUnit(), varInfo.getIcon(), varInfo.getIconColor(), varInfo.getBackgroundColor());

        //获取key
        String key = getShardingKey(chartQueryVo, varInfo);
        //查询选择的时间数据
        ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
        List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
        if (StringUtils.isNotEmpty(monthList) && StringUtils.isNotNull(monthList.get(0).getDataValue())) {
            singleInfo.setSingleValue(monthList.get(0).getDataValue());
        }

        //获取环比数据
        if (varInfo.getChain()) {
            query.setParams(DateUtils.dateToParamForDay(GenerateTimeUtils.generateChainTime(chartQueryVo.getBeginTime()), GenerateTimeUtils.generateChainTime(chartQueryVo.getEndTime())));
            //查询环比数据
            List<ShardingMonth> chainMonthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
            singleInfo.setChain(true);
            if (StringUtils.isNotEmpty(chainMonthList) && StringUtils.isNotNull(chainMonthList.get(0).getDataValue())) {
                singleInfo.setChainValue(chainMonthList.get(0).getDataValue());
            }
            singleInfo.setChainRatio(FormatUtils.computePercent(singleInfo.getSingleValue(), singleInfo.getChainValue()));
        }

        //获取同比数据
        if (varInfo.getYoy()) {
            query.setParams(DateUtils.dateToParamForDay(GenerateTimeUtils.generateYoyTime(chartQueryVo.getBeginTime(), DatavDateDimEnum.DAY), GenerateTimeUtils.generateYoyTime(chartQueryVo.getEndTime(), DatavDateDimEnum.DAY)));
            //查询同比数据
            List<ShardingMonth> yoyMonthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
            singleInfo.setYoy(true);
            if (StringUtils.isNotEmpty(yoyMonthList) && StringUtils.isNotNull(yoyMonthList.get(0).getDataValue())) {
                singleInfo.setYoyValue(yoyMonthList.get(0).getDataValue());
            }
            singleInfo.setYoyRatio(FormatUtils.computePercent(singleInfo.getSingleValue(), singleInfo.getYoyValue()));
        }

        //显示类型（1数值 2百分比 3比值）计算比值
        if (varInfo.getPercentage() != null && varInfo.getPercentage() > 1 && varInfo.getBaseValue() != null) {
            //百分比
            if (DataVPercentageEnum.PERCENTAGE.getKey().equals(varInfo.getPercentage())) {
                singleInfo.setSingleValue(FormatUtils.calculateRatioPercent(singleInfo.getSingleValue(), varInfo.getBaseValue()));
            } else {
                singleInfo.setSingleValue(FormatUtils.calculateRatio(singleInfo.getSingleValue(), varInfo.getBaseValue()));
            }
        } else {
            EnergyConverter.convertEnergy(singleInfo);
        }
        return singleInfo;
    }
    //endregion

    //region 组装时间数据

    /**
     * 组装时间数据
     * <pre>
     *     beginTime 开始时间
     *     endTime   结束时间
     *
     * </pre>
     *
     * @param configVo 查询参数
     **/
    private void packageTime(ChartQueryVo configVo) {
        String beginTime = "";
        String endTime = "";
        boolean isFullDate = StringUtils.isNull(configVo.getDashboardConfigCard().getIsFullDate()) || configVo.getDashboardConfigCard().getIsFullDate().equals(1);

        //格式化【日】时间 00:05～24:00 00:10～24:00 00:15～24:00 00:30～24:00 01:00～24:00
        if (StringUtils.isNotEmpty(configVo.getDayTime())) {
            configVo.getDashboardConfigCard().setDateDim(DatavDateDimEnum.DAY.getKey());
            beginTime = DateUtils.completionDayTime(configVo.getDayTime(), true);
            endTime = DateUtils.completionDayTime(configVo.getDayTime(), false, isFullDate);
            //防止配错的情况，按日查询的只能是 5分钟 10分钟 15分钟 30分钟 1时
            Integer dateDim = configVo.getDashboardConfigCard().getSecondDateDim();
            if (SysChartTypeEnum.STACKED_CHART.getKey().equals(configVo.getDashboardConfigCard().getChartType())) {
                dateDim = DatavDateDimSecondEnum.MINUTE_5.getKey();
            }
            configVo.setTimeList(GenerateTimeUtils.generateMinuteHourSlots(beginTime, endTime, dateDim, isFullDate));

            //设置环比时间
            configVo.setChainBeginTime(GenerateTimeUtils.generateChainTime(beginTime, DatavDateDimEnum.DAY));
            configVo.setChainEndTime(GenerateTimeUtils.generateChainTime(endTime, DatavDateDimEnum.DAY));
            configVo.setChainTimeList(GenerateTimeUtils.generateMinuteHourSlots(configVo.getChainBeginTime(), configVo.getChainEndTime(), dateDim, isFullDate));
            //设置同比时间
            configVo.setYoyBeginTime(GenerateTimeUtils.generateYoyTime(beginTime, DatavDateDimEnum.DAY));
            configVo.setYoyEndTime(GenerateTimeUtils.generateYoyTime(endTime, DatavDateDimEnum.DAY));
            configVo.setYoyTimeList(GenerateTimeUtils.generateMinuteHourSlots(configVo.getYoyBeginTime(), configVo.getYoyEndTime(), dateDim, isFullDate));
        }
        // 格式化【月】时间 1号～30号/31号
        if (StringUtils.isNotEmpty(configVo.getMonthTime())) {
            configVo.getDashboardConfigCard().setDateDim(DatavDateDimEnum.MONTH.getKey());
            beginTime = DateUtils.completionMonthTime(configVo.getMonthTime(), true);
            endTime = DateUtils.completionMonthTime(configVo.getMonthTime(), false, isFullDate);
            configVo.setTimeList(GenerateTimeUtils.generateMonthlySlots(beginTime, endTime, isFullDate));

            //设置环比时间
            configVo.setChainBeginTime(GenerateTimeUtils.generateChainTime(beginTime, DatavDateDimEnum.MONTH));
            configVo.setChainEndTime(GenerateTimeUtils.generateChainTime(endTime, DatavDateDimEnum.MONTH));
            configVo.setChainTimeList(GenerateTimeUtils.generateMonthlySlots(configVo.getChainBeginTime(), configVo.getChainEndTime(), isFullDate));

            //设置同比时间
            configVo.setYoyBeginTime(GenerateTimeUtils.generateYoyTime(beginTime, DatavDateDimEnum.MONTH));
            configVo.setYoyEndTime(GenerateTimeUtils.generateYoyTime(endTime, DatavDateDimEnum.MONTH));
            configVo.setYoyTimeList(GenerateTimeUtils.generateMonthlySlots(configVo.getYoyBeginTime(), configVo.getYoyEndTime(), isFullDate));
        }

        // 格式化【年】时间 1月～12月
        if (StringUtils.isNotEmpty(configVo.getYearTime())) {
            configVo.getDashboardConfigCard().setDateDim(DatavDateDimEnum.YEAR.getKey());
            beginTime = DateUtils.completionYearTime(configVo.getYearTime(), true);
            endTime = DateUtils.completionYearTime(configVo.getYearTime(), false, isFullDate);
            configVo.setTimeList(GenerateTimeUtils.generateYearlySlots(beginTime, endTime, isFullDate));

            //设置环比时间
            configVo.setChainBeginTime(GenerateTimeUtils.generateChainTime(beginTime, DatavDateDimEnum.YEAR));
            configVo.setChainEndTime(GenerateTimeUtils.generateChainTime(endTime, DatavDateDimEnum.YEAR));
            configVo.setChainTimeList(GenerateTimeUtils.generateYearlySlots(configVo.getChainBeginTime(), configVo.getChainEndTime(), isFullDate));

            //设置同比时间
            configVo.setYoyBeginTime(GenerateTimeUtils.generateYoyTime(beginTime, DatavDateDimEnum.YEAR));
            configVo.setYoyEndTime(GenerateTimeUtils.generateYoyTime(endTime, DatavDateDimEnum.YEAR));
            configVo.setYoyTimeList(GenerateTimeUtils.generateYearlySlots(configVo.getYoyBeginTime(), configVo.getYoyEndTime(), isFullDate));

        }
        configVo.setBeginTime(beginTime);
        configVo.setEndTime(endTime);
    }

    /**
     * 重新计算日时间
     */
    private void packageRecalculateTime(ChartQueryVo configVo) {
        String beginTime = DateUtils.getPastMinutes(configVo.getBeginTime(), -configVo.getDashboardConfigCard().getSecondDateDim());
        String endTime = configVo.getEndTime();

        boolean isFullDate = StringUtils.isNull(configVo.getDashboardConfigCard().getIsFullDate()) || configVo.getDashboardConfigCard().getIsFullDate().equals(1);

        Integer dateDim = configVo.getDashboardConfigCard().getSecondDateDim();
        configVo.setTimeList(GenerateTimeUtils.generateMinuteHourSlots(beginTime, endTime, dateDim, isFullDate));

        //设置环比时间
        configVo.setChainBeginTime(GenerateTimeUtils.generateChainTime(beginTime, DatavDateDimEnum.DAY));
        configVo.setChainEndTime(GenerateTimeUtils.generateChainTime(endTime, DatavDateDimEnum.DAY));
        configVo.setChainTimeList(GenerateTimeUtils.generateMinuteHourSlots(configVo.getChainBeginTime(), configVo.getChainEndTime(), dateDim, isFullDate));

        //设置同比时间
        configVo.setYoyBeginTime(GenerateTimeUtils.generateYoyTime(beginTime, DatavDateDimEnum.DAY));
        configVo.setYoyEndTime(GenerateTimeUtils.generateYoyTime(endTime, DatavDateDimEnum.DAY));
        configVo.setYoyTimeList(GenerateTimeUtils.generateMinuteHourSlots(configVo.getYoyBeginTime(), configVo.getYoyEndTime(), dateDim, isFullDate));

        configVo.setBeginTime(beginTime);
        configVo.setEndTime(endTime);
    }

    //endregion

    //region 组装通用设备变量
    private void packageDeviceVar(ChartQueryVo queryVo) {

        //指定变量1.状态量 2.桑基 3.排行榜 4.饼图 5.水滴 6.仪表盘
        if (SysChartTypeEnum.SpecialVarList.contains(queryVo.getDashboardConfigCard().getChartType())) {
            return;
        }
        //通用变量需要前端传入设备编码
        if (queryVo.getDashboardConfigCard().getIndicator() != 2 || StringUtils.isEmpty(queryVo.getDeviceSn()) && StringUtils.isEmpty(queryVo.getChartConfig().getVarList())) {
            return;
        }
        List<ChartConfigDto.Var> varList = queryVo.getChartConfig().getVarList();
        //获取设备对应的变量
        List<Integer> comVariablelist = varList.stream().map(ChartConfigDto.Var::getComVariable).collect(Collectors.toList());
        if (StringUtils.isEmpty(comVariablelist)) {
            throw new ServiceException("卡片配置错误");
        }
        //查询数据
        FeignMonitorDeviceVar varParam = new FeignMonitorDeviceVar();
        varParam.setDeviceSn(queryVo.getDeviceSn());
        varParam.getParams().put("variableTypes", comVariablelist);
        List<FeignMonitorDeviceVar> deviceVarList = remoteMonitorDeviceVarService.getListInner(varParam, SecurityConstants.INNER);
        if (StringUtils.isEmpty(deviceVarList)) {
            throw new ServiceException("未找到对应变量");
        }

        //按照变量类型分组
        Map<Integer, List<FeignMonitorDeviceVar>> variableTypeMap = deviceVarList.stream()
                .collect(Collectors.groupingBy(FeignMonitorDeviceVar::getVariableType));

        List<ChartConfigDto.Var> newVarList = new ArrayList<>();
        varList.forEach(varInfo -> {
            boolean hasKey = variableTypeMap.containsKey(varInfo.getComVariable());
            if (hasKey) {
                String variableTypeName = "-" + SysConfigVariableTypeEnum.fromKey(varInfo.getComVariable());
                List<FeignMonitorDeviceVar> tempVarList = variableTypeMap.get(varInfo.getComVariable());
                tempVarList.forEach(CVarInfo -> {
                    ChartConfigDto.Var tempVar = new ChartConfigDto.Var();
                    BeanUtils.copyProperties(varInfo, tempVar);
                    tempVar.setVarSn(CVarInfo.getVarSn());
                    tempVar.setVarName(CVarInfo.getVarName());
                    tempVar.setLegend(CVarInfo.getVarName() + variableTypeName);
                    tempVar.setUnit(CVarInfo.getUnit());
                    newVarList.add(tempVar);
                });
            }
        });
        queryVo.getChartConfig().setVarList(newVarList);
    }
    //endregion

    //region 获取ShardingKey

    /**
     * 获取ShardingKey
     *
     * @param chartQueryVo 查询参数
     * @param varInfo      变量配置
     */
    private String getShardingKey(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo) {
        return StringUtils.format("valueType:{}|storageType:{}|dateDim:{}", chartQueryVo.getChartConfig().getValueType(), varInfo.getStorageType(), chartQueryVo.getDashboardConfigCard().getDateDim());
    }

    //endregion

    //region 处理通用图表

    /**
     * 处理通用图表
     */
    private EchartsSeriesVo handleCommonChartComputePercentData(ChartQueryVo chartQueryVoSource, ChartConfigDto.Var item, boolean chain, boolean yoy, List<Float> currentData) {
        ChartQueryVo chartQueryVo = new ChartQueryVo();
        BeanUtils.copyProperties(chartQueryVoSource, chartQueryVo);
        boolean isDay = DatavDateDimEnum.DAY.getKey().equals(chartQueryVo.getDashboardConfigCard().getDateDim());
        //连续值+累计值+日维度 需要计算累计值 需要多取一条数据，
        if (isDay && chartQueryVo.getChartConfig().getValueType() == 2 && item.getStorageType() == 2) {
            packageRecalculateTime(chartQueryVo);
        }
        List<ChartQueryVo.TimeInfo> timeList = chartQueryVo.getTimeList();
        String name = item.getLegend();
        //获取key
        String key = getShardingKey(chartQueryVo, item);
        //日期参数
        Map<String, Object> dateToParamForDay = DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime());

        if (chain) {
            name = name + "-环比";
            dateToParamForDay = DateUtils.dateToParamForDay(chartQueryVo.getChainBeginTime(), chartQueryVo.getChainEndTime());
            timeList = chartQueryVo.getChainTimeList();
        }
        if (yoy) {
            name = name + "-同比";
            dateToParamForDay = DateUtils.dateToParamForDay(chartQueryVo.getYoyBeginTime(), chartQueryVo.getYoyEndTime());
            timeList = chartQueryVo.getYoyTimeList();
        }
        List<Float> seriesData = timeList.stream().map(ChartQueryVo.TimeInfo::getValue).collect(Collectors.toList());
        EchartsSeriesVo seriesVo = new EchartsSeriesVo(name, SysChartTypeEnum.getTypeByKey(chartQueryVo.getDashboardConfigCard().getChartType()), seriesData, chain || yoy ? null : item.getColor());
        ShardingQuery query = new ShardingQuery(item.getVarSn(), item.getStorageType(), item.getChangeType(), dateToParamForDay);

        //日期维度（日）的情况下取 5、10 15、30 1时数据
        if (isDay) {
            //小于小时用minute来计算
            if (chartQueryVo.getDashboardConfigCard().getSecondDateDim() < DatavDateDimSecondEnum.HOUR_1.getKey()) {
                query.getParams().put("minute", String.valueOf(chartQueryVo.getDashboardConfigCard().getSecondDateDim()));
            } else {
                query.getParams().put("hour", "0");
            }
        }
        //查询数据库
        List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
        if (StringUtils.isNotEmpty(monthList)) {

            //依次补充值 数据库中的值肯会中断，中断的值默认是0
            Map<String, Float> shardingMonthMap = monthList.stream()
                    .collect(Collectors.toMap(ShardingMonth::getFormattedDatetime, ShardingMonth::getDataValue));
            for (int i = 0; i < timeList.size(); i++) {
                ChartQueryVo.TimeInfo timeInfo = timeList.get(i);
                if (shardingMonthMap.containsKey(timeInfo.getKey())) {
                    if (seriesData.size() >= i) {
                        seriesData.set(i, shardingMonthMap.get(timeInfo.getKey()));
                    }
                }
            }

            //按日 连续值+累计值+日维度 累计值 = 后一条 - 前一条，因此要取出后一天的第一条记录做减法
            if (isDay && chartQueryVo.getChartConfig().getValueType() == 2 && item.getStorageType() == 2) {
                List<Float> newSeriesData = new ArrayList<>();
                for (int i = 1; i < seriesData.size(); i++) {
                    if (seriesData.get(i) == null) {
                        newSeriesData.add(null);
                    } else {
                        newSeriesData.add(FormatUtils.calculateRatio(seriesData.get(i) - seriesData.get(i - 1), 1F));
                    }
                }
                seriesData = newSeriesData;
            } else {

            }
        }
        boolean isBarLineChart = SysChartTypeEnum.BAR_LINE_CHART.getKey().equals(chartQueryVo.getDashboardConfigCard().getChartType());
        int axisIndex = (chain || yoy) ? 1 : 0;
        //计算比值
        if (chain || yoy) {
            seriesData = FormatUtils.computePercents(currentData, seriesData);
            seriesVo.setType(SysChartTypeEnum.LINE_CHART.getType());
        }
        if (isBarLineChart) {
            seriesVo.setXAxisIndex(axisIndex);
        } else {
            seriesVo.setYAxisIndex(axisIndex);
        }
        seriesVo.setData(seriesData);
        return seriesVo;
    }

    //endregion

    //region 处理状态图

    /**
     * 处理状态图
     */
    private ChartCardInfoVo handleStatusChartData(ChartQueryVo chartQueryVo) {
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();

        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //变量校验
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            return result;
        }
        //设置状态数据
        result.setStatusInfo(getStatusInfo(chartQueryVo, chartConfig.getVarList().get(0)));
        //兼容大屏 设置右侧区域
        if (chartQueryVo.getDashboardConfigCard().getPageType() == 1) {
            List<ChartCardInfoVo.StatusInfo> statusList = new ArrayList<>();
            statusList.add(result.getStatusInfo());
            chartConfig.getChildren().forEach(item -> {
                statusList.add(getStatusInfo(chartQueryVo, item));
            });
            result.setStatusRightList(statusList);
        }
        return result;
    }

    /**
     * 处理通讯设备
     */
    private void handleCommunicationDeviceStatus(Long deptId, ChartConfigDto.Var varInfo, ChartCardInfoVo.StatusInfo statusInfo) {
        DatavStateTypeEnum stateTypeEnum = DatavStateTypeEnum.fromKey(varInfo.getStateType());
        if (stateTypeEnum == null) {
            return;
        }
        boolean status = false;
        switch (stateTypeEnum) {
            case COMMUNICATION_STATUS:
                //通讯设备状态
                status = remoteCommunicationDeviceService.statusAll(deptId, SecurityConstants.INNER);
                break;
            case DEVICE_STATUS:
                //监控设备状态
                status = remoteMonitorDeviceService.statusAll(deptId, SecurityConstants.INNER);
                break;
            case MAINS_STATUS:
                //市电状态 todo
                break;
            default:
                break;
        }
        statusInfo.setIcon(varInfo.getIcon());
        statusInfo.setStatus(status);
    }

    /**
     * 处理指定设备变量状态
     *
     * @param varInfo    变量信息
     * @param statusInfo 状态数据
     */
    private void handleMonitorDeviceStatus(ChartConfigDto.Var varInfo, ChartCardInfoVo.StatusInfo statusInfo) {
        //获取指定变量信息
        FeignMonitorDeviceVar deviceVar = remoteMonitorDeviceVarService.getInfoInnerAuth(varInfo.getVarSn(), SecurityConstants.INNER);
        //取状态量
        if (deviceVar == null || deviceVar.getVarType() != 2) {
            return;
        }
        statusInfo.setIcon(varInfo.getIcon());
        statusInfo.setVarSn(varInfo.getVarSn());

        //redis中读取数据
        EnumSOTVO vo = null;
        String json = redisService.getCacheObject(CacheConstants.MONITOR_DEVISE_VARSN_KEY + varInfo.getVarSn());
        if (!StringUtils.isEmpty(json)) {
            vo = JSON.parseObject(json, EnumSOTVO.class);
        }
        if (vo == null) {
            statusInfo.setStatus(false);
        } else {
            statusInfo.setStatus(new BigDecimal(vo.getValue().toString()).compareTo(BigDecimal.ZERO) == 0);
        }
    }

    /**
     * 获取状态数据
     */
    private ChartCardInfoVo.StatusInfo getStatusInfo(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo) {
        ChartCardInfoVo.StatusInfo statusInfo = new ChartCardInfoVo.StatusInfo();

        //数据校验
        if (varInfo.getEquipType() == null) {
            return statusInfo;
        }
        if (DatavEquipTypeEnum.COMMUNICATION_DEVICE.getKey().equals(varInfo.getEquipType())) {
            // 处理通讯相关 通讯、监控、市电
            handleCommunicationDeviceStatus(chartQueryVo.getDashboardConfigCard().getDeptId(), varInfo, statusInfo);
        } else if (DatavEquipTypeEnum.MONITOR_DEVICE.getKey().equals(varInfo.getEquipType())) {
            // 处理指定设备状态
            handleMonitorDeviceStatus(varInfo, statusInfo);
        }
        return statusInfo;
    }

    //endregion

    //region 处理仪表盘

    /**
     * 处理仪表盘
     */
    private ChartCardInfoVo handleDashboardChartData(ChartQueryVo chartQueryVo) {
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        ChartCardInfoVo.DashboardInfo dashboardInfo = new ChartCardInfoVo.DashboardInfo("", "", "");
        result.setDashboardInfo(dashboardInfo);
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            return result;
        }
        //变量校验
        ChartConfigDto.Var varInfo = chartConfig.getVarList().get(0);
        if (StringUtils.isNull(varInfo.getVarSn())) {
            return result;
        }
        //设置主区域
        dashboardInfo = getDashboardChartData(chartQueryVo, varInfo);
        result.setDashboardInfo(dashboardInfo);
        //兼容大屏 设置右侧区域
        if (chartQueryVo.getDashboardConfigCard().getPageType() == 1) {
            List<ChartCardInfoVo.DashboardInfo> rightList = new ArrayList<>();
            rightList.add(result.getDashboardInfo());
            chartConfig.getChildren().forEach(item -> {
                rightList.add(getDashboardChartData(chartQueryVo, item));
            });
            result.setDashboardRightList(rightList);
        }
        return result;
    }

    private ChartCardInfoVo.DashboardInfo getDashboardChartData(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo) {
        if (SysChartTypeEnum.DROP_CHART.getKey().equals(chartQueryVo.getDashboardConfigCard().getChartType())) {
            varInfo.setPercentage(DataVPercentageEnum.RATIO.getKey());
        }
        if (StringUtils.isEmpty(varInfo.getLegend())) {
            varInfo.setLegend(varInfo.getVarName());
        }
        ChartCardInfoVo.DashboardInfo dashboardInfo = new ChartCardInfoVo.DashboardInfo(varInfo.getLegend(), varInfo.getUnit(), varInfo.getVarSn());
        //获取key
        String key = getShardingKey(chartQueryVo, varInfo);
        //查询选择的时间数据
        ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
        List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
        if (StringUtils.isNotEmpty(monthList) && StringUtils.isNotNull(monthList.get(0).getDataValue())) {
            Float value = monthList.get(0).getDataValue();
            if (DataVPercentageEnum.NUMBER.getKey().equals(varInfo.getPercentage())) {
                //数值显示
                dashboardInfo.setMaxValue(varInfo.getMaxValue());
                dashboardInfo.setMinValue(varInfo.getMinValue());
                dashboardInfo.setValue(value);
            } else if (DataVPercentageEnum.PERCENTAGE.getKey().equals(varInfo.getPercentage())) {
                //计算百分比
                dashboardInfo.setMinValue(0f);
                dashboardInfo.setMaxValue(100f);
                dashboardInfo.setValue(FormatUtils.calculateRatioPercent(value, varInfo.getBaseValue()));
            } else if (DataVPercentageEnum.RATIO.getKey().equals(varInfo.getPercentage())) {
                dashboardInfo.setMinValue(0f);
                dashboardInfo.setMaxValue(100f);
                dashboardInfo.setValue(FormatUtils.calculateRatio(value, varInfo.getBaseValue()));
            }
        }
        return dashboardInfo;
    }

    //endregion

    //region 处理排行图
    private ChartCardInfoVo handleRankingChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            throw new ServiceException("卡片未配置变量");
        }
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //判断变量是否为空
        if (ObjectUtils.isEmpty(chartConfig.getVarList())) {
            return result;
        }
        List<ChartCardInfoVo.RankingInfo> rankingList = new ArrayList<>();
        chartConfig.getVarList().forEach(varInfo -> {
            if (StringUtils.isNotEmpty(varInfo.getVarSn())) {
                if (varInfo.getLegend() == null) {
                    varInfo.setLegend(varInfo.getVarSn());
                }
                ChartCardInfoVo.RankingInfo rankingInfo = new ChartCardInfoVo.RankingInfo(varInfo.getLegend(), varInfo.getUnit(), 0F);
                //获取key
                String key = getShardingKey(chartQueryVo, varInfo);
                ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
                //查询数据
                List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
                if (ObjectUtils.isNotEmpty(monthList) && ObjectUtils.isNotEmpty(monthList.get(0))) {
                    Float value = monthList.get(0).getDataValue();
                    //计算比值
                    if (!DataVPercentageEnum.NUMBER.getKey().equals(varInfo.getPercentage())) {
                        value = FormatUtils.calculateRatio(value, varInfo.getBaseValue());
                    }
                    rankingInfo.setValue(value);
                }
                rankingList.add(rankingInfo);
            }
        });

        if (ObjectUtils.isEmpty(rankingList)) {
            return result;
        }

        //1、计算出总和
        float totalValue = (float) rankingList.stream()
                .mapToDouble(ChartCardInfoVo.RankingInfo::getValue)
                .sum();
        //2、循环计算每一项占比
        rankingList.forEach(info -> info.setRatio(FormatUtils.calculateRatio(info.getValue(), totalValue)));
        //3、排序
        List<ChartCardInfoVo.RankingInfo> sortedList = rankingList.stream()
                .sorted(Comparator.comparing(ChartCardInfoVo.RankingInfo::getValue).reversed()
                        .thenComparing(Comparator.comparing(ChartCardInfoVo.RankingInfo::getRatio).reversed()))
                .collect(Collectors.toList());
        //4、更新排序
        IntStream.range(0, sortedList.size())
                .forEachOrdered(i -> sortedList.get(i).setIndex(i + 1));
        rankingList.clear();
        result.setRankingList(sortedList);
        return result;
    }
    //endregion

    //region 处理桑基图

    /**
     * 处理桑基图
     */
    private ChartCardInfoVo handleSankeyChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        if (StringUtils.isEmpty(chartConfig.getVarList())) {
            throw new ServiceException("卡片未配置变量");
        }
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //判断变量是否为空
        if (ObjectUtils.isEmpty(chartConfig.getVarList())) {
            return result;
        }
        ChartCardInfoVo.SankeyInfo sankeyInfo = new ChartCardInfoVo.SankeyInfo();

        //名字数据 每个变量名称 要保证为唯一
        Set<ChartCardInfoVo.SankeyNameInfo> nameData = new HashSet<>();
        List<ChartCardInfoVo.SankeyLinksInfo> links = new ArrayList<>();

        chartConfig.getVarList().forEach(item -> {
            processSankey(chartQueryVo, item, nameData, links);
        });
        sankeyInfo.setData(nameData);
        sankeyInfo.setLinks(links);
        result.setSankeyInfo(sankeyInfo);
        return result;
    }

    private void processSankey(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo, Set<ChartCardInfoVo.SankeyNameInfo> nameList, List<ChartCardInfoVo.SankeyLinksInfo> links) {
        if (varInfo.getVarSn() == null) {
            return;
        }
        if (varInfo.getLegend() == null) {
            varInfo.setLegend(varInfo.getVarSn());
        }
        //添加名字
        nameList.add(new ChartCardInfoVo.SankeyNameInfo(varInfo.getLegend()));
        varInfo.getChildren().forEach(item -> {
            if (item.getLegend() == null) {
                item.setLegend(item.getVarSn());
            }
            //添加名字
            nameList.add(new ChartCardInfoVo.SankeyNameInfo(item.getLegend()));
            links.add(new ChartCardInfoVo.SankeyLinksInfo(varInfo.getLegend(), item.getLegend(), getSankeyInfoByVarSn(chartQueryVo, item)));
            if (ObjectUtils.isNotEmpty(item.getChildren())) {
                processSankey(chartQueryVo, item, nameList, links);
            }
        });
    }

    /**
     * 通过变量编码获取值
     */
    private Float getSankeyInfoByVarSn(ChartQueryVo chartQueryVo, ChartConfigDto.Var varInfo) {
        Float value = 0f;
        //获取key
        String key = getShardingKey(chartQueryVo, varInfo);
        ShardingQuery query = new ShardingQuery(varInfo.getVarSn(), varInfo.getStorageType(), varInfo.getChangeType(), DateUtils.dateToParamForDay(chartQueryVo.getBeginTime(), chartQueryVo.getEndTime()), chartQueryVo.getDashboardConfigCard().getDateDim());
        //查询数据
        List<ShardingMonth> monthList = shardingCommonService.SHARDING_TYPE_MAP.get(key).apply(query);
        if (ObjectUtils.isNotEmpty(monthList) && ObjectUtils.isNotEmpty(monthList.get(0))) {
            value = monthList.get(0).getDataValue();
        }
        return value;
    }
    //endregion

    //region 处理电价

    /**
     * 处理电价
     */
    private ChartCardInfoVo handleSegmentedChartData(ChartQueryVo chartQueryVo) {
        //校验变量数据
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        result.setSchemeMap(electricPriceSchemeConfigService.getSeasonalRangeList(DatavElectricityPriceTypeEnum.COMMUNICATION_DEVICE.getKey().equals(chartConfig.getElectricityPriceType()) ? 0 : chartConfig.getSchemeId(), SecurityConstants.INNER));
        return result;
    }
    //endregion3

    //region 处理公共表格

    private ChartCardInfoVo handleCommonTable(ChartQueryVo chartQueryVo) {
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());

        Map<String, Object> param = new HashMap<>();
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        //1表格
        param.put("dataType", 1);
        param.put("tableType", chartConfig.getTableType());
        param.put("headType", chartConfig.getHeadType());
        param.put("staticType", chartConfig.getStaticType());
        Object object = remoteSpecialDataVService.getSpecialData(param, SecurityConstants.INNER);
        result.setTableInfo(object);
        return result;
    }

    /**
     * 获取>>专用模板【统计】数据
     * <pre>
     *     dataType   数据类型：1表格 2图表
     *     tableType  图表类型：1报警 2工单
     *     staticType 统计类型（1按类型 2按级别）（1按类型 2按日期）
     * </pre>
     *
     * @param chartQueryVo 查询参数
     * @return 结果
     */
    private ChartCardInfoVo handleCommonStatic(ChartQueryVo chartQueryVo) {
        //初始化数据
        ChartCardInfoVo result = new ChartCardInfoVo();
        //卡片名称
        result.setCardName(chartQueryVo.getDashboardConfigCard().getCardName());
        //组装参数
        Map<String, Object> param = new HashMap<>();
        ChartConfigDto chartConfig = chartQueryVo.getChartConfig();
        param.put("dataType", 2);
        param.put("tableType", chartConfig.getTableType());
        param.put("headType", chartConfig.getHeadType());
        param.put("staticType", chartConfig.getStaticType());
        param.put("dayTime", chartQueryVo.getDayTime());
        param.put("monthTime", chartQueryVo.getMonthTime());
        param.put("yearTime", chartQueryVo.getYearTime());
        LinkedHashMap<String, String> dataMap = (LinkedHashMap<String, String>) remoteSpecialDataVService.getSpecialData(param, SecurityConstants.INNER);

        EchartsOption echartsOption = new EchartsOption();
        result.setEchartsOption(echartsOption);
        if (chartConfig.getStaticType() == 1) {
            //饼图内容
            EchartsSeriesVo seriesVo = new EchartsSeriesVo();
            seriesVo.setType(SysChartTypeEnum.PIE_CHART.getType());
            seriesVo.setName(chartQueryVo.getDashboardConfigCard().getCardName());
            echartsOption.getSeries().add(seriesVo);
            //饼图数据
            List<EchartsSeriesVo.ObjDataInfo> objDataInfoList = new ArrayList<>();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                objDataInfoList.add(new EchartsSeriesVo.ObjDataInfo(entry.getKey(), entry.getValue()));
            }
            seriesVo.setObjData(objDataInfoList);
        } else {
            if (chartConfig.getTableType() == 1) {
                EchartsSeriesVo seriesVo = new EchartsSeriesVo();
                seriesVo.setName(chartQueryVo.getDashboardConfigCard().getCardName());
                seriesVo.setType(SysChartTypeEnum.BAR_CHART.getType());
                List<Float> data = new ArrayList<>();
                List<String> axisNameList = new ArrayList<>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    axisNameList.add(entry.getKey());
                    data.add(Float.parseFloat(String.valueOf(entry.getValue())));
                }
                seriesVo.setData(data);
                seriesVo.setBarMaxWidth("35");
                EchartsAxisVo axisVo = new EchartsAxisVo("category", axisNameList, null, "bottom", true, false);

                List<EchartsAxisVo> xAxis = new ArrayList<>();
                xAxis.add(axisVo);
                List<EchartsAxisVo> yAxis = new ArrayList<>();
                yAxis.add(new EchartsAxisVo("value", null, "个", "left", true, false));
                echartsOption.getSeries().add(seriesVo);
                echartsOption.setXAxis(xAxis);
                echartsOption.setYAxis(yAxis);
                List<String> unitList = new ArrayList<>(Collections.singletonList("个"));
                echartsOption.setUnitList(unitList);
            } else {
                EchartsSeriesVo seriesVo = new EchartsSeriesVo();
                seriesVo.setName(chartQueryVo.getDashboardConfigCard().getCardName());
                seriesVo.setType(SysChartTypeEnum.BAR_CHART.getType());
                List<ChartQueryVo.TimeInfo> timeList = chartQueryVo.getTimeList();
                List<Float> seriesData = timeList.stream().map(ChartQueryVo.TimeInfo::getValue).collect(Collectors.toList());
                //依次补充值 数据库中的值肯会中断，中断的值默认是0
                for (int i = 0; i < timeList.size(); i++) {
                    ChartQueryVo.TimeInfo timeInfo = timeList.get(i);
                    if (dataMap.containsKey(timeInfo.getKey())) {
                        if (seriesData.size() >= i) {
                            seriesData.set(i, Float.parseFloat(String.valueOf(dataMap.get(timeInfo.getKey()))));
                        }
                    }
                }
                seriesVo.setData(seriesData);
                handleXAxis(chartQueryVo, echartsOption);
                //处理Y轴
                List<EchartsAxisVo> yAxis = new ArrayList<>();
                yAxis.add(new EchartsAxisVo("value", null, "个", "left", true, false));
                echartsOption.getSeries().add(seriesVo);
                echartsOption.setYAxis(yAxis);
                List<String> unitList = new ArrayList<>(Collections.singletonList("个"));
                echartsOption.setUnitList(unitList);
            }
        }
        return result;
    }

    //endregion
}
