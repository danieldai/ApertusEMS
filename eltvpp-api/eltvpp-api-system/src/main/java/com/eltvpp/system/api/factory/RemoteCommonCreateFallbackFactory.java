package com.eltvpp.system.api.factory;

import com.eltvpp.system.api.domain.SysCommonCreate;
import com.eltvpp.system.api.RemoteCommonCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据表生成记录服务降级处理
 *
 * @author eltvpp
 */
@Component
public class RemoteCommonCreateFallbackFactory implements FallbackFactory<RemoteCommonCreateService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteCommonCreateFallbackFactory.class);

    @Override
    public RemoteCommonCreateService create(Throwable throwable) {
        log.error("数据表生成记录服务调用失败:{}", throwable.getMessage());
        return new RemoteCommonCreateService() {
            @Override
            public List<SysCommonCreate> list(SysCommonCreate commonCreate, String source) {
                return null;
            }
        };
    }
}
