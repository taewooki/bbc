<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page import="bbc.BbcDAO" %>
 <%@ page import="bbc.BbcDTO" %>   
 <%@ page import="java.io.PrintWriter" %> 
 <% request.setCharacterEncoding("UTF-8");%>
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
		} 
		int bbcID = 0;
		if(request.getParameter("bbcID")!= null){
			bbcID = Integer.parseInt(request.getParameter("bbcID"));
		}
		if (bbcID == 0) {
			PrintWriter script = response.getWriter();
			script.println("<script>");
			script.println("alert('유효하지 않은 글입니다.')");
			script.println("location.href = 'board.jsp'");
			script.println("</script>");
		}
		BbcDTO bbc = new BbcDAO().getBbc(bbcID);
		if(!userID.equals(bbc.getUserID())){
			PrintWriter script = response.getWriter();
			script.println("<script>");
			script.println("alert('권한이 없습니다.')");
			script.println("location.href = 'board.jsp'");
			script.println("</script>");
		} else{
			if (request.getParameter("bbcTitle") == null || request.getParameter("bbcContent") == null ||request.getParameter("bbcTitle").equals("") || request.getParameter("bbcContent").equals("")){
				PrintWriter script = response.getWriter();
				script.println("<script>");
				script.println("alert('입력이 안된 사항이 있습니다.')");
				script.println("history.back()");
				script.println("</script>");
			} else {
				BbcDAO bbcDAO = new BbcDAO();
				int result = bbcDAO.update(bbcID, request.getParameter("bbcTitle"), request.getParameter("bbcContent"));
				if (result == -1) {
					PrintWriter script = response.getWriter();
					script.println("<script>");
					script.println("alert('글 수정에 실패했습니다.')");
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