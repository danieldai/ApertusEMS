package com.eltvpp.system.service.impl;

import com.eltvpp.system.service.*;
import com.eltvpp.common.core.exception.ServiceException;
import com.eltvpp.common.core.utils.ChineseToPinyinUtils;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.GenRandomUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.common.datascope.annotation.DataScope;
import com.eltvpp.common.security.utils.SecurityUtils;
import com.eltvpp.system.api.domain.SysGroup;
import com.eltvpp.system.api.domain.SysStation;
import com.eltvpp.system.domain.CommunicationChannel;
import com.eltvpp.system.domain.CommunicationDevice;
import com.eltvpp.system.domain.MonitorDevice;
import com.eltvpp.system.mapper.MonitorDeviceMapper;
import com.eltvpp.system.mapper.SysStationMapper;
import com.eltvpp.system.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 能源监控设备Service业务层处理
 *
 * @author JUNFU.WANG
 * @date 2023-10-07
 */
@Service
public class MonitorDeviceServiceImpl implements IMonitorDeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorDeviceServiceImpl.class);

    @Autowired
    private MonitorDeviceMapper monitorDeviceMapper;

    @Autowired
    private SysStationMapper stationMapper;

    @Autowired
    private ISysGroupService groupService;

    @Autowired
    private IPublicService publicService;

    @Autowired
    private ICommunicationChannelService communicationChannelService;

    @Autowired
    private ICommunicationDeviceService communicationDeviceService;

    /**
     * 查询能源监控设备
     *
     * @param id 能源监控设备主键
     * @return 能源监控设备
     */
    @Override
    public MonitorDevice selectMonitorDeviceById(Long id) {
        return monitorDeviceMapper.selectMonitorDeviceById(id);
    }

    /**
     * 查询能源监控设备
     *
     * @param deviseSn 能源监控设备编码
     * @return 能源监控设备
     */
    @Override
    public MonitorDevice selectMonitorDeviceBySn(String deviseSn) {
        return monitorDeviceMapper.selectMonitorDeviceBySn(deviseSn);
    }

    /**
     * 查询能源监控设备列表（融合分组）
     *
     * @param monitorDevice 能源监控设备
     * @return 能源监控设备集合
     */
    @Override
    public List<MonitorDevice> selectMonitorDeviceListFusionGroup(MonitorDevice monitorDevice) {

        //首先查找出分组名称
        SysGroup sysGroup = new SysGroup();
        if (monitorDevice.getDeptId() != null) {
            sysGroup.setDeptId(monitorDevice.getDeptId());
        }

        //region 图表展示页面左树设定
        //情形0：全部
        //情形1：逆变器
        //情形2：汇流箱、并网柜、电能表、气象站
        //情形3：逆变器、汇流箱、并网柜、电能表
        List<String> groupnames = new ArrayList<>();
        if (monitorDevice.getListStyle() == 1) {
            groupnames.add("逆变器");
        }
        if (monitorDevice.getListStyle() == 2) {
            groupnames.add("汇流箱");
            groupnames.add("并网柜");
            groupnames.add("电能表");
            groupnames.add("气象站");
        }
        if (monitorDevice.getListStyle() == 3) {
            groupnames.add("逆变器");
            groupnames.add("汇流箱");
            groupnames.add("并网柜");
            groupnames.add("电能表");
        }
        if (!groupnames.isEmpty()) {
            //光伏固定5个1级分组：逆变器、汇流箱、并网柜、电能表、气象站
            //通过中文名称获取相关GroupId
            Map<String, Object> map = new HashMap<>();
            map.put("groupNames", groupnames);
            sysGroup.setParams(map);
        }
        //endregion

        //region 是否有筛选条件
        if (monitorDevice.getGroupId() != null) {
            sysGroup.setId(monitorDevice.getGroupId());
        }
        if (StringUtils.isNotEmpty(monitorDevice.getGroupName())) {
            sysGroup.setGroupName(monitorDevice.getGroupName());
        }
        //endregion

        sysGroup.setMapId(monitorDevice.getMapId());
        sysGroup.setStopFlag(0);
        List<SysGroup> groupList = groupService.selectSysGroupList(sysGroup);

        //region 筛选时可能只选择了一个分类，那么要将它的子分类也要选出来
        List<SysGroup> newGroupList = new ArrayList<>();
        for (SysGroup group : groupList) {
            newGroupList.addAll(groupService.getGroupChildren(group.getId(), true));
        }
        //endregion

        //构建成树形结构
        groupList = groupService.buildGroupTree(newGroupList);

        //转换成设备主体
        List<MonitorDevice> convertList = convertMD(groupList);

        //查询分组下挂载的设备
        convertList = getLoadDevice(convertList, monitorDevice);

        //region 如果是分组，且目录下没有数据，那么剔除
        convertList = eliminateEmptyList(convertList);
        //endregion

        return convertList;
    }

    // 如果分组目录下面没有数据，则删除
    private List<MonitorDevice> eliminateEmptyList(List<MonitorDevice> groupList) {
        List<MonitorDevice> returnList = new ArrayList<>();

        for (MonitorDevice item : groupList) {
            //作用：判断分组下面是否有设备
            if (!hasChildrenData(item)) {
                continue;
            }

            //作用：设备下面是否有设备
            if (item.getChildren().isEmpty()) {
                returnList.add(item);
                continue;
            }

            //作用：如果分组下面有设备
            List<MonitorDevice> children = eliminateEmptyList(item.getChildren());
            if (!children.isEmpty()) {
                item.setChildren(children);
                returnList.add(item);
            }
        }

        return returnList;
    }

    private Boolean hasChildrenData(MonitorDevice item) {
        //如果不是分组，那么不能删除
        if (item.getIsGroup() == 0) {
            return true;
        }

        //如果是分组，且子列表为空，则删除
        if (item.getChildren().isEmpty()) {
            return false;
        }

        for (MonitorDevice child : item.getChildren()) {
            return hasChildrenData(child);
        }

        return true;
    }

    // 将【分组树形】结构转化成【设备树形】结构
    private List<MonitorDevice> convertMD(List<SysGroup> groupList) {
        List<MonitorDevice> result = new ArrayList<>();
        for (SysGroup group : groupList) {
            MonitorDevice vo = new MonitorDevice();
            vo.setId(group.getId());
            vo.setEntId(group.getEntId());
            vo.setDeptId(group.getDeptId());
            vo.setDeviceName(group.getGroupName());
            vo.setDeviceSn(group.getGroupSn());
            vo.setIsGroup(1);
            vo.setChildren(convertMD(group.getChildren()));
            result.add(vo);
        }
        return result;
    }

    // 查询分组下挂载的设备
    private List<MonitorDevice> getLoadDevice(List<MonitorDevice> convertList, MonitorDevice monitorDevice) {
        List<MonitorDevice> result = new ArrayList<>();

        for (MonitorDevice item : convertList) {
            getLoadDevice(item.getChildren(), monitorDevice);

            monitorDevice.setGroupId(item.getId());
            List<MonitorDevice> deviceList = monitorDeviceMapper.selectMonitorDeviceList(monitorDevice);
            item.getChildren().addAll(deviceList);
            result.add(item);
        }

        return result;
    }

    /**
     * 查询能源监控设备列表
     *
     * @param monitorDevice 能源监控设备
     * @return 能源监控设备
     */
    @Override
    public List<SysGroup> selectMonitorDeviceListForGroup(MonitorDevice monitorDevice) {
        //首先查找出分组名称
        SysGroup sysGroup = new SysGroup();
        sysGroup.setMapId(2L);
        sysGroup.setStopFlag(0);
        List<SysGroup> groupList = groupService.selectSysGroupList(sysGroup);

        for (SysGroup item : groupList) {
            //获取数据加上 deptid==当前站点ID
            //解释：数据权限控制用户能看哪些数据（需要DataScope注解），而deptid=?是控制页面显示哪些数据（需要在where里加）
            monitorDevice.setDeptId(publicService.getCurrentStation());
            monitorDevice.setGroupId(item.getId());
            List<MonitorDevice> deviceList = monitorDeviceMapper.selectMonitorDeviceList(monitorDevice);
            item.setChildrenList(deviceList);
        }

        //构建树结构
        groupList = groupService.buildGroupTree(groupList);
        return groupList;
    }

    /**
     * 查询能源监控设备列表
     *
     * @param monitorDevice 能源监控设备
     * @return 能源监控设备
     */
    @Override
    public List<MonitorDevice> selectMonitorDeviceList(MonitorDevice monitorDevice) {
        //获取数据加上 deptid==当前站点ID
        //解释：数据权限控制用户能看哪些数据（需要DataScope注解），而deptid=?是控制页面显示哪些数据（需要在where里加）
        monitorDevice.setDeptId(publicService.getCurrentStation());
        return monitorDeviceMapper.selectMonitorDeviceList(monitorDevice);
    }

    /**
     * 新增能源监控设备
     *
     * @param monitorDevice 能源监控设备
     * @return 结果
     */
    @Override
    public int insertMonitorDevice(MonitorDevice monitorDevice) {
        if (monitorDevice.getEntId() == null || monitorDevice.getEntId() <= 0) {
            monitorDevice.setEntId(publicService.getCurrentEnterprise());
        }
        if (monitorDevice.getDeptId() == null || monitorDevice.getDeptId() <= 0) {
            monitorDevice.setDeptId(publicService.getCurrentStation());
        }
        monitorDevice.setCreateBy(SecurityUtils.getNickName());
        monitorDevice.setCreateTime(DateUtils.getNowDate());
        if (monitorDevice.getStopFlag() == null) {
            monitorDevice.setStopFlag(0);
        }
        monitorDevice.setDeleteFlag(0);
        return monitorDeviceMapper.insertMonitorDevice(monitorDevice);
    }

    /**
     * 修改能源监控设备
     *
     * @param monitorDevice 能源监控设备
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int updateMonitorDevice(MonitorDevice monitorDevice) {
        monitorDevice.setCreateBy(null);
        monitorDevice.setCreateTime(null);
        monitorDevice.setUpdateBy(SecurityUtils.getNickName());
        monitorDevice.setUpdateTime(DateUtils.getNowDate());
        return monitorDeviceMapper.updateMonitorDevice(monitorDevice);
    }

    /**
     * 修改能源监控设备状态
     *
     * @param id    能源监控设备主键
     * @param state 状态
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int updateMonitorDeviceState(MonitorDevice monitorDevice, Long id, Integer state) {
        monitorDevice.setId(id);
        monitorDevice.setStopFlag(state);
        monitorDevice.setUpdateBy(SecurityUtils.getNickName());
        monitorDevice.setUpdateTime(DateUtils.getNowDate());
        return monitorDeviceMapper.updateMonitorDevice(monitorDevice);
    }

    /**
     * 批量删除能源监控设备
     *
     * @param ids 需要删除的能源监控设备主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteMonitorDeviceByIds(MonitorDevice monitorDevice, Long[] ids) {
        Map<String, Object> params = monitorDevice.getParams();
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("ids", ids);

        monitorDevice.setParams(params);
        monitorDevice.setDeleteFlag(1);
        return monitorDeviceMapper.updateMonitorDevice(monitorDevice);
    }

    /**
     * 删除能源监控设备信息
     *
     * @param id 能源监控设备主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteMonitorDeviceById(MonitorDevice monitorDevice, Long id) {
        monitorDevice.setId(id);
        monitorDevice.setDeleteFlag(1);
        return monitorDeviceMapper.updateMonitorDevice(monitorDevice);
    }

    // 删除上传之外的设备
    private int deleteMonitorDeviceBySns(MonitorDevice monitorDevice, List<String> deviceSns) {
        Map<String, Object> param = new HashMap<>();
        param.put("deviceSns", deviceSns);
        monitorDevice.setParams(param);
        return monitorDeviceMapper.deleteMonitorDeviceBySns(monitorDevice);
    }

    /**
     * 导入能源监控设备
     *
     * @param deviceList      用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importDevice(List<MonitorDevice> deviceList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(deviceList) || deviceList.size() == 0) {
            throw new ServiceException("导入设备数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        //（1）一次性必须全部导出来
        //（2）设备编码列是不可修改的，新增的留空
        //三种情况
        //1、先删除，否则有新增的就不能删除了
        //2、修改
        //3、新增

        //1-1.其中一个设备的（理论上是第一个）
        MonitorDevice demoDevice = null;

        //1-2.不需要删除的SN列表
        List<String> notDeleteList = new ArrayList<>();
        for (MonitorDevice item : deviceList) {
            if (StringUtils.isEmpty(item.getDeviceSn())) {
                continue;
            }
            if (demoDevice == null) {
                demoDevice = selectMonitorDeviceBySn(item.getDeviceSn());
            }
            if (!notDeleteList.contains(item.getDeviceSn())) {
                notDeleteList.add(item.getDeviceSn());
            }
        }
        if (StringUtils.isNull(demoDevice)) {
            failureMsg.insert(0, "上传的列表中设备编码有改动。");
            throw new ServiceException(failureMsg.toString());
        }

        //1-3.首先删除不在本次上传列表中的数据
        MonitorDevice deleteMonitorDevice = new MonitorDevice();
        deleteMonitorDevice.setEntId(demoDevice.getEntId());
        deleteMonitorDevice.setDeptId(demoDevice.getDeptId());
        deleteMonitorDevice.setStationType(demoDevice.getStationType());
        deleteMonitorDeviceBySns(deleteMonitorDevice, notDeleteList);

        //1-4.然后再导入
        for (MonitorDevice item : deviceList) {
            try {
                //设备编码为空，新增
                if (StringUtils.isEmpty(item.getDeviceSn())) {
                    MonitorDevice insert = packageUpload(item, demoDevice, true);
                    monitorDeviceMapper.insertMonitorDevice(insert);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、设备名称 ").append(item.getDeviceName()).append(" 导入成功");
                    continue;
                }

                //先验证此设备是否存在
                MonitorDevice dto = selectMonitorDeviceBySn(item.getDeviceSn());
                if (StringUtils.isNull(dto)) {
                    //新增
                    MonitorDevice insert = packageUpload(item, demoDevice, true);
                    monitorDeviceMapper.insertMonitorDevice(insert);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、设备名称 ").append(item.getDeviceName()).append(" 导入成功");
                } else {
                    //修改
                    MonitorDevice update = packageUpload(item, dto, false);
                    monitorDeviceMapper.updateMonitorDevice(update);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、设备名称 ").append(item.getDeviceName()).append(" 更新成功");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、设备名称 " + item.getDeviceName() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
                LOGGER.error(msg, e);
            }
        }

        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 复用、组装
     *
     * @param in     上传来的设备信息
     * @param demo   从库中取的第一条记录做为模板
     * @param insert 插入还是更新
     * @return 结果
     */
    private MonitorDevice packageUpload(MonitorDevice in, MonitorDevice demo, Boolean insert) {
        if (insert) {
            demo.setId(null);

            //电表可以设置表号
            if (StringUtils.isEmpty(in.getDeviceSn())) {
                //规则：站点编码_设备名称（中文转英文）
                SysStation station = stationMapper.selectSysStationByDeptId(demo.getDeptId());
                String sn = ChineseToPinyinUtils.ToFirstChar(in.getDeviceName());
                demo.setDeviceSn(station.getStationSn() + "_" + sn);
            } else {
                demo.setDeviceSn(in.getDeviceSn());
            }
        }

        //通道、通讯设备不能固定，因为一个站点下有多个通道、通讯设备
        if (StringUtils.isNotEmpty(in.getChannelSn())) {
            CommunicationChannel channel = communicationChannelService.selectCommunicationChannelBySn(in.getChannelSn());
            if (channel != null) {
                demo.setChannelSn(in.getChannelSn());
                demo.setChannelId(channel.getId());
            }
        }
        if (StringUtils.isNotEmpty(in.getComDeviceSn())) {
            CommunicationDevice communicationDevice = communicationDeviceService.selectCommunicationDeviceBySn(in.getComDeviceSn());
            if (communicationDevice != null) {
                demo.setComDeviceSn(in.getComDeviceSn());
                demo.setComDeviceId(communicationDevice.getId());
            }
        }

        demo.setElectricType(in.getElectricType());
        demo.setDeviceName(in.getDeviceName());
        demo.setGroupId(in.getGroupId() == null ? 0L : in.getGroupId());
        demo.setProFactory(in.getProFactory() == null ? 0L : in.getProFactory());
        demo.setProType(in.getProType() == null ? 0 : in.getProType());
        demo.setProVer(in.getProVer());
        demo.setProSn(in.getProSn());
        demo.setProModel(in.getProModel());
        demo.setBuyPrice(in.getBuyPrice() == null ? 0f : in.getBuyPrice());
        demo.setAgStandard(in.getAgStandard());
        demo.setRatedVol(in.getRatedVol() == null ? 0f : in.getRatedVol());
        demo.setRatedEle(in.getRatedEle() == null ? 0f : in.getRatedEle());
        demo.setRatedPower(in.getRatedPower() == null ? 0f : in.getRatedPower());
        demo.setEleLoadRate(in.getEleLoadRate() == null ? 0f : in.getEleLoadRate());
        demo.setIsAnalysisEnergy(in.getIsAnalysisEnergy() == null ? 0 : in.getIsAnalysisEnergy());
        demo.setRatedVolume(in.getRatedVolume() == null ? 0f : in.getRatedVolume());
        demo.setAzimuth(in.getAzimuth() == null ? 0f : in.getAzimuth());
        demo.setDipAngle(in.getDipAngle() == null ? 0f : in.getDipAngle());
        demo.setManufactureDate(in.getManufactureDate());
        demo.setUseLife(in.getUseLife() == null ? 0 : in.getUseLife());
        demo.setAttenuationRate(in.getAttenuationRate() == null ? 0f : in.getAttenuationRate());

        return demo;
    }

    /**
     * 通过部门ID查询所有监控状态
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean statusAllByDeptId(Long deptId) {
        if (StringUtils.isNull(deptId) || deptId <= 0) {
            return false;
        }
        //停用的设备数量
        int result = monitorDeviceMapper.statusAllByDeptId(deptId);
        //大与0则说明有停用的设备 状态就是离线
        return !(result > 0);
    }

    /**
     * 通过部门ID查询设备数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public Integer selectDeviceCountByDeptId(Long deptId) {
        return monitorDeviceMapper.selectDeviceCountByDeptId(deptId);
    }
}
