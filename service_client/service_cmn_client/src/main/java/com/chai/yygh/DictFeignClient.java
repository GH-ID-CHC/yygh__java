package com.chai.yygh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Repository
public interface DictFeignClient {
    /**
     * 获取数据字典的名称
     * */
    @GetMapping("/admin/cmn/dict/getName/{parentDictCode}/{value}")
    public String getName(
            @PathVariable("parentDictCode") String parentDictCode,
            @PathVariable("value") String value);

    /**
     * 获取数据字典的名称
     * */
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(
            @PathVariable("value") String value);
}
