package com.chai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chai.yygh.model.hosp.Schedule;
import com.chai.yygh.repository.ScheduleRepository;
import com.chai.yygh.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-16 21:27
 **/
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> switchMap) {
        //将map转换为json字符串再转对象类型
        String jsonString = JSONObject.toJSONString(switchMap);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        String hoscode = schedule.getHoscode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule findSchedule = scheduleRepository.getSchduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (findSchedule != null) {
            schedule.setId(findSchedule.getId());
            schedule.setCreateTime(findSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(findSchedule.getStatus());
            schedule.setIsDeleted(findSchedule.getIsDeleted());
            scheduleRepository.save(schedule);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }

    }

    @Override
    public Page<Schedule> page(Map<String, Object> switchMap) {
        System.out.println("swichmap===>"+switchMap);
        //将map转换为json字符串再转对象类型
        String jsonString = JSONObject.toJSONString(switchMap);
        System.out.println(jsonString);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        Integer pageNum = Integer.parseInt((String) switchMap.get("page"));
        Integer pageSize = Integer.parseInt((String) switchMap.get("limit"));
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = 1;
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 10;
        }
        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Schedule isSchedule = new Schedule();
        isSchedule.setStatus(1);
        isSchedule.setIsDeleted(0);
        isSchedule.setHoscode(schedule.getHoscode());
        isSchedule.setHosScheduleId(schedule.getHosScheduleId());
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        Example<Schedule> scheduleExample = Example.of(isSchedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(scheduleExample, pageRequest);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schdule = scheduleRepository.getSchduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (hosScheduleId != null) {
            scheduleRepository.deleteById(schdule.getId());
        }
    }
}
