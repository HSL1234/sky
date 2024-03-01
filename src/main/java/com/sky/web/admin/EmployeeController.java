package com.sky.web.admin;

import com.sky.common.constant.JwtClaimsConstant;
import com.sky.common.constant.PasswordConstant;
import com.sky.common.constant.StatusConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.properties.JwtProperties;
import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.common.utils.JwtUtil;
import com.sky.pojo.dto.EmployeeDTO;
import com.sky.pojo.dto.EmployeeLoginDTO;
import com.sky.pojo.dto.EmployeePageQueryDTO;
import com.sky.pojo.dto.PasswordEditDTO;
import com.sky.pojo.entity.Employee;
import com.sky.pojo.vo.EmployeeLoginVO;
import com.sky.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result add(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setPassword(BCrypt.hashpw(PasswordConstant.DEFAULT_PASSWORD, BCrypt.gensalt()));
        employee.setCreateTime(new Date());
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateTime(new Date());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setStatus(StatusConstant.ENABLE);

        Integer i = employeeService.insert(employee);
        if (i > 0) {
            return Result.success();
        }
        return Result.error("新增失败！");
    }

    /**
     * 员工分页查询
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 禁用/启用
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改员工状态")
    public Result status(@PathVariable Integer status, @RequestParam Long id) {
        Integer i = employeeService.updateStatus(status, id);
        if (i > 0) {
            return Result.success();
        }
        return Result.error("修改失败！");
    }

    /**
     * 编辑员工
     */
    @PutMapping
    @ApiOperation("编辑员工")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(new Date());
        employee.setUpdateUser(BaseContext.getCurrentId());
        Integer i = employeeService.updateEmployee(employee);
        if (i > 0) {
            return Result.success();
        }
        return Result.error("修改失败！");
    }

    /**
     * 根据id查询员工
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工")
    public Result<Employee> show(@PathVariable Long id) {
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }

}
