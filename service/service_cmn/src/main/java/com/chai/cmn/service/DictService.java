package com.chai.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chai.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void export(HttpServletResponse response);

    void importDictData(MultipartFile file);

    String getName(String parentDictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
