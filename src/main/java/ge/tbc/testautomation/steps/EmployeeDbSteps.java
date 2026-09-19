package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.db.config.DataBaseConfig;
import ge.tbc.testautomation.db.mapper.EmployeeMapper;
import ge.tbc.testautomation.db.mapper.UserMapper;
import ge.tbc.testautomation.db.model.EmployeeRow;
import io.qameta.allure.Step;
import org.apache.ibatis.session.SqlSession;

public class EmployeeDbSteps {

    @Step("Insert employee via mybatis")
    public void insertEmployee(EmployeeRow employee) {
        try (SqlSession session = DataBaseConfig.openSession()) {
            session.getMapper(EmployeeMapper.class).insertEmployee(employee);
        }
    }

    @Step("Get employee by id via mybatis")
    public EmployeeRow getEmployeeById(long employeeId) {
        try (SqlSession session = DataBaseConfig.openSession()) {
            return session.getMapper(EmployeeMapper.class).selectByEmployeeId(employeeId);
        }
    }

    @Step("Update employee via mybatis")
    public void updateEmployee(EmployeeRow employee) {
        try (SqlSession session = DataBaseConfig.openSession()) {
            session.getMapper(EmployeeMapper.class).updateEmployee(employee);
        }
    }

    @Step("Count employee rows by id via mybatis")
    public int countByEmployeeId(long employeeId) {
        try (SqlSession session = DataBaseConfig.openSession()) {
            return session.getMapper(EmployeeMapper.class).countByEmployeeId(employeeId);
        }
    }

    @Step("Get user email by id via mybatis")
    public String getUserEmailById(long userId) {
        try (SqlSession session = DataBaseConfig.openSession()) {
            return session.getMapper(UserMapper.class).selectEmailById(userId);
        }
    }
}
