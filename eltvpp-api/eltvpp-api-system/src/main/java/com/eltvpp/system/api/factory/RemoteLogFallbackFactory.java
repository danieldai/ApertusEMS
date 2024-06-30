package com.eltvpp.system.api.factory;

import com.eltvpp.system.api.RemoteLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.eltvpp.common.core.domain.R;
import com.eltvpp.system.api.domain.SysLogLogin;
import com.eltvpp.system.api.domain.SysLogOper;

/**
 * 日志服务降级处理
 * 
 * @author eltvpp
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable)
    {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService()
        {
            @Override
            public R<Boolean> saveLog(SysLogOper sysLogOper, String source)
            {
                return R.fail("保存操作日志失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> saveLogininfor(SysLogLogin sysLogLogin, String source)
            {
                return R.fail("保存登录日志失败:" + throwable.getMessage());
            }
        };

    }
}
