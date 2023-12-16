package com.chai.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.chai.cmn.mapper.DictMapper;
import com.chai.yygh.model.cmn.Dict;
import com.chai.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * @program: yygh
 * @author:
 * @create: 2023-02-20 21:16
 **/
public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;
    //通过构造器的方式进行注入
    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    //一行一行读取
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //调用方法添加数据库
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
