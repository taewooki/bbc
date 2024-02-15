<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page import="bbc.BbcDAO" %>  
 <%@ page import="java.io.PrintWriter" %> 
 <% request.setCharacterEncoding("UTF-8");%>
 <jsp:useBean id="bbc" class="bbc.BbcDTO" scope="page"/>
 <jsp:setProperty name="bbc" property="bbcTitle"/>
 <jsp:setProperty name="bbc" property="bbcContent"/> 
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width",initial-scale="1">
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/custom.css">
<title>사용자 게시판</title>
</head>
<body>
	<%	
		String userID = null;
		if(session.getAttribute("userID") != null){
		userID =(String)session.getAttribute("userID");
		}
		if(userID == null){
		PrintWriter script = response.getWriter();
		script.println("<script>");
		script.println("alert('로그인을 하세요.')");
		script.println("location.href = 'login.jsp'");
		script.println("</script>");
		} else{
			if (bbc.getBbcTitle() == null || bbc.getBbcContent() == null){
				PrintWriter script = response.getWriter();
				script.println("<script>");
				script.println("alert('입력이 안된 사항이 있습니다.')");
				script.println("history.back()");
				script.println("</script>");
			} else {
				BbcDAO bbcDAO = new BbcDAO();
				int result = bbcDAO.write(bbc.getBbcTitle(), userID, bbc.getBbcContent());
				if (result == -1) {
					PrintWriter script = response.getWriter();
					script.println("<script>");
					script.println("alert('글쓰기에 실패했습니다.')");
					script.println("history.back()");
					script.println("</script>");
				}
				else {				
					PrintWriter script = response.getWriter();
					script.println("<script>");				
					script.println("location.href = 'board.jsp'");
					script.println("</script>");
				}			
			}			
		}
		
	%>

</body>
</html>