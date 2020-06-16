package com.tc.service;

import java.util.ArrayList;


import com.tc.entity.Role;

public interface RoleService {
    int deleteByPrimaryKey(String db,Long id);

    int insert(String db,Role record);

    Role selectByPrimaryKey(String db,Long id);

    int updateByPrimaryKey(String db,Role record);
       
    ArrayList<Role> selectAll(String db);
    
    ArrayList<Role> selectAllByUserId(String db,Long id);
    
    int deleteByRidAndMid(String db,Long rid,Long mid);
}