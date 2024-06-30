package com.eltvpp.system.api.factory;

import com.eltvpp.system.api.RemoteGroupService;
import com.eltvpp.system.api.domain.SysGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 常用服务降级处理
 *
 * @author XIAOTONG.CAO
 */
public class RemoteGroupFallbackFactory implements FallbackFactory<RemoteGroupService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteGroupFallbackFactory.class);

    @Override
    public RemoteGroupService create(Throwable throwable) {
        log.error("数据字典服务调用失败:{}", throwable.getMessage());
        return new RemoteGroupService() {
            @Override
            public SysGroup getInfoByGroupSn(String groupSn, String source) {
                return null;
            }

            @Override
            public SysGroup getInfoByGroupId(Long groupId, String source) {
                return null;
            }

            @Override
            public List<SysGroup> getListBySearch(SysGroup sysGroup, String source) {
                return null;
            }
        };
    }
}
