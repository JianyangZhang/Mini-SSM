package com.mini.ssm.servlet;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.junit.Test;

import com.mini.ssm.annotation.MiniController;

public class DispacherServlet extends HttpServlet {
	List<String> classNames;

	public DispacherServlet() {
		super();
		this.classNames = new ArrayList<String>();
		;
	}

	public void init(ServletConfig config) {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
	}

	@Test
	public void test() {
		scanComponents("com.mini.ssm");
		createInstances(classNames);
	}

	private void createInstances(List<String> classNames) {
		for (String className : classNames) {
			String s = className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(s);
				if (clazz.isAnnotationPresent(MiniController.class)) {
					Object object = clazz.newInstance();
					String key = generateKey(object);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
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
				System.out.println("É¨Ãèµ½ÎÄ¼þ£º" + root + "." + fileName);
				classNames.add(root + "." + fileName);
			}
		}
	}
}
