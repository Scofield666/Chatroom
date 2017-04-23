package cn.itcast.vo;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * �����û�ʵ����
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
		if (obj == null)  //��ʵ���������ô˷�����this�����Ȼʱ�ǿյģ������objΪnull�����߲���
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;//����������3���жϣ�����һ������ȷ��this��obj����������ͬ��ǿ�
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

	//���UserServlet����session״̬�仯ʱ���紴������ͨ���������Զ��ѵ�ǰ�û�����usermap
	public void valueBound(HttpSessionBindingEvent event) {
		System.out.println("������....");
		HttpSession session = event.getSession();

		Map<User, HttpSession> userMap = (Map<User, HttpSession>) session
				.getServletContext().getAttribute("userMap");

		userMap.put(this, session);
	}

	// ���ʱ�Ƴ�user
	public void valueUnbound(HttpSessionBindingEvent event) {
		System.out.println("�˳���....");
		HttpSession session = event.getSession();
		Map<User, HttpSession> userMap = (Map<User, HttpSession>) session
				.getServletContext().getAttribute("userMap");
		userMap.remove(this);
	}
}