import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import JDBC.Manager;
import JDBC.ManagerDao;
import JDBC.User;
import JDBC.UserDao;

/*
* 在查询时,查询到的列需要与类映射时,使用RowMapper rowMapper = new BeanPropertyRowMapper(Class)
* 否则可以直接在查询方法中传入字节码对象
* select * from tableName 这样的查询时,一定要bean的属性名与列名相同,否则必须指定别名实现列名
* 与属性名的映射,否则查询到的数据无法绑定到对象的属性中
* */

public class JDBCTest {
    private ApplicationContext ctx = null;
    private JdbcTemplate jdbcTemplate;
    private ManagerDao managerDao;
    private UserDao userDao;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        managerDao = (ManagerDao) ctx.getBean("managerDao");
        userDao = (UserDao) ctx.getBean("userDao");
        namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) ctx.getBean("namedParameterJdbcTemplate");
    }

    /**
     * 从数据库中获取一条记录,实际得到对应的一个对象
     * 注意不是调用queryForObject(String sql, Class<?> requiredType, Object... args)方法
     * 而需要调用queryForObject(String sql, RowMapper<?> rowMapper, Object... args)
     * rowMapper = new BeanPropertyRowMapper<>(Bean.class)
     * RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<>(Manager.class);
     * 1.其中的RowMapper,指定如何去映射结果集的行,常用的实现类为BeanPropertyRowMapper
     * 2.使用SQL中列的别名完成列明和类的属性名的映射.
     * 3.不支持级联属性,JdbcTemplate到底是一个JDBC的小工具,而不是ORM框架
     */
    @Test
    public void testQueryForObject() {
        String sql = "SELECT id m_id,managerName m_managerName" +
                ",managerPassword m_managerPassword from manager where id = ?";
        RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<>(Manager.class);
        Manager manager = jdbcTemplate.queryForObject(sql, rowMapper, 1);
        System.out.println(manager);
    }

    /**
     * 获取单个列的值,或统计查询
     * 使用 queryForObject(String sql, Class<?> requiredType)
     * */
    @Test
    public void testQueryForObject2() {
        String sql = "SELECT count(id) FROM manager";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        System.out.println(count);
    }

    /**
     * 查到实体类的集合
     * 注意调用的不是queryForList方法,而是query方法
     */
    @Test
    public void testQueryForList() {
        String sql = "SELECT id m_id,managerName m_managerName" +
                ",managerPassword m_managerPassword from manager where id > ?";
        RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<>(Manager.class);
        List<Manager> managers = jdbcTemplate.query(sql, rowMapper, 5);
        System.out.println(managers);
        for (Manager m :
                managers) {
            System.out.println(m);
            System.out.println("-----------------");
        }
    }


    /**
     * 执行批量更新:批量的 INSERT,UPDATE,DELETE
     * 最后一个参数是Object[] 的List类型:
     * 因为修改一条记录需要一个Object数组,
     * 那么多条就需要多个Object数组,即Object数组的集合
     */
    @Test
    public void testBatchUpdate() {
        String sql = "INSERT INTO manager(managerName, managerPassword)" +
                " VALUES(?,?)";
        List<Object[]> batchArgs = new ArrayList<>();

        batchArgs.add(new Object[]{"AA", 1});
        batchArgs.add(new Object[]{"BB", 2});
        batchArgs.add(new Object[]{"CC", 3});
        batchArgs.add(new Object[]{"DD", 2});


        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * 执行INSERT,UPDATE,DELETE
     */
    @Test
    public void testUpdate() {
        String sql = "UPDATE manager SET managerName=? Where id=?";
        jdbcTemplate.update(sql, "Jack", 001);
    }

    @Test
    public void testDataSource() throws SQLException {
        DataSource dataSource = (DataSource) ctx.getBean("dataSource");
        System.out.println(dataSource.getConnection());

    }

    @Test
    public void testManagerDao() {
        Manager manager = managerDao.get(1);
        System.out.println(manager);
    }

    @Test
    public void testUserDao() {
        User user = userDao.get(2);
        System.out.println(user);
    }

    /**
     * 可以为参数起名字
     * 1.好处:若有多个参数,则不用再去对应位置,直接对应参数名,便于维护
     * 2.缺点:较为麻烦
     * */
    @Test
    public void testNamedParameterJdbcTemplate() {
        String sql = "INSERT INTO manager(managerName," +
                "managerPassword) VALUES(:mn,:mp)";


        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("mn", "huangyongqi");
        paraMap.put("mp", "123456");


        namedParameterJdbcTemplate.update(sql, paraMap);
    }

    /**
     * 使用具名参数时,可以使用update(String sql, SqlParameterSource paramSource) 方法进行更新操作
     * 1. SQL语句中的参数名和类的属性必须一致!
     * 2. 使用SqlParameterSource的实现类BeanPropertySqlParameterSource作为参数
     * */
    @Test
    public void testNamedParameterJdbcTemplate2() {
        String sql = "INSERT INTO manager(managerName,managerPassword) " +
                "VALUES(:m_managerName,:m_managerPassword)";

        Manager manager = new Manager("huangyongqi2", "1232");
        Manager manager2 = new Manager("huangyongqi3", "1232");
        Manager manager3 = new Manager("huangyongqi4", "1232");
        Manager manager4 = new Manager("huangyongqi5", "1232");

        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(manager);

        namedParameterJdbcTemplate.update(sql, parameterSource);

    }
}
