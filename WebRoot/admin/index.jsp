<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>同步摄像头人脸库</h1>
	
	<input type="button" value="同步摄像头人脸库" onclick="start()">
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script>
	function start() {
		$.ajax({
			type: "post",
			url:"${pageContext.request.contextPath}/sdk/start.do",
			dataType: "json",
			data: {},
			success: function (data) {
			}
		});
	}
</script>
</html>