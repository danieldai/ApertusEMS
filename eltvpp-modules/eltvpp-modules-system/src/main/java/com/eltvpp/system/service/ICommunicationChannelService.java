package com.eltvpp.system.service;

import java.util.List;

import com.eltvpp.system.domain.CommunicationChannel;

/**
 * 通讯通道Service接口
 *
 * @author JUNFU.WANG
 * @date 2023-10-07
 */
public interface ICommunicationChannelService {
    /**
     * 查询通讯通道
     *
     * @param id 通讯通道主键
     * @return 通讯通道
     */
    public CommunicationChannel selectCommunicationChannelById(Long id);

    /**
     * 通过通道sn获取通道信息
     *
     * @param channelSn 通道sn
     * @return 通讯通道
     */
    public CommunicationChannel selectCommunicationChannelBySn(String channelSn);

    /**
     * 通过注册码获取通道信息
     *
     * @param registrationCode 注册码
     * @return 通讯通道
     */
    public CommunicationChannel selectCommunicationChannelByRegisterCode(String registrationCode);

    /**
     * 查询通讯通道列表
     *
     * @param communicationChannel 通讯通道
     * @return 通讯通道集合
     */
    public List<CommunicationChannel> selectCommunicationChannelList(CommunicationChannel communicationChannel);

    /**
     * 新增通讯通道
     *
     * @param communicationChannel 通讯通道
     * @return 结果
     */
    public int insertCommunicationChannel(CommunicationChannel communicationChannel);

    /**
     * 修改通讯通道
     *
     * @param communicationChannel 通讯通道
     * @return 结果
     */
    public int updateCommunicationChannel(CommunicationChannel communicationChannel);

    /**
     * 修改通讯通道状态
     *
     * @param id    通讯通道主键
     * @param state 状态
     * @return 结果
     */
    public int updateCommunicationChannelState(Long id, Integer state);

    /**
     * 批量删除通讯通道
     *
     * @param ids 需要删除的通讯通道主键集合
     * @return 结果
     */
    public int deleteCommunicationChannelByIds(Long[] ids);

    /**
     * 删除通讯通道信息
     *
     * @param id 通讯通道主键
     * @return 结果
     */
    public int deleteCommunicationChannelById(Long id);
}
