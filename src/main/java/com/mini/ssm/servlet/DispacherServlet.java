package com.mini.ssm.servlet;

import java.io.File;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.junit.Test;

public class DispacherServlet extends HttpServlet {
	public void init(ServletConfig config) {
		scanComponents("com.mini.ssm");
	}

	@Test
	public void test() {
		scanComponents("com.mini.ssm");
	}

	private void scanComponents(String path) {
		// System.out.println("…®√ËŒª÷√£∫" + "/" + path.replace(".", "/"));
		URL url = this.getClass().getResource("/" + path.replace(".", "/"));
		System.out.println(url.getFile());
		File files = new File(url.getFile());
		String[] filesUrl = files.list();
		for (String fileUrl : filesUrl) {
			System.out.println(fileUrl);
		}
	}

}
