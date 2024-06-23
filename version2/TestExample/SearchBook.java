package com.demo.command;

import java.util.List;

import lombok.Data;

@Data
public class SearchBook {
	String lastBuildDate;
	int total;
	int start;
	int display;
	List<Item> items;
}
