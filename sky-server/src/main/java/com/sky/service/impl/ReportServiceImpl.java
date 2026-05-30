package com.sky.service.impl;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    //统计指定时间区间内的营业额
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        log.info("营业额数据统计：{}到{}", begin, end);
        //创建数组用于存放从begin到end的每天
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        //循环，日期+1，直到结束日期
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //创建存放营业额数据的数组
        List<Double> turnoverList = new ArrayList<>();
        //创建存放营业额数据的数组
        for (LocalDate date : dateList) {
            //创建查询条件
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByDate(map);
            turnoverList.add(turnover == null ? 0.0 : turnover);
         }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

    }


    //用户统计
    public UserReportVO getUserStatistics(LocalDate begin , LocalDate end){
        log.info("用户数据统计：{}到{}", begin, end);

        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询订单数量
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            totalUserList.add(userMapper.countByMap(map));
            newUserList.add(userMapper.countByMap(map));
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }


    //订单统计
    public OrderReportVO getOrderStatistics(LocalDate begin , LocalDate end){
        log.info("订单数据统计：{}到{}", begin, end);

        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询订单数量
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            orderCountList.add(orderMapper.countByDate(map));

            map.put("status", Orders.COMPLETED);
            validOrderCountList.add(orderMapper.countByDate(map));
        }

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(orderMapper.count()!=null ? orderMapper.count():0)
                .validOrderCount(orderMapper.countStatus(Orders.COMPLETED)!=null ? orderMapper.countStatus(Orders.COMPLETED):0)
                .orderCompletionRate(
                        (orderMapper.countStatus(Orders.COMPLETED)!=null) &&(orderMapper.count()!=null)
                                ? orderMapper.countStatus(Orders.COMPLETED).doubleValue() / orderMapper.count():0.0)
                .build();
    }
}
