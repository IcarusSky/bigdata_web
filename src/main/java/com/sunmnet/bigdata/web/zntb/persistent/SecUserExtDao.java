package com.sunmnet.bigdata.web.zntb.persistent;

import com.sunmnet.bigdata.web.zntb.model.dto.AccountUser;
import com.sunmnet.bigdata.web.zntb.model.po.SecUserExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecUserExtDao {
    SecUserExt selectByUserId(@Param("userId") Integer userId);

    List<SecUserExt> selectByName(@Param("name") String name, @Param("topN") String topN);

    void insert(SecUserExt userExt);

    void updateByUserId(SecUserExt userExt);

    void deleteByUserId(@Param("userId") Integer userId);

    SecUserExt selectInfoByUserId(@Param("userId")Integer userId);

    AccountUser confirmUserInfo(@Param("userName")String userName);
}
