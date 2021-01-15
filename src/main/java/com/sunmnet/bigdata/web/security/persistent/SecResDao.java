package com.sunmnet.bigdata.web.security.persistent;

import com.sunmnet.bigdata.web.security.model.po.SecRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecResDao {
    int deleteById(Integer id);

    int insert(SecRes record);

    int insertSelective(SecRes record);

    void insertBatch(@Param("records") List<SecRes> records);

    SecRes selectById(Integer id);

    /**
     * 查询所有角色和资源的关联关系
     */
    List<SecRes> selectAllRoleRes();

    /**
     * 查询某个角色和资源的关联关系
     */
    List<SecRes> selectRoleRes(Integer roleId);

    /**
     * 查询某用户所拥有某种类型资源ID列表，包括授权给用户及用户所属角色的所有资源
     *
     * @param resType 资源类型，参见：{@link SecRes.Type}
     * @param userId  用户ID
     * @return 资源ID列表
     */
    List<Long> resIdList(@Param("userId") Integer userId,
                         @Param("resType") String resType);

    int updateByIdSelective(SecRes record);

    int updateById(SecRes record);

    int deleteByRes(@Param("resType") String resType, @Param("resId") Long resId);

    int deleteByOwner(@Param("ownerType") Byte ownerType, @Param("ownerId") Integer ownerId);

    void deleteByMenuId(@Param("menuId") Integer menuId);

    List<SecRes> selectResByUserIdAndResType(@Param("userId") Integer userId,
                                             @Param("resType") String resType);

    Integer selectPermission(@Param("userId") Integer userId, @Param("resId") Long resId, @Param("resType") String resType);
}