package com.tc.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.tc.entity.Role;

public interface RoleMapper {
    int deleteByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int insert(@Param("db") String db,@Param("role") Role record);

    int insertSelective(@Param("db") String db,@Param("role") Role record);

    Role selectByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int updateByPrimaryKey(@Param("db") String db,@Param("role") Role record);
    
    ArrayList<Role> selectAll(@Param("db") String db);
    
    ArrayList<Role> selectAllByUserId(@Param("db") String db,@Param("userid") Long id);
    
    int deleteByRidAndMid(@Param("db") String db,@Param("rid") Long rid,@Param("mid") Long mid);
    
}