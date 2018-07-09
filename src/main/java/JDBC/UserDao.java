package JDBC;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
/**
 * 不推荐使用JdbcDaoSupport,推荐直接使用JdbcTemplate作为Dao类的成员变量
 * */
@Repository
public class UserDao extends JdbcDaoSupport {

    @Autowired
    public void setDataSource2(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public User get(Integer id) {
        String sql = "SELECT * FROM user WHERE id=?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
                    //区别
        User user = getJdbcTemplate().queryForObject(sql, rowMapper, id);

        return user;
    }

}
