package com.mini.ssm.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.mini.ssm.annotation.MiniAutoWired;
import com.mini.ssm.annotation.MiniController;
import com.mini.ssm.annotation.MiniMapping;
import com.mini.ssm.annotation.MiniService;

public class DispacherServlet extends HttpServlet {
	List<String> classNames;
	Map<String, Object> beansMap;
	Map<String, MethodInfo> methodsMap;

	public DispacherServlet() {
		super();
		this.classNames = new ArrayList<String>();
		this.beansMap = new HashMap<String, Object>();
		this.methodsMap = new HashMap<String, MethodInfo>();
	}

	public void init(ServletConfig config) {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
		doAutowire();
		doMapping();
	}

	@Test
	public void test() {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
		doAutowire();
		doMapping();
		System.out.println(methodsMap.toString());
	}

	private void scanComponents(String root) {
		URL url = this.getClass().getResource("/" + root.replace(".", "/"));
		String[] fileNames = new File(url.getFile()).list();
		for (String fileName : fileNames) {
			File nextHierarchy = new File(url.getFile() + "/" + fileName);
			if (nextHierarchy.isDirectory()) {
				scanComponents(root + "." + fileName);
			} else {
				System.out.println("扫描到文件：" + root + "." + fileName);
				classNames.add(root + "." + fileName);
			}
		}
	}

	private void createInstances(List<String> classNames) {
		for (String className : classNames) {
			String s = className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(s);
				if (clazz.isAnnotationPresent(MiniController.class)) {
					Object object = clazz.newInstance();
					String key = clazz.getAnnotation(MiniController.class).value();
					if (key.equals("")) {
						key = generateKey(object);
					}
					beansMap.put(key, object);
				} else if (clazz.isAnnotationPresent(MiniService.class)) {
					Object object = clazz.newInstance();
					String key = clazz.getAnnotation(MiniService.class).value();
					if (key.equals("")) {
						key = generateKey(object);
					}
					beansMap.put(key, object);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	private void doAutowire() {
		for (Map.Entry<String, Object> beanMap : beansMap.entrySet()) {
			Object object = beanMap.getValue();
			Class<?> clazz = object.getClass();
			if (clazz.isAnnotationPresent(MiniController.class) || clazz.isAnnotationPresent(MiniService.class)) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(MiniAutoWired.class)) {
						String key = field.getAnnotation(MiniAutoWired.class).value();
						field.setAccessible(true); // 取消private权限控制检查 (并没有改变字段的访问权限)
						try {
							field.set(object, beansMap.get(key));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void doMapping() {
		for (Map.Entry<String, Object> beanMap : beansMap.entrySet()) {
			Object object = beanMap.getValue();
			Class<?> clazz = object.getClass();
			if (clazz.isAnnotationPresent(MiniController.class)) {
				String requestURIOnClass = clazz.getAnnotation(MiniMapping.class).value();
				for (Method method : clazz.getMethods()) {
					if (method.isAnnotationPresent(MiniMapping.class)) {
						String requestURIOnMethod = method.getAnnotation(MiniMapping.class).value();
						String requestURI = requestURIOnClass + requestURIOnMethod;
						methodsMap.put(requestURI, new MethodInfo(method, clazz));
					}
				}
			}
		}
	}

	private String generateKey(Object object) {
		String fullName = object.getClass().getName();
		String shortenName = fullName.substring((fullName.lastIndexOf(".") + 1), fullName.length());
		String key = shortenName.substring(0, 1).toLowerCase() + shortenName.substring(1);
		return key;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		String contextURI = req.getContextPath();
		String uri = requestURI.replace(contextURI, "");
		Method method = methodsMap.get(uri).method;
		Class clazz = methodsMap.get(uri).clazz;
		boolean flag = false;
		for (Object object : beansMap.values()) {
			if (object.getClass().equals(clazz)) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			System.out.println("请求映射失败，未找到对应的方法");
		}
	}
	

	class MethodInfo {
		public MethodInfo(Method method, Class<?> clazz) {
			this.method = method;
			this.clazz = clazz;
		}
		Method method;
		Class<?> clazz;
	}
}
