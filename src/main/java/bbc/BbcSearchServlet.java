package bbc;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BbcSearchServlet")
public class BbcSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		// AJAX에서 전달한 데이터 받기
		String bbcTitle = request.getParameter("bbcTitle");
		  
		System.out.println("서블릿전송");
		response.getWriter().write(getJSON(bbcTitle));	
		
	}
	  public String getJSON(String bbcTitle) {
		  if(bbcTitle == null) bbcTitle ="";	
		  StringBuffer result = new StringBuffer("");
		  result.append("{\"result\":[");
			BbcDAO bbcDAO = new BbcDAO();
			// table을 보여주기 위해 ArrayList를 만듬
			ArrayList<BbcDTO> titleList = bbcDAO.search(bbcTitle);
			for(int i = 0; i< titleList.size(); i++) {
				result.append("[{\"value\":\"" + titleList.get(i).getBbcID() + "\"},");
				result.append("{\"value\":\"" + titleList.get(i).getBbcTitle() + "\"},");
				result.append("{\"value\":\"" + titleList.get(i).getUserID() + "\"},");
				result.append("{\"value\":\"" + titleList.get(i).getBbcDate() + "\"},");
				result.append("{\"value\":\"" + titleList.get(i).getBoardHit() + "\"}],");	
				
	        }
			result.append("]}");
			return result.toString();	  
	  }		

}//End
