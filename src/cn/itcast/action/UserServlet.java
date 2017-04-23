package cn.itcast.action;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import cn.itcast.service.UserService;
import cn.itcast.utils.BaseServlet;
import cn.itcast.vo.User;

public class UserServlet extends BaseServlet {
	/**
	 * ���session�Ƿ����
	 */
	public String check(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// ��session�л���û�����Ϣ
		User existUser = (User) req.getSession().getAttribute("existUser");//existUser���Ե�½ʱ��setAttribute
		// �ж�session�е��û��Ƿ����,nullΪ���ڣ�������Ч
		if(existUser == null){
			resp.getWriter().println("1");
		}else{
			resp.getWriter().println("2");
		}
		return null;
	}
	/*
	 *��25�е�����ת����getAttribute��������һ��Object�������object����ָ��һ��Userʵ����object��������ĳ��ࣩ
	 *Parent p = new Child();                    Parent p = new Parent();  
	 *Child  d = (Child) p;  ��������Ϸ�                        Child  c = (Child) p;  ����������Ϸ�
	 */
	
	/**
	 *  �˳�������
	 *  @throws IOException
	 */
	public String exit(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// ���session����Ȼ������
		HttpSession session = req.getSession();
		session.invalidate();
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
		return null;
	}

	/**
	 * ������������
	 */
	public String sendMessage(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// �Ƚ���ǰ�˴����������� ��
		System.out.println("���Է���....");
		String from = req.getParameter("from"); // ������
		String face = req.getParameter("face"); // ����
		String to = req.getParameter("to"); // ������
		String color = req.getParameter("color"); // ������ɫ
		String content = req.getParameter("content"); // ��������
		Date now = new Date();
		String sendTime = SimpleDateFormat.getDateTimeInstance().format(now); // ����ʱ��
		ServletContext application = getServletContext();
		String sourceMessage = (String) application.getAttribute("message");//�Ȼ��֮ǰ�������¼����ƴ���µ���Ϣ
		// ƴ�ӷ��Ե�����
		sourceMessage += "<font color='blue'><strong>" + from
				+ "</strong></font><font color='#CC0000'>" + face
				+ "</font>��<font color='green'>[" + to + "]</font>˵��"
				+ "<font color='" + color + "'>" + content + "</font>��"
				+ sendTime + "��<br>";
		application.setAttribute("message", sourceMessage);// ����Ϣ���뵽application�ķ�Χ������������ȫ�ֵ��˿�������ͨ��getattribute���
		return getMessage(req, resp);
	}
	
	/**
	 * ��ȡ��Ϣ
	 */
	public String getMessage(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String message = (String) getServletContext().getAttribute("message");
		if(message != null){
			resp.getWriter().println(message);//����ַ��ı����ͻ���
		}
		return null;
	}	
	
	/**
	 * ��������
	 * ��userMap�н��û���Ӧ��session����
	 */
	public String kick(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int id = Integer.parseInt(req.getParameter("id"));
		Map<User, HttpSession> userMap = (Map<User, HttpSession>) getServletContext()
				.getAttribute("userMap");
		// ��User����д��equals�� hashCode������ ���������������û���id��ͬ����Ϊ��ͬһ���û�.
		User user = new User();
		user.setId(id); 
		HttpSession session = userMap.get(user);//����get��user��ʱ��ϵͳ�������д��hashcode��equals���Ƚ�user��usermap�е�key�Ի�ȡ��Ӧvalue
		session.invalidate();
		resp.sendRedirect(req.getContextPath()+"/main.jsp");
		return null;
	}
	
	/**
	 * ��¼
	 */
	public String login(HttpServletRequest req,HttpServletResponse resp){
		Map<String, String[]> map = req.getParameterMap();
		//�˴��������е�����ת��Ϊmap��ʽ����key-value��ʽ������������populate����ӳ�䵽Bean�������У����ý��д����� set���� ����
		User user = new User();
		try {
			BeanUtils.populate(user, map);
			// ����Service�㴦������ 
			UserService us = new UserService();
			User existUser = us.login(user);
			if (existUser == null) {
				req.setAttribute("msg", "�û������������!");
				/*
				 * Ϊʲô����resp.setattribute����Ϊ��ҳ����req�󣬵�һ��servlet���������userservlet�������������˴���
				 * Ȼ��������msg������Ϊ�����һ���ַŽ�req��ת���������servlet(������index.jsp)����msg������ʾ��index�У�Ȼ��index��Ϊresp����
				 * ������������൱�ڵ�һ��servlet��ڶ���servlet���������������е���Ϣ���ջ��ڵڶ���servlet��Ѱ�أ�����Ϊresp
				 * response����ֻ��װ�����ս����Ҫ���ص�htmlҳ�棩���������ҳ�����ݽ����޸ģ����Ҳ��û��setattribute����
				 */
				return "/index.jsp";
				//����һ��string��Ŀ���ǻ��һ��ת��·������UserServlet�̳���BaseServlet��service������service����login������stringת�����ο�BaseServlet(41-44)��
			} else {
				/*
				 * ��ͬһ���������������½�����û��Ļ���ǰ�ߵĵ�½��Ϣ���session���Ƴ������º��߱�����ǰ�ߵ�������Ϣ�����ǹ���һ��session����
				 * ������ڵڶ����û���¼��֮ǰ��session����
				 */
				req.getSession().invalidate();			
				// �ڲ�ͬ������е�½ͬһ���û�ʱ��ǰ��session���map���Ƴ�����û�����٣�����ǰ�߲��ܱ������ߣ�������ǰ��session.
				// ��õ�ServletCOntext�д��userMap����.
				Map<User, HttpSession> userMap = (Map<User, HttpSession>) this.getServletContext().getAttribute("userMap");
				//usermap���������е�user���󣨲�ͬsession����ͬreq����ͬ�û�����ֻ��ͨ��������Ϊapplication�Ķ�����ܻ�ã����ʹ��getServletContext()
				if(userMap.containsKey(existUser)){
					userMap.get(existUser).invalidate();
				}
				
				req.getSession().setAttribute("existUser", existUser);
				ServletContext application = getServletContext();//this.����ʡȥ
				//�������servletcontext����Ŀ�����ڱ���������Ϣ��������Щ��Ϣ�����������û��ģ��㿴�����ˣ������ܿ����㣩�������ȫ�ֶ���ͬ��135��
				String sourceMessage = "";

				if (application.getAttribute("message") != null) {
					sourceMessage = application.getAttribute("message").toString();
				}

				sourceMessage += existUser.getUsername() + "�߽��������ң�</font><br>";
				application.setAttribute("message", sourceMessage);//session��application�ȶ���ʹ��setattribute����ʱ����key-value��ʽ��������

				resp.sendRedirect(req.getContextPath() + "/main.jsp");
				return null;//����һ�д����н������ض�������������ң������ٷ���һ��·��������service��������·������ת����
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;//��������ʹ����try���п��ܳ����쳣��ʲô�������أ������󷵻�һ��null����ΪString�ķ���ֵ������Ȼ�ᱨ��
	}
}
