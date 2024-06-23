package com.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.demo.command.Item;
import com.demo.command.SearchBook;
import com.demo.service.ExcelCreateService;
import com.demo.service.TestService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class TestController {

	@Autowired
	TestService tservice;
	
	@Autowired
	ExcelCreateService Eservice;
	
	
	@GetMapping("/test/api")
	public void testApi(HttpServletResponse response) {
		String d_titl = "나무";
		SearchBook SB = tservice.bookExcel(d_titl);
		
		List<Item> items = SB.getItems();
		Eservice.downloadExcel(response, d_titl, items, 1,20*256, 120 * 20);
	
	
	}
}
