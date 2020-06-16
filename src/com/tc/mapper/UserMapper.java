package com.tc.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.tc.entity.User;

public interface UserMapper {
    int deleteByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int insert(@Param("db") String db,@Param("user") User record);

    int insertSelective(@Param("db") String db,@Param("user") User record);

    User selectByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int updateByPrimaryKeySelective(@Param("db") String db,@Param("user") User record);

    int updateByPrimaryKey(@Param("db") String db,@Param("user") User record);
    
    User selectByUserNameAndPwd(@Param("db") String db,@Param("user") User record);
    
    ArrayList<User> selectAll(@Param("db") String db);
    
    
}