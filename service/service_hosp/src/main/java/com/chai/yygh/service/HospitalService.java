package com.chai.yygh.service;

import com.chai.yygh.model.hosp.Hospital;
import com.chai.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> switchMap);

    Hospital getHospital(String hoscode);

    Page<Hospital> page(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);


    /**
     * 获取医院信息
     *
     * @param id id
     * @return {@code Hospital}
     */
    Hospital getInfo(String id);

    /**
     * 获取医院的名称
     *
     * @param hoscode 医院编号
     * @return {@link String}
     */
    String getHospName(String hoscode);

    /**
     * 获取医院列表
     *
     * @param hosname hosname
     * @return {@link List}<{@link Hospital}>
     */
    List<Hospital> findByHosname(String hosname);
}
