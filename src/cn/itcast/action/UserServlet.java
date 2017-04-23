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
	 * 检查session是否过期
	 */
	public String check(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// 从session中获得用户的信息
		User existUser = (User) req.getSession().getAttribute("existUser");//existUser来自登陆时的setAttribute
		// 判断session中的用户是否过期,null为过期，否则有效
		if(existUser == null){
			resp.getWriter().println("1");
		}else{
			resp.getWriter().println("2");
		}
		return null;
	}
	/*
	 *第25行的类型转换，getAttribute方法返回一个Object对象，这个object对象指向一个User实例（object是所有类的超类）
	 *Parent p = new Child();                    Parent p = new Parent();  
	 *Child  d = (Child) p;  这种情况合法                        Child  c = (Child) p;  这种情况不合法
	 */
	
	/**
	 *  退出聊天室
	 *  @throws IOException
	 */
	public String exit(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// 获得session对象，然后销毁
		HttpSession session = req.getSession();
		session.invalidate();
		resp.sendRedirect(req.getContextPath()+"/index.jsp");
		return null;
	}

	/**
	 * 发送聊天内容
	 */
	public String sendMessage(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		// 先接收前端传输来的数据 。
		System.out.println("可以发送....");
		String from = req.getParameter("from"); // 发言人
		String face = req.getParameter("face"); // 表情
		String to = req.getParameter("to"); // 接收者
		String color = req.getParameter("color"); // 字体颜色
		String content = req.getParameter("content"); // 发言内容
		Date now = new Date();
		String sendTime = SimpleDateFormat.getDateTimeInstance().format(now); // 发言时间
		ServletContext application = getServletContext();
		String sourceMessage = (String) application.getAttribute("message");//先获得之前的聊天记录，再拼接新的信息
		// 拼接发言的内容
		sourceMessage += "<font color='blue'><strong>" + from
				+ "</strong></font><font color='#CC0000'>" + face
				+ "</font>对<font color='green'>[" + to + "]</font>说："
				+ "<font color='" + color + "'>" + content + "</font>（"
				+ sendTime + "）<br>";
		application.setAttribute("message", sourceMessage);// 将消息存入到application的范围，这样才能让全局的人看到，可通过getattribute获得
		return getMessage(req, resp);
	}
	
	/**
	 * 获取消息
	 */
	public String getMessage(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String message = (String) getServletContext().getAttribute("message");
		if(message != null){
			resp.getWriter().println(message);//输出字符文本到客户端
		}
		return null;
	}	
	
	/**
	 * 踢人下线
	 * 从userMap中将用户对应的session销毁
	 */
	public String kick(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int id = Integer.parseInt(req.getParameter("id"));
		Map<User, HttpSession> userMap = (Map<User, HttpSession>) getServletContext()
				.getAttribute("userMap");
		// 在User中重写了equals和 hashCode方法， 根据这两方法，用户的id相同就认为是同一个用户.
		User user = new User();
		user.setId(id); 
		HttpSession session = userMap.get(user);//调用get（user）时，系统会根据重写的hashcode和equals，比较user和usermap中的key以获取对应value
		session.invalidate();
		resp.sendRedirect(req.getContextPath()+"/main.jsp");
		return null;
	}
	
	/**
	 * 登录
	 */
	public String login(HttpServletRequest req,HttpServletResponse resp){
		Map<String, String[]> map = req.getParameterMap();
		//此处将请求中的内容转换为map形式，即key-value形式，这样可以用populate方法映射到Bean的属性中，不用进行大量的 set属性 设置
		User user = new User();
		try {
			BeanUtils.populate(user, map);
			// 调用Service层处理数据 
			UserService us = new UserService();
			User existUser = us.login(user);
			if (existUser == null) {
				req.setAttribute("msg", "用户名或密码错误!");
				/*
				 * 为什么不是resp.setattribute？因为从页面获得req后，第一个servlet（就是这个userservlet）对请求做出了处理，
				 * 然后处理结果（msg）再作为请求的一部分放进req中转交给另外的servlet(这里是index.jsp)处理，msg最后会显示在index中，然后index作为resp返回
				 * 因此这个请求就相当于第一个servlet向第二个servlet发出的请求，请求中的信息最终会在第二个servlet中寻回，并作为resp
				 * response对象只会装载最终结果（要返回的html页面），它不会对页面内容进行修改，因此也就没有setattribute方法
				 */
				return "/index.jsp";
				//返回一个string（目的是获得一个转发路径），UserServlet继承了BaseServlet的service方法，service调用login，根据string转发，参考BaseServlet(41-44)行
			} else {
				/*
				 * 在同一个浏览器上连续登陆两个用户的话，前者的登陆信息会从session中移除，导致后者保留了前者的其他信息（他们共享一个session），
				 * 因此需在第二个用户登录后将之前的session销毁
				 */
				req.getSession().invalidate();			
				// 在不同浏览器中登陆同一个用户时，前者session会从map中移除，但没有销毁，导致前者不能被踢下线，需销毁前者session.
				// 获得到ServletCOntext中存的userMap集合.
				Map<User, HttpSession> userMap = (Map<User, HttpSession>) this.getServletContext().getAttribute("userMap");
				//usermap包含了所有的user对象（不同session，不同req，不同用户），只有通过作用域为application的对象才能获得，因此使用getServletContext()
				if(userMap.containsKey(existUser)){
					userMap.get(existUser).invalidate();
				}
				
				req.getSession().setAttribute("existUser", existUser);
				ServletContext application = getServletContext();//this.可以省去
				//创建这个servletcontext对象目的用于保存聊天信息，由于这些信息是面向所有用户的（你看到别人，别人能看到你），因此需全局对象，同第135行
				String sourceMessage = "";

				if (application.getAttribute("message") != null) {
					sourceMessage = application.getAttribute("message").toString();
				}

				sourceMessage += existUser.getUsername() + "走进了聊天室！</font><br>";
				application.setAttribute("message", sourceMessage);//session，application等对象使用setattribute方法时均以key-value形式保存数据

				resp.sendRedirect(req.getContextPath() + "/main.jsp");
				return null;//在上一行代码中进行了重定向进入了聊天室，不需再返回一个路径（不需service方法根据路径进行转发）
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;//由于上面使用了try，有可能出现异常而什么都不返回，因此最后返回一个null（作为String的返回值），不然会报错
	}
}
