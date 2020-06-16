package com.tc.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tc.entity.Menu;
import com.tc.entity.MenuInfo;
import com.tc.entity.Role;
import com.tc.entity.User;
import com.tc.service.MenuInfoService;
import com.tc.service.MenuService;
import com.tc.service.RoleService;
import com.tc.service.UserService;


@Controller
@RequestMapping("/admin/")
public class AdminController {

	@Autowired
	private UserService us;
	
	@Autowired
	private MenuService me;
	@Autowired
	private RoleService ro;
	@Autowired
	private MenuInfoService minfo;
	
	@RequestMapping("getUserById")
	@ResponseBody
	public User getUserById(String db,Long id){
		return us.selectByPrimaryKey(db,id);
	}
	
	@RequestMapping("insertUser")
	@ResponseBody
	public Integer insertUser(String db,User user){
		user.setUserName("周六");
		user.setPassword("123456");
		user.setDj("krls");
		user.setRoleId(3);
		user.setUpdateDate("2018-03-01 12:00:00");
		return us.insert(db, user);
	}
	
//	@RequestMapping("adminLogin")
//	public String AdminLogin(HttpServletRequest request){
//		String db = request.getParameter("schoolid");
//		db="question";
//		String userName = request.getParameter("userName");
//		String password = request.getParameter("password");
//		User user = new User();
//		user.setUserName(userName);
//		user.setPassword(password);
//		User usertemp = us.selectByUserNameAndPwd(db, user);	
//		if(usertemp!=null){
//			
//			List<Role> rolelist = ro.selectAllByUserId(db, Long.valueOf(usertemp.getRoleId()));
//			List<Menu> list = new ArrayList<Menu>();
//			if(0<=rolelist.size()){			
//				for(int i = 0 ;i < rolelist.size();i++){
//					Menu menu = new Menu();
//					menu= me.selectByPrimaryKey(db, Long.valueOf(rolelist.get(i).getMenuId()));
//					List<MenuInfo> menulist = minfo.selectByMenuId(db, menu.getId());
//					if(null!=menulist){
//						menu.setMenuInfoList(menulist);					
//					}
//					list.add(menu);
//				}			
//			}
//			request.getSession().setAttribute("list",list);
//			request.getSession().setAttribute("db",db);
//			request.getSession().setAttribute("user",usertemp);
//			request.setAttribute("user", usertemp);		
//			return "redirect:/admin/index.jsp";
//		}else{
//			request.setAttribute("error", "1");
//			return "/admin/login.jsp";
//		}	
//	}
	
	
	
	@RequestMapping("adminLogout")
	public String AdminLogout(HttpServletRequest request){
		request.getSession().removeAttribute("user");
		return "/admin/login.jsp";
	}
	
}
