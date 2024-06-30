package com.eltvpp.system.service.impl;

import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.common.datascope.annotation.DataScope;
import com.eltvpp.common.security.utils.SecurityUtils;
import com.eltvpp.system.domain.CommunicationDevice;
import com.eltvpp.system.mapper.CommunicationDeviceMapper;
import com.eltvpp.system.service.ICommunicationDeviceService;
import com.eltvpp.system.service.IPublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通讯设备Service业务层处理
 *
 * @author JUNFU.WANG
 * @date 2023-10-07
 */
@Service
public class CommunicationDeviceServiceImpl implements ICommunicationDeviceService {
    @Autowired
    private CommunicationDeviceMapper communicationDeviceMapper;

    @Autowired
    private IPublicService publicService;

    /**
     * 查询通讯设备
     *
     * @param id 通讯设备主键
     * @return 通讯设备
     */
    @Override
    public CommunicationDevice selectCommunicationDeviceById(Long id) {
        return communicationDeviceMapper.selectCommunicationDeviceById(id);
    }

    /**
     * 查询通讯设备
     *
     * @param deviceSn 通讯设备主键
     * @return 通讯设备
     */
    @Override
    public CommunicationDevice selectCommunicationDeviceBySn(String deviceSn) {
        return communicationDeviceMapper.selectCommunicationDeviceBySn(deviceSn);
    }

    /**
     * 查询通讯设备列表
     *
     * @param communicationDevice 通讯设备
     * @return 通讯设备
     */
    @Override
    public List<CommunicationDevice> selectCommunicationDeviceList(CommunicationDevice communicationDevice) {
        //获取数据加上 deptid==当前站点ID
        //解释：数据权限控制用户能看哪些数据（需要DataScope注解），而deptid=?是控制页面显示哪些数据（需要在where里加）
        communicationDevice.setDeptId(publicService.getCurrentStation());
        return communicationDeviceMapper.selectCommunicationDeviceList(communicationDevice);
    }

    /**
     * 新增通讯设备
     *
     * @param communicationDevice 通讯设备
     * @return 结果
     */
    @Override
    public int insertCommunicationDevice(CommunicationDevice communicationDevice) {
        if (communicationDevice.getEntId() == null || communicationDevice.getEntId() <= 0) {
            communicationDevice.setEntId(publicService.getCurrentEnterprise());
        }
        if (communicationDevice.getDeptId() == null || communicationDevice.getDeptId() <= 0) {
            communicationDevice.setDeptId(publicService.getCurrentStation());
        }
        communicationDevice.setCreateBy(SecurityUtils.getNickName());
        communicationDevice.setCreateTime(DateUtils.getNowDate());
        if (communicationDevice.getStopFlag() == null) {
            communicationDevice.setStopFlag(0);
        }
        communicationDevice.setDeleteFlag(0);
        return communicationDeviceMapper.insertCommunicationDevice(communicationDevice);
    }

    /**
     * 修改通讯设备
     *
     * @param communicationDevice 通讯设备
     * @return 结果
     */
    @Override
    public int updateCommunicationDevice(CommunicationDevice communicationDevice) {
        communicationDevice.setCreateBy(null);
        communicationDevice.setCreateTime(null);
        communicationDevice.setUpdateBy(SecurityUtils.getNickName());
        communicationDevice.setUpdateTime(DateUtils.getNowDate());
        return communicationDeviceMapper.updateCommunicationDevice(communicationDevice);
    }

    /**
     * 修改通讯设备状态
     *
     * @param id    通讯设备主键
     * @param state 状态
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int updateCommunicationDeviceState(CommunicationDevice communicationDevice, Long id, Integer state) {
        communicationDevice.setId(id);
        communicationDevice.setStopFlag(state);
        communicationDevice.setUpdateBy(SecurityUtils.getNickName());
        communicationDevice.setUpdateTime(DateUtils.getNowDate());
        return communicationDeviceMapper.updateCommunicationDevice(communicationDevice);
    }

    /**
     * 批量删除通讯设备
     *
     * @param ids 需要删除的通讯设备主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteCommunicationDeviceByIds(CommunicationDevice communicationDevice, Long[] ids) {
        Map<String, Object> params = communicationDevice.getParams();
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("ids", ids);

        communicationDevice.setParams(params);
        communicationDevice.setDeleteFlag(1);
        return communicationDeviceMapper.updateCommunicationDevice(communicationDevice);
    }

    /**
     * 删除通讯设备信息
     *
     * @param id 通讯设备主键
     * @return 结果
     */
    @Override
    @DataScope(deptAlias = "s")
    public int deleteCommunicationDeviceById(CommunicationDevice communicationDevice, Long id) {
        communicationDevice.setId(id);
        communicationDevice.setDeleteFlag(1);
        return communicationDeviceMapper.updateCommunicationDevice(communicationDevice);
    }

    /**
     * 通过部门ID查询所有通讯机器状态
     * @param deptId 部门ID
     * @return 结果
     * */
    @Override
    @Transactional(readOnly = true)
    public boolean statusAllByDeptId(Long deptId){
        if(StringUtils.isNull(deptId) || deptId<=0){
            return false;
        }
        //停用的设备数量
        int result = communicationDeviceMapper.statusAllByDeptId(deptId);
        //大与0则说明有停用的设备 状态就是离线
        return !(result > 0);
    }
}
