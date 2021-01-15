package com.sunmnet.bigdata.web.security.authentication;

import com.sunmnet.bigdata.web.security.model.dto.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DbUserDetailService extends JdbcDaoImpl {
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected List<UserDetails> loadUsersByUsername(final String username) {
        return this.getJdbcTemplate().query(super.getUsersByUsernameQuery(), new String[]{username}, new RowMapper() {
            public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer id = rs.getInt(1);
                String userId = "";
                String username = rs.getString(2);
                String password = rs.getString(3);
                String name = rs.getString(4);
                Byte sex = rs.getByte(5);
                boolean enabled = rs.getBoolean(6);
                User user = new User(username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
                user.setId(id);
                user.setUserId(userId);
                user.setName(name);
                user.setSex(sex);
                return user;
            }
        });
    }

    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
        return userFromUserQuery;
    }
}
