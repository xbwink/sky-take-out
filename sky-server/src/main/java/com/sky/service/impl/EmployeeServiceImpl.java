package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //这里使用security进行加密
        if (!passwordEncoder.matches(password,employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {

        //1.将信息拷贝至Employee
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        //2.设置初始值,默认密码：123456
        employee.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        employee.setStatus(StatusConstant.ENABLE); //状态默认为启用
        //设置创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 设置当前记录创建人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     * @param dto
     * @return
     */
    @Override
    public Map<String, Object> queryPage(EmployeePageQueryDTO dto) {
        //1、计算limit查询条件(pageNo-1)*pageSize
        int pageNo = (dto.getPage()-1)*dto.getPageSize();
        dto.setPage(pageNo);

        //2、构建一个分页查询dto
        List<Employee> list = employeeMapper.queryByPage(dto);
        //查询总记录数
        Integer count = employeeMapper.count(dto.getName());

        //3、构建返回结果对象
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",count);
        map.put("records",list);

        return map;
    }

    @Override
    public void editStatus(Integer status, Integer id) {
        //查询员工是否存在
        Employee employee = employeeMapper.getById(id);
        if(employee==null){
            throw new AccountNotFoundException("该员工不存在");
        }
        //更新状态
        employee.setStatus(status);
        employeeMapper.update(employee);
    }

}
