package com.mini.ssm.servlet;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.junit.Test;

import com.mini.ssm.annotation.MiniAutoWired;
import com.mini.ssm.annotation.MiniController;
import com.mini.ssm.annotation.MiniService;

public class DispacherServlet extends HttpServlet {
	List<String> classNames;
	Map<String, Object> beansMap;

	public DispacherServlet() {
		super();
		this.classNames = new ArrayList<String>();
		this.beansMap = new HashMap<String, Object>();
	}

	public void init(ServletConfig config) {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
		doAutowire();
	}

	@Test
	public void test() {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
		doAutowire();
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

	private String generateKey(Object object) {
		String fullName = object.getClass().getName();
		String shortenName = fullName.substring((fullName.lastIndexOf(".") + 1), fullName.length());
		String key = shortenName.substring(0, 1).toLowerCase() + shortenName.substring(1);
		return key;
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
}
