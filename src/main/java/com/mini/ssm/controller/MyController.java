package com.mini.ssm.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mini.ssm.annotation.MiniAutoWired;
import com.mini.ssm.annotation.MiniController;
import com.mini.ssm.annotation.MiniMapping;
import com.mini.ssm.annotation.MiniRequestParam;
import com.mini.ssm.service.MyService;

@MiniController
@MiniMapping("/mini")
public class MyController {
	@MiniAutoWired("MyServiceImpl")
	private MyService myService;
	
	@MiniMapping("/query")
	public void query(HttpServletRequest request, HttpServletResponse response, 
			          @MiniRequestParam String name,  @MiniRequestParam String age) {
		try {
			PrintWriter pw = response.getWriter();
			String result = myService.query(name, age);
			pw.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
