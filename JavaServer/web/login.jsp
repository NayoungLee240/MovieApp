<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String id = request.getParameter("id");
	String pwd = request.getParameter("pwd");
	//Thread.sleep(3000); // 3초 후에 아이디, 비밀번호 확인
	if (id.equals("id01") && pwd.equals("pwd01")) {
		out.print("1");
	} else {
		out.print("2");
	}
%>