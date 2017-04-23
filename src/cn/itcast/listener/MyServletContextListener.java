package cn.itcast.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

import cn.itcast.vo.User;

/**
 * ����ServletContext���󴴽�������
 */
public class MyServletContextListener implements ServletContextListener{
	//ʵ��һ��������������servletcontext����Ӧ�ó�������ʱ��ʹusermap(��½�û���)�Զ�װ�ؽ�servletcontext
	// ͨ��ServletContextEvent�¼��������¼�Դ��servletcontext��
	public void contextInitialized(ServletContextEvent sce) {
		Map<User,HttpSession> userMap = new HashMap<User,HttpSession>();
		sce.getServletContext().setAttribute("userMap", userMap);
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("End");
	}
}
