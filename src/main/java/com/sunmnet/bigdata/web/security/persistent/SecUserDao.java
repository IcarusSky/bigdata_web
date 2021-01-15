package com.sunmnet.bigdata.web.security.persistent;

import com.sunmnet.bigdata.web.security.model.dto.UserQuery;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.model.po.SecUserRoles;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SecUserDao {
    int deleteById(Integer id);

    int insert(SecUser record);

    int insertSelective(SecUser record);

    SecUser selectById(Integer id);

    SecUser selectByUsername(String username);

    List<SecUser> selectAll(UserQuery query);

    List<SecUserRoles> selectUserRoles(Map<String, Object> params);

    int countSameUserName(@Param("id") Integer id, @Param("userName") String userName);
    
    int updateByIdSelective(SecUser record);

    int updateById(SecUser record);
    
    List<SecUser> selectAccount(@Param("accountType") Byte accountType, @Param("accountCode") String accountCode);

	int updateUserPasswordByUserNo(SecUser userInfo);
}