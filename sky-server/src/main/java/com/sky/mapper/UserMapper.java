package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 添加用户
     * @param user
     */
//    @AutoFill(OperationType.INSERT) 只有createTime字段
    void insert(User user);

    /**
     * 根据id获取用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 统计范围内用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(*) from user where create_time >= #{beginTime} and create_time <= #{endTime}")
    Integer countNewUser(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 统计时间节点前的用户数量
     * @param endTime
     * @return
     */
    @Select("select count(*) from user where create_time <= #{endTime}")
    Integer countTotalUser(LocalDateTime endTime);
}
