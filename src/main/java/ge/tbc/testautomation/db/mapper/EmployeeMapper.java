package ge.tbc.testautomation.db.mapper;

import ge.tbc.testautomation.db.model.EmployeeRow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface EmployeeMapper {

    @Insert("INSERT INTO employee (employee_id, name, department, phone, address, salary, email, birth_date) " +
            "VALUES (#{employeeId}, #{name}, #{department}, #{phone}, #{address}, #{salary}, #{email}, #{birthDate})")
    int insertEmployee(EmployeeRow employee);

    @Select("SELECT employee_id, name, department, phone, address, salary, email, birth_date " +
            "FROM employee WHERE employee_id = #{employeeId}")
    @Results({
            @Result(property = "employeeId", column = "employee_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "department", column = "department"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "address", column = "address"),
            @Result(property = "salary", column = "salary"),
            @Result(property = "email", column = "email"),
            @Result(property = "birthDate", column = "birth_date")
    })
    EmployeeRow selectByEmployeeId(@Param("employeeId") long employeeId);

    @Update("UPDATE employee SET name = #{name}, department = #{department}, phone = #{phone}, " +
            "address = #{address}, salary = #{salary}, email = #{email}, birth_date = #{birthDate} " +
            "WHERE employee_id = #{employeeId}")
    int updateEmployee(EmployeeRow employee);

    @Select("SELECT COUNT(*) FROM employee WHERE employee_id = #{employeeId}")
    int countByEmployeeId(@Param("employeeId") long employeeId);

    @Delete("DELETE FROM employee WHERE employee_id = #{employeeId}")
    int deleteByEmployeeId(@Param("employeeId") long employeeId);
}
