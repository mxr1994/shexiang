package com.tc.service;

import java.util.ArrayList;
import com.tc.entity.User;

public interface UserService {
    int deleteByPrimaryKey(String db,Long id);

    int insert(String db,User record);

    int insertSelective(String db,User record);

    User selectByPrimaryKey(String db,Long id);

    int updateByPrimaryKeySelective(String db,User record);

    int updateByPrimaryKey(String db,User record);
    
    User selectByUserNameAndPwd(String db,User record);
    
    ArrayList<User> selectAll(String db);
    
}