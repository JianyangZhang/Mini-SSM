package com.mini.ssm.service;

import com.mini.ssm.annotation.MiniService;

@MiniService
public class MyServiceImpl implements MyService {

	public String query(String name, String age) {
		return "MyService Response: " + name + " " + age;
	}
}
