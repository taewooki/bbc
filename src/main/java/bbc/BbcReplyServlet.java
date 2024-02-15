package bbc;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;





@WebServlet("/bbcReply")
public class BbcReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;       
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		 // 사용자 입력값 확인을 위한 로그 출력
        System.out.println("사용자 입력값 확인:");
        System.out.println("userID: " + request.getParameter("userID"));
        System.out.println("bbcID: " + request.getParameter("bbcID"));
        System.out.println("bbcTitle: " + request.getParameter("bbcTitle"));
        System.out.println("bbcContent: " + request.getParameter("bbcContent"));
        
		String userID = request.getParameter("userID");
		HttpSession session=request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType","오류 메시지");
			session.setAttribute("messageContent","접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		String bbcID =request.getParameter("bbcID");		
		if(bbcID == null || bbcID.equals("")){
			session.setAttribute("messageType","오류 메시지");
			session.setAttribute("messageContent","접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		String bbcTitle = request.getParameter("bbcTitle");
		String bbcContent = request.getParameter("bbcContent");
		if(bbcTitle == null || bbcTitle.equals("") || bbcContent == null || bbcContent.equals("")) {
			session.setAttribute("messageType","오류 메시지");
			session.setAttribute("messageContent","내용을 모두 채워주세요.");
			response.sendRedirect("write.jsp");
			return;
		}
		
		BbcDAO bbcDAO = new BbcDAO();
		BbcDTO parent = bbcDAO.getBbc(Integer.parseInt(bbcID));
		bbcDAO.replyUpdate(parent);
		bbcDAO.reply(userID, bbcTitle, bbcContent, parent);
		session.setAttribute("messageType", "성공 메시지");
		session.setAttribute("messageContent","성공적으로 답변이 작성되었습니다.");
		response.sendRedirect("board.jsp");
		return;
		
	}

}
