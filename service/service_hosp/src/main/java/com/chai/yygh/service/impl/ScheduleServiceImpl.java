package com.chai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chai.yygh.model.hosp.Schedule;
import com.chai.yygh.repository.ScheduleRepository;
import com.chai.yygh.service.HospitalService;
import com.chai.yygh.service.ScheduleService;
import com.chai.yygh.vo.hosp.BookingScheduleRuleVo;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                //工作日期进行分组
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")//取别名
                        .count().as("docCount")                    //总记录数
                        .sum("reservedNumber").as("reservedNumber")//可预约数
                        .sum("availableNumber").as("availableNumber"),//剩余预约数
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)

        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();

        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg,
                        Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
        //把日期对应星期获取
        for (BookingScheduleRuleVo mappedResult : mappedResults) {
            Date workDate = mappedResult.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            mappedResult.setDayOfWeek(dayOfWeek);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",mappedResults);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }
    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
