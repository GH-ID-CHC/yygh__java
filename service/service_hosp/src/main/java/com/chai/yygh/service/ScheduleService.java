package com.chai.yygh.service;

import com.chai.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> switchMap);

    Page<Schedule> page(Map<String, Object> switchMap);

    void remove(String hoscode, String hosScheduleId);

    /**
     * 获取排版信息
     *
     * @param page    当前页
     * @param limit   记录数
     * @param hoscode 医院编码
     * @param depcode 科室编码
     * @return {@link Map}<{@link String},{@link Object}>
     */
    Map<String,Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);
}
