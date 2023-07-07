package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;

import java.util.Map;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Map<String, Object> queryPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param status 修改成
     * @param id 被修改的员工
     */
    void editStatus(Integer status, Integer id);

    /**
     * 根据id查询员工
     */
    Employee getById(Integer id);

    /**
     * 修改员工
     * @param employeeDTO
     */
    void updateEmployee(EmployeeDTO employeeDTO);
}
