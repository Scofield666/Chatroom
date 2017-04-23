package cn.itcast.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

import cn.itcast.vo.User;

/**
 * 监听ServletContext对象创建和销毁
 */
public class MyServletContextListener implements ServletContextListener{
	//实现一个监听器，监听servletcontext，当应用程序启动时，使usermap(登陆用户表)自动装载进servletcontext
	// 通过ServletContextEvent事件对象获得事件源（servletcontext）
	public void contextInitialized(ServletContextEvent sce) {
		Map<User,HttpSession> userMap = new HashMap<User,HttpSession>();
		sce.getServletContext().setAttribute("userMap", userMap);
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("End");
	}
}
