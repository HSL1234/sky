package com.sky.service;


import com.sky.common.result.PageResult;
import com.sky.pojo.dto.EmployeeLoginDTO;
import com.sky.pojo.dto.EmployeePageQueryDTO;
import com.sky.pojo.dto.PasswordEditDTO;
import com.sky.pojo.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工添加
     * @param employee
     * @return
     */
    Integer insert(Employee employee);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用/禁用
     * @param status
     * @param id
     * @return
     */
    Integer updateStatus(Integer status, Long id);

    /**
     * 修改员工
     * @param employee
     * @return
     */
    Integer updateEmployee(Employee employee);

    /**
     * 查询员工
     * @param id
     * @return
     */
    Employee selectById(Long id);

    void editPassword(PasswordEditDTO passwordEditDTO);
}
