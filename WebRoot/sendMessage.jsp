<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>SendMessage</title>
<script language="JavaScript"></script>
<style type="text/css">
body, table, td, th {
	font-size: 9pt;
}
</style>
<style id="style-1-cropbar-clipper">/* Copyright 2014 Evernote Corporation. All rights reserved. */
.en-markup-crop-options {
	top: 18px !important;
	left: 50% !important;
	margin-left: -100px !important;
	width: 200px !important;
	border: 2px rgba(255, 255, 255, .38) solid !important;
	border-radius: 4px !important;
}

.en-markup-crop-options div div:first-of-type {
	margin-left: 0px !important;
}
</style>
</head>
<body>
	<b>SendMessage:发送短信</b>
	<form action="/sendMsgServlet" method="post">
		<b>号 码:(多个号码用 "|"、 "," 隔开)</b><br>
		<textarea name="destnumbers" rows="4" cols="60"></textarea>
		<br>
		<b>内 容:(请发正规短信内容：如：今天天气转凉，请注意防寒保暖！祝身体健康，开心快乐！)</b><br>
		<textarea name="msg" rows="4" cols="60"></textarea>
		<br>
		<b>每秒条数:</b><input name="numberPerSecond" type="text" size="16" value="5"><b>总发数:</b><input name="totalNumber" type="text" size="16" value="10"><br>
		<input name="Submit" type="button" value="POST 提 交"
			onclick="
                
                this.form.method='post';
                this.form.submit();
              
            ">.
		<input name="Submit" type="button" value="GET 提 交"
			onclick="
                
                this.form.method='get';
                this.form.submit();
              
            ">
	</form>
	<b>返回的XML信息:</b>
	<br>
	<textarea rows="8" cols="80">&lt;root&gt;&lt;/root&gt;</textarea>

</body>
</html>