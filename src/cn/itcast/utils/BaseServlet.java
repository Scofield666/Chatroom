package cn.itcast.utils;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
	/*
	 * 由于一个请求通常对应一个servlet，而且一个servlet只有一个方法（doGet/doPost等），这样会导致servlet过多，不易管理
	 * 方法1.把多个方法写在同一个servlet中，然后通过判断method来调用对应方法。此方法要进行大量if判断，不够好
	 * 因此编写一个BaseServlet作为servlet的父类，让运行时生成的servlet继承此类，通过反射动态调用servlet中的方法，多个方法就可写在一个servlet中
	 * 由于这些方法都是由serevlet中的service方法调用，因此重写service方法，让service方法调用其他多个方法
	 * 一。获取当前对象（servlet实例）的类型类（因为getmethod定义在Class中），这就有了第33行
	 * 二。获取请求中的method，通过getmethod获取方法对象（method来自链接或表单，如第26行）
	 * 三。通过invoke执行方法
	 */
	/**
	 * 重写service方法
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");

		// 例如：http://xxx/xxxservlet?method=login
		String methodName = req.getParameter("method");// 此方法用于获得参数method的值（即请求头中的login，post等方法）
		
		// 当没用指定要调用的方法时，那么默认请求的是execute()方法。
		if(methodName == null || methodName.isEmpty()) {
			methodName = "execute";
		}
		Class<?> c = this.getClass();//getmethod是Class类中的方法，因此需先获得Class对象-(不采用泛型会有警告，有待考究)，getClass()获得的是字节码文件XXX.class，字节码文件是Class类的对象
		try {//getMethod可能会引发NoSuchMethodException，因此需要捕获
			Method m = c.getMethod(methodName, HttpServletRequest.class,// 通过方法名称及方法参数类对象（API要求传入类对象参数，需用.class获取）获取方法的反射对象（查阅API）
					HttpServletResponse.class);//参数尝试写成数组形式

			String result = (String) m.invoke(this, req, resp);//invoke用于执行对象（即this，运行时类的对象）的方法
			//invoke返回值是object，此处转为string是由于返回结果为url（xxx.html,xxx.jsp等），查阅UserServlet
			if(result != null && !result.isEmpty()) {// 通过返回值完成请求转发
				req.getRequestDispatcher(result).forward(req, resp);//forward这种跳转方式无法在url后添加传递参数，需要用setAttribute等方法把信息放进请求中
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}

//当一个class生成实例的时候JVM中都会有一个class字节码文件，这个文件是Class对象，对象中保存了创建对象所需的所有信息
//class字节码是在java运行时加载进去的，getClass()是运行程序时动态加载的，那this.getClass()获得的是编译的.class字节码文件