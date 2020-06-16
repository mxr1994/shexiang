package com.tc.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.entity.User;
import com.tc.mapper.UserMapper;
import com.tc.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserMapper um;
	
	public int deleteByPrimaryKey(String db,Long id) {
		// TODO Auto-generated method stub
		return um.deleteByPrimaryKey(db, id);
	}

	public int insert(String db,User record) {
		return  um.insert(db, record);
	}

	public int insertSelective(String db,User record) {
		// TODO Auto-generated method stub
		return um.insertSelective(db, record);
	}
	
	/**
	 * 根据指定ID查询用户
	 */
	public User selectByPrimaryKey(String db,Long id) {
		return um.selectByPrimaryKey(db,id);
	}

	public int updateByPrimaryKey(String db,User record) {
		// TODO Auto-generated method stub
		return um.updateByPrimaryKey(db, record);
	}

	public int updateByPrimaryKeySelective(String db,User record) {
		// TODO Auto-generated method stub
		return um.updateByPrimaryKeySelective(db, record);
	}

	public User selectByUserNameAndPwd(String db, User record) {
		return um.selectByUserNameAndPwd(db, record);
	}

	public ArrayList<User> selectAll(String db) {
		return um.selectAll(db);
	}


}
