package com.eltvpp.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.system.api.RemoteLogService;
import com.eltvpp.system.api.domain.SysLogOper;

/**
 * 异步调用日志服务
 * 
 * @author eltvpp
 */
@Service
public class AsyncLogService
{
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysLogOper sysLogOper) throws Exception
    {
        remoteLogService.saveLog(sysLogOper, SecurityConstants.INNER);
    }
}
