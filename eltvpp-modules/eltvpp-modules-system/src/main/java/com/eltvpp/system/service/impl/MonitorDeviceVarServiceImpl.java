package com.eltvpp.system.service.impl;

import com.eltvpp.common.core.exception.ServiceException;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.GenRandomUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.common.datascope.annotation.DataScope;
import com.eltvpp.common.security.utils.SecurityUtils;
import com.eltvpp.system.domain.MonitorDevice;
import com.eltvpp.system.domain.MonitorDeviceVar;
import com.eltvpp.system.mapper.MonitorDeviceMapper;
import com.eltvpp.system.mapper.MonitorDeviceVarMapper;
import com.eltvpp.system.service.IMonitorDeviceVarService;
import com.eltvpp.system.service.IPublicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控设备变量Service业务层处理
 *
 * @author JUNFU.WANG
 * @date 2023-10-07
 */
@Service
public class MonitorDeviceVarServiceImpl implements IMonitorDeviceVarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorDeviceVarServiceImpl.class);

    @Autowired
    private MonitorDeviceVarMapper monitorDeviceVarMapper;

    @Autowired
    private MonitorDeviceMapper monitorDeviceMapper;

    @Autowired
    private IPublicService publicService;

    /**
     * 查询监控设备变量
     *
     * @param id 监控设备变量主键
     * @return 监控设备变量
     */
    @Override
    public MonitorDeviceVar selectMonitorDeviceVarById(Long id) {
        return monitorDeviceVarMapper.selectMonitorDeviceVarById(id);
    }

    /**
     * 通过变量编码查询监控设备变量
     *
     * @param varSn 监控设备变量编码
     * @return 监控设备变量
     */
    @Override
    public MonitorDeviceVar selectMonitorDeviceVarByVarSn(String varSn) {
        return monitorDeviceVarMapper.selectMonitorDeviceVarByVarSn(varSn);
    }

    /**
     * 查询监控设备变量列表
     *
     * @param monitorDeviceVar 监控设备变量
     * @return 监控设备变量
     */
    @Override
    public List<MonitorDeviceVar> selectMonitorDeviceVarList(MonitorDeviceVar monitorDeviceVar) {
        //获取数据加上 deptid==当前站点ID
        //解释：数据权限控制用户能看哪些数据（需要DataScope注解），而deptid=?是控制页面显示哪些数据（需要在where里加）
        if (monitorDeviceVar.getDeptId() == null) {
            monitorDeviceVar.setDeptId(publicService.getCurrentStation());
        }
        return monitorDeviceVarMapper.selectMonitorDeviceVarList(monitorDeviceVar);
    }

    /**
     * 新增监控设备变量
     *
     * @param monitorDeviceVar 监控设备变量
     * @return 结果
     */
    @Override
    public int insertMonitorDeviceVar(MonitorDeviceVar monitorDeviceVar) {
        if (monitorDeviceVar.getEntId() == null || monitorDeviceVar.getEntId() <= 0) {
            monitorDeviceVar.setEntId(publicService.getCurrentEnterprise());
        }
        if (monitorDeviceVar.getDeptId() == null || monitorDeviceVar.getDeptId() <= 0) {
            monitorDeviceVar.setDeptId(publicService.getCurrentStation());
        }
        monitorDeviceVar.setCreateBy(SecurityUtils.getNickName());
        monitorDeviceVar.setCreateTime(DateUtils.getNowDate());
        if (monitorDeviceVar.getStopFlag() == null) {
            monitorDeviceVar.setStopFlag(0);
        }
        monitorDeviceVar.setDeleteFlag(0);
        return monitorDeviceVarMapper.insertMonitorDeviceVar(monitorDeviceVar);
    }

    /**
     * 修改监控设备变量
     *
     * @param monitorDeviceVar 监控设备变量
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int updateMonitorDeviceVar(MonitorDeviceVar monitorDeviceVar) {
        monitorDeviceVar.setCreateBy(null);
        monitorDeviceVar.setCreateTime(null);
        monitorDeviceVar.setUpdateBy(SecurityUtils.getNickName());
        monitorDeviceVar.setUpdateTime(DateUtils.getNowDate());
        return monitorDeviceVarMapper.updateMonitorDeviceVar(monitorDeviceVar);
    }

    /**
     * 修改监控设备变量状态
     *
     * @param id    监控设备变量主键
     * @param state 状态
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int updateMonitorDeviceVarState(MonitorDeviceVar monitorDeviceVar, Long id, Integer state) {
        monitorDeviceVar.setId(id);
        monitorDeviceVar.setStopFlag(state);
        monitorDeviceVar.setUpdateBy(SecurityUtils.getNickName());
        monitorDeviceVar.setUpdateTime(DateUtils.getNowDate());
        return monitorDeviceVarMapper.updateMonitorDeviceVar(monitorDeviceVar);
    }

    /**
     * 修改监控设备变量【监控状态】
     *
     * @param id    监控设备变量主键
     * @param state 状态
     * @return 结果
     */
    @Override
    public int updateDeviceVarMonitorState(MonitorDeviceVar monitorDeviceVar, Long id, Integer state) {
        monitorDeviceVar.setId(id);
        monitorDeviceVar.setIsMonitor(state);
        monitorDeviceVar.setUpdateBy(SecurityUtils.getNickName());
        monitorDeviceVar.setUpdateTime(DateUtils.getNowDate());
        return monitorDeviceVarMapper.updateMonitorDeviceVar(monitorDeviceVar);
    }

    /**
     * 批量删除监控设备变量
     *
     * @param ids 需要删除的监控设备变量主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteMonitorDeviceVarByIds(MonitorDeviceVar monitorDeviceVar, Long[] ids) {
        Map<String, Object> params = monitorDeviceVar.getParams();
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("ids", ids);

        monitorDeviceVar.setParams(params);
        monitorDeviceVar.setDeleteFlag(1);
        return monitorDeviceVarMapper.updateMonitorDeviceVar(monitorDeviceVar);
    }

    /**
     * 删除监控设备变量信息
     *
     * @param id 监控设备变量主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteMonitorDeviceVarById(MonitorDeviceVar monitorDeviceVar, Long id) {
        monitorDeviceVar.setId(id);
        monitorDeviceVar.setDeleteFlag(1);
        return monitorDeviceVarMapper.updateMonitorDeviceVar(monitorDeviceVar);
    }

    // 删除上传之外的设备
    private int deleteMonitorDeviceBySns(MonitorDeviceVar monitorDeviceVar, List<String> varSns) {
        Map<String, Object> param = new HashMap<>();
        param.put("varSns", varSns);
        monitorDeviceVar.setParams(param);
        return monitorDeviceVarMapper.deleteMonitorDeviceBySns(monitorDeviceVar);
    }

    /**
     * 导入监控设备变量
     *
     * @param deviceVarList   监控设备变量列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importDevice(List<MonitorDeviceVar> deviceVarList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(deviceVarList) || deviceVarList.size() == 0) {
            throw new ServiceException("导入设备变量数据不能为空！");
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
        MonitorDeviceVar demoVar = null;

        //1-2.不需要删除的SN列表
        List<String> notDeleteList = new ArrayList<>();
        for (MonitorDeviceVar item : deviceVarList) {
            if (StringUtils.isEmpty(item.getVarSn())) {
                continue;
            }
            if (demoVar == null) {
                demoVar = selectMonitorDeviceVarByVarSn(item.getVarSn());
            }
            if (!notDeleteList.contains(item.getVarSn())) {
                notDeleteList.add(item.getVarSn());
            }
        }
        if (StringUtils.isNull(demoVar)) {
            failureMsg.insert(0, "上传的列表中变量编码有改动。");
            throw new ServiceException(failureMsg.toString());
        }

        //1-3.首先删除不在本次上传列表中的数据
        MonitorDeviceVar deleteMonitorDeviceVar = new MonitorDeviceVar();
        deleteMonitorDeviceVar.setEntId(demoVar.getEntId());
        deleteMonitorDeviceVar.setDeptId(demoVar.getDeptId());
        deleteMonitorDeviceVar.setStationType(demoVar.getStationType());
        deleteMonitorDeviceBySns(deleteMonitorDeviceVar, notDeleteList);

        //1-4.然后再导入
        for (MonitorDeviceVar item : deviceVarList) {
            try {
                //设备编码为空，新增
                if (StringUtils.isEmpty(item.getVarSn())) {
                    MonitorDeviceVar insert = packageUpload(item, demoVar, true);
                    if (insert == null) {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、设备名称 ").append(item.getDeviceName()).append(" 的变量导入失败：");
                        continue;
                    }
                    monitorDeviceVarMapper.insertMonitorDeviceVar(insert);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、变量名称 ").append(item.getVarName()).append(" 导入成功");
                    continue;
                }

                //先验证此设备是否存在
                MonitorDeviceVar dto = selectMonitorDeviceVarByVarSn(item.getVarSn());
                if (StringUtils.isNull(dto)) {
                    //新增
                    MonitorDeviceVar insert = packageUpload(item, demoVar, true);
                    if (insert == null) {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、设备名称 ").append(item.getDeviceName()).append(" 的变量导入失败：");
                        continue;
                    }
                    monitorDeviceVarMapper.insertMonitorDeviceVar(insert);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、变量名称 ").append(item.getVarName()).append(" 导入成功");
                } else {
                    //修改
                    MonitorDeviceVar update = packageUpload(item, dto, false);
                    if (update == null) {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、设备名称 ").append(item.getDeviceName()).append(" 的变量导入失败：");
                        continue;
                    }
                    monitorDeviceVarMapper.updateMonitorDeviceVar(update);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、变量名称 ").append(item.getVarName()).append(" 更新成功");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、变量名称 " + item.getVarName() + " 导入失败：";
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
    private MonitorDeviceVar packageUpload(MonitorDeviceVar in, MonitorDeviceVar demo, Boolean insert) {
        if (insert) {
            demo.setId(null);

            //设备编号+索引编码（如果索引编码为空，则自动生成4位随机数）
            if (StringUtils.isNotEmpty(in.getVarMapSn())) {
                demo.setVarSn(in.getDeviceSn() + "_" + in.getVarMapSn());
            } else {
                demo.setVarSn(in.getDeviceSn() + "_" + GenRandomUtils.GenRandomNumber(4));
            }
        }

        //获取设备信息
        MonitorDevice device = monitorDeviceMapper.selectMonitorDeviceBySn(in.getDeviceSn());
        if (StringUtils.isNull(device)) {
            return null;
        }

        demo.setChannelSn(device.getChannelSn());
        demo.setChannelId(device.getChannelId());
        demo.setComDeviceSn(device.getComDeviceSn());
        demo.setComDeviceId(device.getComDeviceId());
        demo.setDeviceId(device.getId());
        demo.setDeviceSn(in.getDeviceSn());
        demo.setVarName(in.getVarName());
        demo.setVarMapSn(in.getVarMapSn());
        demo.setUnit(in.getUnit());
        demo.setIsMonitor(in.getIsMonitor() == null ? 0 : in.getIsMonitor());
        demo.setVarType(in.getVarType());
        demo.setVariableType(in.getVariableType() == null ? 0 : in.getVariableType());
        demo.setMessageAddress(in.getMessageAddress());
        demo.setDataType(in.getDataType());
        demo.setInitValue(in.getInitValue() == null ? 0f : in.getInitValue());
        demo.setBaseValue(in.getBaseValue() == null ? 0f : in.getBaseValue());
        demo.setCoefficient(in.getCoefficient() == null ? 0f : in.getCoefficient());
        demo.setSaveCycle(in.getSaveCycle() == null ? 0 : in.getSaveCycle());
        demo.setIsAccumulation(in.getIsAccumulation() == null ? 0 : in.getIsAccumulation());
        demo.setAccumulationType(in.getAccumulationType());

        return demo;
    }

    /**
     * 根据部门ID查询所有点位
     */
    @Override
    public Integer selectDeviceVarCountByDeptId(Long deptId) {
        return monitorDeviceVarMapper.selectDeviceVarCountByDeptId(deptId);
    }

}
