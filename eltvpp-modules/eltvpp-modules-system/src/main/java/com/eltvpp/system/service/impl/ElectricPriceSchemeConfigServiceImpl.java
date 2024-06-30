package com.eltvpp.system.service.impl;

import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.system.api.RemoteShardingMonthAccumulateService;
import com.eltvpp.system.api.domain.SysStation;
import com.eltvpp.system.domain.ElectricPriceSchemeConfig;
import com.eltvpp.system.domain.MonitorDeviceVar;
import com.eltvpp.system.domain.jsonvo.SeasonalRangeChartVo;
import com.eltvpp.system.domain.jsonvo.SeasonalRangeVo;
import com.eltvpp.system.mapper.ElectricPriceSchemeConfigMapper;
import com.eltvpp.system.service.IElectricPriceSchemeConfigService;
import com.eltvpp.system.service.IMonitorDeviceVarService;
import com.eltvpp.system.service.IPublicService;
import com.eltvpp.system.service.ISysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 电度电价配置Service业务层处理
 *
 * @author JUNFU.WANG
 * @date 2023-11-02
 */
@Service
public class ElectricPriceSchemeConfigServiceImpl implements IElectricPriceSchemeConfigService {
    @Autowired
    private ElectricPriceSchemeConfigMapper electricPriceSchemeConfigMapper;

    @Autowired
    private RemoteShardingMonthAccumulateService shardingMonthAccumulateService;

    @Autowired
    private IMonitorDeviceVarService deviceVarService;

    @Autowired
    private ISysStationService stationService;

    @Autowired
    private IPublicService publicService;

    /**
     * 查询电度电价配置
     *
     * @param id 电度电价配置主键
     * @return 电度电价配置
     */
    @Override
    public ElectricPriceSchemeConfig selectElectricPriceSchemeConfigById(Long id) {
        return electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigById(id);
    }

    /**
     * 通过电价ID和月份获取符合的配置
     *
     * @param SchemeId 电价标准ID
     * @param month    当前月份
     * @return 电度电价配置
     */
    @Override
    public ElectricPriceSchemeConfig selectElectricPriceSchemeConfigBySchemeId(Long SchemeId, Integer month) {
        return electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigBySchemeId(SchemeId, month);
    }

    /**
     * 通过电价标准ID取出所有的电度电价配置
     *
     * @param schemeId 电价标准ID
     * @return 电度电价配置
     */
    @Override
    public List<ElectricPriceSchemeConfig> selectElectricPriceSchemeBySchemeId(Long schemeId) {
        ElectricPriceSchemeConfig electricPriceSchemeConfig = new ElectricPriceSchemeConfig();
        electricPriceSchemeConfig.setSchemeId(schemeId);
        return electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigList(electricPriceSchemeConfig);
    }

    /**
     * 通过电价标准ID重新计算所有的分时电价
     *
     * @param schemeId 电价标准ID
     * @return 结果
     */
    @Override
    public Integer recalculateSeasonalPriceBySchemeId(Long schemeId) {
        ElectricPriceSchemeConfig electricPriceSchemeConfig = new ElectricPriceSchemeConfig();
        electricPriceSchemeConfig.setSchemeId(schemeId);
        List<ElectricPriceSchemeConfig> list = electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigList(electricPriceSchemeConfig);
        int res = 0;
        for (ElectricPriceSchemeConfig item : list) {
            res = recalculateSeasonalPrice(item);
            if (res == 0) {
                return res;
            }
        }
        return res;
    }

    /**
     * 重新计算分时电价
     *
     * @param id 电价标准ID
     * @return 结果
     */
    @Override
    public Integer recalculateSeasonalPrice(Long id) {
        ElectricPriceSchemeConfig electricPriceSchemeConfig = electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigById(id);
        return recalculateSeasonalPrice(electricPriceSchemeConfig);
    }

    /**
     * 重新计算分时电价（执行）
     * 只更新当前站点下的电价
     */
    private Integer recalculateSeasonalPrice(ElectricPriceSchemeConfig dto) {
        if (dto == null) {
            return 0;
        }

        int year = dto.getEffectYear();

        List<Integer> monthList = new ArrayList<>();
        for (String i : dto.getEffectMonth().split(",")) {
            monthList.add(Integer.parseInt(i));
        }
        monthList.sort(Comparator.naturalOrder()); //排序：正序（倒序：reverseOrder）

        String beginTime = year + "-" + StringUtils.padLeft(monthList.get(0), 2) + "-01 00:00:00";
        String endTime = year + "-" + StringUtils.padLeft(monthList.get(monthList.size() - 1), 2) + "-" + DateUtils.getMonthLastDay(year, monthList.get(monthList.size() - 1)) + " 23:59:59";

        //当前站点下的所有变量（累积变量）
        Long deptId = publicService.getCurrentStation();

        //当前站点绑定的是否是此电价标准
        SysStation station = stationService.selectSysStationByDeptId(deptId);
        if (station == null || !station.getSchemeId().equals(dto.getSchemeId())) {
            return -1;
        }

        MonitorDeviceVar deviceVar = new MonitorDeviceVar();
        deviceVar.setDeptId(deptId);
        deviceVar.setIsAccumulation(1);
        deviceVar.setStopFlag(0);
        List<MonitorDeviceVar> deviceVarList = deviceVarService.selectMonitorDeviceVarList(deviceVar);
        if (deviceVarList.isEmpty()) {
            return 0;
        }
        List<String> varList = new ArrayList<>();
        for (MonitorDeviceVar item : deviceVarList) {
            varList.add(item.getVarSn());
        }

        for (SeasonalRangeVo item : dto.getSeasonalRange()) {
            List<Integer> hourList = new ArrayList<>();

            //说明：最后一个时间段应该是 xx:xx ~ 24:00
            //在时间上是不存在24点的（24点为第二天0点），所以选择了String类型
            //因此，在取时间时用【前闭后开】
            for (int i = Integer.parseInt(item.getStartTime().split(":")[0]); i < Integer.parseInt(item.getEndTime().split(":")[0]); i++) {
                hourList.add(i);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("recordYear", year);
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("months", monthList);
            map.put("hours", hourList);
            map.put("seasonalTypeName", item.getSeasonalName());
            map.put("chargePrice", item.getChargePrice());
            map.put("varList", varList);
            shardingMonthAccumulateService.updateShardingMonthAccumulateSeasonal(map, SecurityConstants.INNER);
        }

        return 1;
    }

    /**
     * 查询电度电价配置列表
     *
     * @param electricPriceSchemeConfig 电度电价配置
     * @return 电度电价配置
     */
    @Override
    public List<ElectricPriceSchemeConfig> selectElectricPriceSchemeConfigList(ElectricPriceSchemeConfig electricPriceSchemeConfig) {
        return electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigList(electricPriceSchemeConfig);
    }

    /**
     * 通过电价标准ID获取尖峰平谷时间段
     *
     * @param schemeId 电价标准ID
     * @return 结果
     */
    @Override
    public Map<String, List<SeasonalRangeChartVo>> selectSeasonalRangeList(Long schemeId) {
        Map<String, List<SeasonalRangeChartVo>> map = new HashMap<>();

        //ElectricPriceSchemeConfig schemeConfig = electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigForSchemeId(schemeId);
        ElectricPriceSchemeConfig schemeConfig = electricPriceSchemeConfigMapper.selectElectricPriceSchemeConfigForFenDuanTu(schemeId, DateUtils.getCurrMonth());

        if (schemeConfig == null || schemeConfig.getSeasonalRange() == null) {
            return map;
        }

        for (SeasonalRangeVo item : schemeConfig.getSeasonalRange()) {
            SeasonalRangeChartVo vo = new SeasonalRangeChartVo();
            vo.setName(item.getSeasonalName());
            vo.setStartTime(Integer.parseInt(item.getStartTime().split(":")[0]));
            vo.setEndTime(Integer.parseInt(item.getEndTime().split(":")[0]));
            vo.setChargePrice(item.getChargePrice());
            List<SeasonalRangeChartVo> list = map.get(item.getSeasonalName());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(vo);
            map.put(item.getSeasonalName(), list);
        }

        return map;
    }

    /**
     * 新增电度电价配置
     *
     * @param electricPriceSchemeConfig 电度电价配置
     * @return 结果
     */
    @Override
    public int insertElectricPriceSchemeConfig(ElectricPriceSchemeConfig electricPriceSchemeConfig) {
        return electricPriceSchemeConfigMapper.insertElectricPriceSchemeConfig(electricPriceSchemeConfig);
    }

    /**
     * 修改电度电价配置
     *
     * @param electricPriceSchemeConfig 电度电价配置
     * @return 结果
     */
    @Override
    public int updateElectricPriceSchemeConfig(ElectricPriceSchemeConfig electricPriceSchemeConfig) {
        return electricPriceSchemeConfigMapper.updateElectricPriceSchemeConfig(electricPriceSchemeConfig);
    }

    /**
     * 批量删除电度电价配置
     *
     * @param ids 需要删除的电度电价配置主键
     * @return 结果
     */
    @Override
    public int deleteElectricPriceSchemeConfigByIds(Long[] ids) {
        return electricPriceSchemeConfigMapper.deleteElectricPriceSchemeConfigByIds(ids);
    }

    /**
     * 删除电度电价配置信息
     *
     * @param id 电度电价配置主键
     * @return 结果
     */
    @Override
    public int deleteElectricPriceSchemeConfigById(Long id) {
        return electricPriceSchemeConfigMapper.deleteElectricPriceSchemeConfigById(id);
    }

    /**
     * 删除电度电价配置信息（通过电价标准ID）
     *
     * @param schemeId 电价标准ID
     * @return 结果
     */
    @Override
    public int deleteElectricPriceSchemeConfigBySchemeId(Long schemeId) {
        return electricPriceSchemeConfigMapper.deleteElectricPriceSchemeConfigBySchemeId(schemeId);
    }
}
