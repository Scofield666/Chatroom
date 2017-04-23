package cn.itcast.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
	/*
	 * ����һ������ͨ����Ӧһ��servlet������һ��servletֻ��һ��������doGet/doPost�ȣ��������ᵼ��servlet���࣬���׹���
	 * ����1.�Ѷ������д��ͬһ��servlet�У�Ȼ��ͨ���ж�method�����ö�Ӧ�������˷���Ҫ���д���if�жϣ�������
	 * ��˱�дһ��BaseServlet��Ϊservlet�ĸ��࣬������ʱ���ɵ�servlet�̳д��࣬ͨ�����䶯̬����servlet�еķ�������������Ϳ�д��һ��servlet��
	 * ������Щ����������serevlet�е�service�������ã������дservice��������service�������������������
	 * һ����ȡ��ǰ����servletʵ�����������ࣨ��Ϊgetmethod������Class�У���������˵�33��
	 * ������ȡ�����е�method��ͨ��getmethod��ȡ��������method�������ӻ�������26�У�
	 * ����ͨ��invokeִ�з���
	 */
	/**
	 * ��дservice����
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");

		// ���磺http://xxx/xxxservlet?method=login
		String methodName = req.getParameter("method");// �˷������ڻ�ò���method��ֵ��������ͷ�е�login��post�ȷ�����
		
		// ��û��ָ��Ҫ���õķ���ʱ����ôĬ���������execute()������
		if(methodName == null || methodName.isEmpty()) {
			methodName = "execute";
		}
		Class<?> c = this.getClass();//getmethod��Class���еķ�����������Ȼ��Class����-(�����÷��ͻ��о��棬�д�����)��getClass()��õ����ֽ����ļ�XXX.class���ֽ����ļ���Class��Ķ���
		try {//getMethod���ܻ�����NoSuchMethodException�������Ҫ����
			Method m = c.getMethod(methodName, HttpServletRequest.class,// ͨ���������Ƽ��������������APIҪ������������������.class��ȡ����ȡ�����ķ�����󣨲���API��
					HttpServletResponse.class);//��������д��������ʽ

			String result = (String) m.invoke(this, req, resp);//invoke����ִ�ж��󣨼�this������ʱ��Ķ��󣩵ķ���
			//invoke����ֵ��object���˴�תΪstring�����ڷ��ؽ��Ϊurl��xxx.html,xxx.jsp�ȣ�������UserServlet
			if(result != null && !result.isEmpty()) {// ͨ������ֵ�������ת��
				req.getRequestDispatcher(result).forward(req, resp);//forward������ת��ʽ�޷���url����Ӵ��ݲ�������Ҫ��setAttribute�ȷ�������Ϣ�Ž�������
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}

//��һ��class����ʵ����ʱ��JVM�ж�����һ��class�ֽ����ļ�������ļ���Class���󣬶����б����˴������������������Ϣ
//class�ֽ�������java����ʱ���ؽ�ȥ�ģ�getClass()�����г���ʱ��̬���صģ���this.getClass()��õ��Ǳ����.class�ֽ����ļ�