package com.demo.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ExcelCreateService {
	
	// String과 int만 엑셀로 변환가능 - Date 및 이미지 uri 작업 미완
	public void downloadExcel(HttpServletResponse response, String name, List<?> DBlist) {
		String fileName = null;
		
		try {
			fileName = name+"다운로드.xlsx";
			fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
			// 파일 이름 설정
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename="+fileName);
		// response config
		
		// excel create
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("first sheet");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;
		
		// Header
		try {
			Class<?> clazz = Class.forName(DBlist.get(0).getClass().getName()); // DB레코드를 저장하는 Dto class 정보 가져옴
			
			row = sheet.createRow(rowNum++);
			int fieldNum = clazz.getDeclaredFields().length; // 필드 갯수 = getter 갯수
			String[] methodNames = new String[fieldNum];
			// Dto class 필드 -> excel header
			for(int i=0; i<fieldNum; i++) {
				String fieldName = clazz.getDeclaredFields()[i].getName(); // field name
				String methodName= "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1); // getter Name
				methodNames[i] = methodName; // getter 이름 추가
				cell = row.createCell(i);
				cell.setCellValue(fieldName);
			} // for - first row + getter 이름 등록
			
		// Body
			for(Object record : DBlist) {
				row = sheet.createRow(rowNum++); // record 1 : row 1
				for(int j=0; j<fieldNum; j++) { // field 1 : cell 1
					Method m = clazz.getMethod(methodNames[j]);
		            cell = row.createCell(j);
		            
		            if(m.getReturnType().getName() == "int") {
		            	int result = Integer.parseInt((m.invoke(record)).toString());
		            	cell.setCellValue(result);
		            }else {
		            	cell.setCellValue((m.invoke(record)).toString());
		            }
				} // for - each cell
			} // for - the other rows
	
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
        // Excel File Output
        try {
			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
