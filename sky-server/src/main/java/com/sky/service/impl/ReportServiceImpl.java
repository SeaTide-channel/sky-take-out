package com.sky.service.impl;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;

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

    //获取特定时间段内销售前十的商品
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> list = orderMapper.getTop10(beginTime ,endTime);

        //将获取的list转换成String
        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).toList();
        List<Integer> numberList = list.stream().map(GoodsSalesDTO::getNumber).toList();

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();

    }

    public void exportBusinessData(HttpServletResponse response) {
        //获取日期
        LocalDate end = LocalDate.now();
        LocalDate begin = end.minusDays(30);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //通过POI导入Excel的模板
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //获取excel模板
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheet("Sheet1");

            //根据Excel模板填写数据
                //日期
            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
                //营业额
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
                //订单完成率
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                //新增用户数
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
                //有效订单数
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
                //平均客单价
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());


            //填写详细日期数据
            for(int i=7; i<37; i++){
                //获取当天日期的数据
                LocalDate date = begin.plusDays(i-7);

                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));




                sheet.getRow(i).getCell(1).setCellValue(begin.plusDays(i-7).toString());
                sheet.getRow(i).getCell(2).setCellValue(businessData.getTurnover());
                sheet.getRow(i).getCell(3).setCellValue(businessData.getValidOrderCount());
                sheet.getRow(i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
                sheet.getRow(i).getCell(5).setCellValue(businessData.getUnitPrice());
                sheet.getRow(i).getCell(6).setCellValue(businessData.getNewUsers());
            }


            //通过输出流将Excel文件下载东岸客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);

            //关闭资源
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
