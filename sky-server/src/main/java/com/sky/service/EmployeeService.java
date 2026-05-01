package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    //添加新员工方法
    void insert(EmployeeDTO employeeDTO);

    //分页查询方法
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void banEmployee(Integer status, long id);

    //根据id查询用户数据
    Employee getById(long id);

    //修改员工信息
    void update(EmployeeDTO employee);
}
