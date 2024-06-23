package com.demo.command;

import lombok.Data;

@Data
public class Item { // command 클래스

	// 순서대로 엑셀에 만들어줌
	String image;  // 타입이 다르면 받을 수 있나...? -> no
	String title;
	String link;
	String author;
	int discount;
	String publisher;
	String pubdate;
	long isbn;
	//String description;
/////////////////////////////////////////////////////////
	// Image handling class - 항상 모든 필드 값의 마지막에 와야 함
	// cell을 추가로 만들지 않고, 연결된 이미지 문자열을 저장한 cell을 변경할 것임
	private IMG img = new IMG("image"); // image들어갈 필드 이름. 해당 필드 순서에 이미지가 생성됨

	
}
