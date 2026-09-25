package ge.tbc.testautomation.db.config;

import ge.tbc.testautomation.db.mapper.EmployeeMapper;
import ge.tbc.testautomation.db.mapper.UserMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public final class DataBaseConfig {

    private static final String JDBC_URL =
            "jdbc:h2:tcp://localhost:9093/./companyDB;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;" +
                    "MODE=MSSQLServer;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
    // Both services connect with Spring Boot's default (no username/password configured),
    // which resolves to an empty user/password pair against this H2 instance.
    private static final String JDBC_USER = "";
    private static final String JDBC_PASSWORD = "";

    private static final SqlSessionFactory SQL_SESSION_FACTORY = buildSqlSessionFactory();

    private DataBaseConfig() {
    }

    private static SqlSessionFactory buildSqlSessionFactory() {
        PooledDataSource dataSource = new PooledDataSource("org.h2.Driver", JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        Environment environment = new Environment("companyDB", new JdbcTransactionFactory(), dataSource);

        Configuration configuration = new Configuration(environment);
        configuration.addMapper(EmployeeMapper.class);
        configuration.addMapper(UserMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    public static SqlSession openSession() {
        return SQL_SESSION_FACTORY.openSession(true);
    }
}
