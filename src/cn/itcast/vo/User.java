package cn.itcast.vo;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * 定义用户实体类
 */
public class User implements HttpSessionBindingListener {
	private int id;
	private String username;
	private String password;
	private String type;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)  //能实例化并调用此方法，this对象必然时非空的，因此若obj为null，两者不等
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;//当经过上面3个判断，到这一步可以确定this，obj两个对象是同类非空
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	//结合UserServlet，当session状态变化时（如创建），通过监听器自动把当前用户加入usermap
	public void valueBound(HttpSessionBindingEvent event) {
		System.out.println("进入了....");
		HttpSession session = event.getSession();

		Map<User, HttpSession> userMap = (Map<User, HttpSession>) session
				.getServletContext().getAttribute("userMap");

		userMap.put(this, session);
	}

	// 解绑时移除user
	public void valueUnbound(HttpSessionBindingEvent event) {
		System.out.println("退出了....");
		HttpSession session = event.getSession();
		Map<User, HttpSession> userMap = (Map<User, HttpSession>) session
				.getServletContext().getAttribute("userMap");
		userMap.remove(this);
	}
}