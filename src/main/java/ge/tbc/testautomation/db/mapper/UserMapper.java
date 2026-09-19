package ge.tbc.testautomation.db.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("SELECT email FROM users WHERE id = #{id}")
    String selectEmailById(@Param("id") long id);
}
