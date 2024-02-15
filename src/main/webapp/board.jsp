<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="bbc.BbcDAO" %>
<%@ page import="bbc.BbcDTO" %>
<%@ page import="java.util.ArrayList" %>    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width",initial-scale="1">
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/custom.css">
<title>사용자 게시판</title>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

<style type="text/css">
    a, a:hover{
        color: #000000;
        text-decoration: none;
    }
</style>
</head>
<body>
    <%
        String userID = null;
        if(session.getAttribute("userID") != null){
            userID = (String)session.getAttribute("userID");
        }
        
        int pageNumber = 1;
        if(request.getParameter("pageNumber") != null){
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));          
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
    
</head>

    <div class="container">
        <div class="form-group row pull-right">
            <div class="col-xs-8">
                <input class="form-control" id="bbcTitle" onkeyup="searchFunction()" type="text" size="20">
            </div>
            <div class="col-xs-2">
                <button class="btn btn-primary" onclick="searchFunction();" type="button">검색</button>
            </div>
        </div>
        
        <div class="row">
            <table class="table table-striped" style="text-align: center; border: 1px solid #dddddd">
                <thead>
                    <tr>
                        <th style="background-color: #eeeeee; text-align: center;">번호</th>
                        <th style="background-color: #eeeeee; text-align: center;">제목</th>
                        <th style="background-color: #eeeeee; text-align: center;">작성자</th>                        
                        <th style="background-color: #eeeeee; text-align: center;">작성일</th>
                        <th style="background-color: #eeeeee; text-align: center;">조회수</th>                  
                    </tr>
                </thead>
                <tbody id="ajaxTable">
                    <% 
                        BbcDAO bbcDAO = new BbcDAO();
                        ArrayList<BbcDTO> list = bbcDAO.getList(pageNumber);
                        for(int i = 0; i < list.size(); i++){
                    %>
                        <tr>
                            <td><%= list.get(i).getBbcID()%></td>
                            <td><a href="javascript:viewBbc('<%= list.get(i).getBbcID() %>')">
                    <%
                    	for(int j = 0; j < list.get(i).getBoardLevel(); j++){
                    %>     
                    		<span class="glyphicon glyphicon-chevron-right" aria-hidden="true" style="color: red;"></span> 
                     <%
                    	}
                     %> <%= list.get(i).getBbcTitle().replaceAll(" ","&nbsp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\n","<br>")%></a></td>
                            <td><%= list.get(i).getUserID()%></td>
                            <td><%= list.get(i).getBbcDate().substring(0,11) + list.get(i).getBbcDate().substring(11,13)+"시"+list.get(i).getBbcDate().substring(14,16) +"분"%></td>
                            <td><%= list.get(i).getBoardHit()%></td>
                        </tr>
                    <%
                        }
                    %>                                
                </tbody>                              
            </table>
            <a href="write.jsp" class="btn btn-primary pull-right">글쓰기</a>                
        </div>
</div>     
    
    
<div class="container" style="text-align: center;">
    <div class="row">   
    <% 
        int totalPages = bbcDAO.getTotalPages();
        int postsPerPage = 10; // 한 페이지에 표시되는 게시글의 수를 설정하세요
        int visiblePageRange = 5; // 표시할 페이지 링크의 개수를 설정하세요
        int totalBPages = (int) Math.ceil((double) totalPages / postsPerPage); // 전체 페이지 수 계산
        
        int startPage = Math.max(1, pageNumber - visiblePageRange / 2);
        int endPage = Math.min(totalBPages, startPage + visiblePageRange - 1);
        
        int prevPage = Math.max(1, pageNumber - 5); // 이전 5페이지
        int nextPage = Math.min(totalPages, pageNumber + 5); // 다음 5페이지

        if (pageNumber > 1) {
    %>
        <a href="board.jsp?pageNumber=<%= prevPage %>" class="btn btn-success btn-arrow-left"><</a>
    <%
        }

        for (int i = startPage; i <= endPage; i++) {
    %>
         <a href="board.jsp?pageNumber=<%= i %>" class="btn btn-success btn-rectangle <%= i == pageNumber ? "active" : "" %>"><%= i %></a>
    <%
         }
        if (pageNumber < totalBPages) {
    %>
        <a href="board.jsp?pageNumber=<%= nextPage %>" class="btn btn-success btn-arrow-right">></a>
    <%
        }
    %>
    </div>
</div>

<script>
    function viewBbc(bbcID) {
        window.location.href = "view.jsp?bbcID=" + bbcID;
     //   alert("게시물 ID: " + bbsID); // 함수가 호출되었는지 확인하기 위해 alert 창을 띄웁니다. 
    }   
</script>

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="js/bootstrap.js"></script>
<script type="text/javascript">
    var request = new XMLHttpRequest();
    function searchFunction() {
        request.open("Post","./BbcSearchServlet?bbcTitle=" + encodeURIComponent(document.getElementById("bbcTitle").value),true);
        request.onreadystatechange = searchProcess;
        request.send(null);
    }
    function searchProcess(){
        var table = document.getElementById("ajaxTable");
        table.innerHTML = "";
        if(request.readyState == 4 && request.status == 200){
            var object = eval('(' + request.responseText +')');
            var result = object.result;
            for(var i = 0; i< result.length; i++){
                var row = table.insertRow(0);
                for(var j = 0; j < result[i].length; j++){
                    var cell = row.insertCell(j);
                    cell.innerHTML = result[i][j].value;
                }
                // 클릭 이벤트 추가
                row.cells[1].onclick = function() {
                    var bbcID = this.parentNode.cells[0].innerHTML;
                    viewBbc(bbcID);
                };
            }
        }
    }
</script>
</body>
</html>


