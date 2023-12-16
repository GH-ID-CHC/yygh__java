package com.chai.yygh.service;

import com.chai.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> switchMap);

    Page<Schedule> page(Map<String, Object> switchMap);

    void remove(String hoscode, String hosScheduleId);
}
