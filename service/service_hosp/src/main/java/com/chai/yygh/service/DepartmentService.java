package com.chai.yygh.service;

import com.chai.yygh.model.hosp.Department;
import com.chai.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-15 22:28
 **/
public interface DepartmentService {
    /**
     * 获取科室信息
     *
     * @param switchMap
     * @return {@link Page}<{@link Department}>
     */
    Page<Department> getDepartmentByCode(Map<String, Object> switchMap);

    void save(Map<String, Object> switchMap);

    void removeByCode(String hoscode,String depcode);

    /**
     * 获取医院的科室列表
     *
     * @param hoscode 医院编号
     * @return {@code List<HospitalSet>}
     */
    List<DepartmentVo> getDepList(String hoscode);

    /**
     * 获取科室名称
     *
     * @param hoscode hoscode
     * @param depcode depcode
     * @return {@link Object}
     */
    String getDepName(String hoscode, String depcode);
}
