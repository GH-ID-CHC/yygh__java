package com.chai.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chai.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    /**
     * hoscode获取科室信息
     *
     * @param hoscode hoscode
     * @return {@link HospitalSet}
     */
    HospitalSet getSignByHoscode(String hoscode);
}
