package com.demo.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.demo.command.SearchBook;

@Service
public class TestService {
	
	@Autowired
	RestTemplate restTemplate;

	public SearchBook bookExcel(String d_titl) { // naver Api에서 데이터로 쓸 내용 가져오는 용도
		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/book_adv.json")
				.queryParam("d_titl", d_titl).queryParam("display", "10").queryParam("start", "1")
				.queryParam("sort", "sim").encode().build().toUri();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", "클라이언트ID")
				.header("X-Naver-Client-Secret", "클라이언트Secret")
				.build();
		
		
		ResponseEntity<SearchBook> response = restTemplate.exchange(req, SearchBook.class);
		SearchBook info = response.getBody();
		return info;
	}
}
