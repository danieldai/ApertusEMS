package com.eltvpp.system.api;

/**
 * @author: Hope
 * @created by Hope on 2024/5/8 13:55
 * @email: caoxiaotong@icihai.com
 * @description: <description>
 */

import com.eltvpp.system.api.domain.SysCommonDictData;
import com.eltvpp.system.api.factory.RemoteDictDataFallbackFactory;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 数据字典
 *
 * @author XIAOTONG.CAO
 */
@FeignClient(contextId = "remoteDictDataService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictDataFallbackFactory.class)

public interface RemoteDictDataService {

    /**
     * 根据字典类型和字典键值查询字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    @GetMapping("/dict/data/dictLabel/{dictType}/{dictValue}")
    public String getDictLabel(@PathVariable("dictType") String dictType, @PathVariable("dictValue") String dictValue, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据字典类型和字典键值查询字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    @GetMapping("/dict/data/dictValue/{dictType}/{dictLabel}")
    public String getDictValue(@PathVariable("dictType") String dictType, @PathVariable("dictLabel") String dictValue, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @GetMapping("/dict/data/list/{dictType}")
    public List<SysCommonDictData> getListByDictType(@PathVariable("dictType") String dictType, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
