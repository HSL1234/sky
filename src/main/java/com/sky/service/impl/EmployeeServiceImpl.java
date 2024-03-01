package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.constant.MessageConstant;
import com.sky.common.constant.StatusConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.exception.AccountLockedException;
import com.sky.common.exception.AccountNotFoundException;
import com.sky.common.exception.PasswordErrorException;
import com.sky.common.result.PageResult;
import com.sky.mapper.EmployeeMapper;
import com.sky.pojo.dto.EmployeeLoginDTO;
import com.sky.pojo.dto.EmployeePageQueryDTO;
import com.sky.pojo.dto.PasswordEditDTO;
import com.sky.pojo.entity.Employee;
import com.sky.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.selectOne(new Employee(username));

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对password.equals(employee.getPassword())
        if (!BCrypt.checkpw(password, employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return employee;
    }

    @Override
    public Integer insert(Employee employee) {
        return employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(employeePageQueryDTO.getName())) {
            criteria.andLike("name", "%" + employeePageQueryDTO.getName() + "%");
        }
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        List<Employee> employeeList = employeeMapper.selectByExample(example);
        // 排序
        example.orderBy("createTime").desc();
        PageInfo<Employee> employeePageInfo = new PageInfo<>(employeeList);
        return new PageResult(employeePageInfo.getTotal(), employeeList);
    }

    @Override
    public Integer updateStatus(Integer status, Long id) {
        Employee employee = Employee.builder().id(id).status(status).build();
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public Integer updateEmployee(Employee employee) {
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public Employee selectById(Long id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        employee.setPassword("*******");
        return employee;
    }

    @Override
    @Transactional
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Employee employee = employeeMapper.selectByPrimaryKey(BaseContext.getCurrentId());
        // 旧密码正确
        if (BCrypt.checkpw(passwordEditDTO.getOldPassword(), employee.getPassword())) {
            employee.setPassword(BCrypt.hashpw(passwordEditDTO.getNewPassword(), BCrypt.gensalt()));
            employee.setUpdateUser(BaseContext.getCurrentId());
            employee.setUpdateTime(new Date());
            employeeMapper.updateByPrimaryKeySelective(employee);
        } else {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

    }

}
