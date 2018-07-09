package JDBC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerDao {
/*
* 问题:不管是是否继承JdbcDaoSupport来获得JdbcTemplate,不能直接使用new的方式
* 来创建Dao对象来调用get方法,这样方法中的jdbcTemplate的引用为null
*
* */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Manager get(Integer id) {
        String sql = "SELECT id m_id,managerName m_managerName," +
                "managerPassword m_managerPassword FROM manager where id=?";
        RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<>(Manager.class);

        Manager manager = jdbcTemplate.queryForObject(sql, rowMapper, id);

        return manager;
    }
}
