package com.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.demo.command.IMG;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ExcelCreateService {

	// String과 int만 엑셀로 변환가능 - Date 및 이미지 uri 작업 미완
	public void downloadExcel(HttpServletResponse response, String name, List<?> DBlist, int number
			, int width, int height) {
		
// 리스폰스객체, 파일 이름, DB레코드 리스트, 이미지갯수, 이미지 가로사이즈, 이미지 세로사이즈
// Dto에서 IMG 클래스는 마지막에 와야 함
		
		String fileName = null;

		try {
			fileName = name + "다운로드.xlsx";
			fileName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
			// 파일 이름 설정
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
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
			String[] methodNames = new String[fieldNum]; // getter메소드 이름들 - 순서 공유
			List<String> fieldNames = new ArrayList<>(); // 필드 이름들 - 순서 공유
			// Dto class 필드 -> excel header
			for (int i = 0; i < fieldNum; i++) {
				String fieldName = clazz.getDeclaredFields()[i].getName(); // field name
				String methodName = "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1); // getter Name
				methodNames[i] = methodName; // getter 이름 추가
				fieldNames.add(fieldName);
				if(i >= fieldNum - number) { // IMG 필드의 경우
					continue;
				}
				cell = row.createCell(i);
				cell.setCellValue(fieldName);
				
			} // for - first row + getter 이름 등록

			// Body
			for (Object record : DBlist) {
				row = sheet.createRow(rowNum++); // record 1 : row 1
				for (int j = 0; j < fieldNum; j++) { // field 1 : cell 1
					Method m = clazz.getMethod(methodNames[j]);
///////////////////////////////////////////////////////////////////////
// 한 줄마다 IMG 클래스에 저장된 이름으로 이미지 url이 적힌 cell의 위치 판별
// -> cell을 비우고 이미지 집어넣기 (존재하는 cell의 정보 변경)
					if ((m.getReturnType().getSimpleName()).equals("IMG")) {// 리턴 타입이 IMG 타입인 경우 cell을 만들지 않고 넘어가기
						IMG img = (IMG) m.invoke(record); 
						int index = fieldNames.indexOf(img.getName()); // 이미지 url 정보가 들어간 셀 번호 찾기
						Method getUrl = clazz.getMethod(methodNames[index]); // 해당 필드의 getter - 순서가 같아 불러오기 가능
						String imgUrl = getUrl.invoke(record).toString(); // String class의 toString
						if(imgUrl.equals("") || imgUrl.isEmpty()) {
							continue;
						}
						try {
							URL url = new URL(imgUrl);
							InputStream is = url.openStream();
							byte[] bytes = IOUtils.toByteArray(is);
							int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
							is.close();
							CreationHelper helper = workbook.getCreationHelper();
							Drawing<?> drawing = sheet.createDrawingPatriarch();
							ClientAnchor anchor = helper.createClientAnchor();
							
							
							cell = row.getCell(index);
							sheet.setColumnWidth(index, width);
							cell.getRow().setHeight((short)height);
							
							anchor.setRow1(rowNum - 1);
							anchor.setCol1(index);
							anchor.setRow2(rowNum);
							anchor.setCol2(index+1);
							drawing.createPicture(anchor, pictureIdx);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}// if문
///////////////////////////////////////////////////////////////////////			
					else {
						cell = row.createCell(j);
						if (m.getReturnType().getName() == "int") {// int type field
							int result = Integer.parseInt((m.invoke(record)).toString());
							cell.setCellValue(result);
						} else {
							cell.setCellValue((m.invoke(record)).toString());
						}
					}
					
				} // for - each cell
			} // for - the other rows

		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
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
