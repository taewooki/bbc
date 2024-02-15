<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="bbc.BbcDTO" %> 
<%@ page import="bbc.BbcDAO" %>    
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
			userID = (String)session.getAttribute("userID");
		}

		int bbcID = 0;
		if (request.getParameter("bbcID") != null) {
    		bbcID = Integer.parseInt(request.getParameter("bbcID"));
			}

		if (bbcID == 0) {
  			 PrintWriter script = response.getWriter();
   			 script.println("<script>");
   			 script.println("alert('유효하지 않은 글입니다.')");
    		script.println("location.href = 'board.jsp'");
    		script.println("</script>");
			}

			BbcDAO bbcDAO = new BbcDAO();
			BbcDTO bbc = bbcDAO.getBbc(bbcID);

			// 조회수 증가
		try {
  			  bbcDAO.hit(Integer.toString(bbcID)); // 게시물 증가
		} catch (Exception e) {
   		 // 조회수 증가 중 오류가 발생한 경우
  		  // 오류 메시지 설정 등 예외 처리 로직 추가
  		  e.printStackTrace(); // 예외 정보를 콘솔에 출력
    		// 예외가 발생한 경우에 대한 사용자에게 알리는 등의 작업을 추가할 수 있음
		}
			
		%>
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
				aria-expanded="false">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="main.jsp">Subway 메인</a>
		</div>
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="main.jsp">메인</a></li>
				<li class="active"><a href="board.jsp">게시판</a></li>
			</ul>
			<%
				if(userID == null){
			%>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false">접속하기<span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="login.jsp">로그인</a></li>
						<li><a href="join.jsp">회원가입</a></li>	
					</ul>
				</li>
			</ul>
			<%
				} else {
			%>	
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false">회원관리<span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="logoutAction.jsp">로그아웃</a></li>						
					</ul>
				</li>
			</ul>
			<% 
				}
			%>
		</div>
	</nav>
	<div class="container">
		<div class="row">		
			<table class="table table-striped" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="3" style="background-color: #eeeeee; text-align: center;">게시판 글 보기</th>									
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="width: 20%;">글 제목</td>
						<td colspan="2"><%=bbc.getBbcTitle().replaceAll(" ","&nbsp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\n","<br>")  %></td>
					</tr>
					<tr>	
					
						<td>작성자</td>
						<td colspan="2"><%=bbc.getUserID() %></td>
					</tr>
					<tr>
						<td>작성일자</td>					
						<td colspan="2"><%=bbc.getBbcDate().substring(0,11) + bbc.getBbcDate().substring(11,13)+"시"+bbc.getBbcDate().substring(14,16) +"분" %></td>
        																							
					</tr>
					<tr>
						<td>조회수</td>
						<td colspan="2"><%=bbc.getBoardHit() %></td>
					</tr>
					<tr>
						<td>내용</td>
						<td colspan="2" style="min-height: 200px; text-align: left;"><%=bbc.getBbcContent().replaceAll(" ","&nbsp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\n","<br>") %></td>
					</tr>
											
					</tbody>					
				</table>
				<a href="board.jsp" class="btn btn-primary">목록</a>
				<%
					if(userID !=null && userID.equals(bbc.getUserID())){
				%>
						<a href="update.jsp?bbcID=<%=bbcID %>" class="btn btn-primary">수정</a>
						<a onclick="return confirm('정말로 삭제하시겠습니까?')" href="deleteAction.jsp?bbcID=<%=bbcID %>" class="btn btn-primary">삭제</a>
				<%
					}else{
				%>
					<a href="reply.jsp?bbcID=<%=bbcID %>" class="btn btn-primary">답변</a>
				<%
					}	
				%>		
									
				<a href="write.jsp" class="btn btn-primary pull-right">글쓰기</a>						
		</div>
	</div>	
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="js/bootstrap.js"></script>

</body>
</html>