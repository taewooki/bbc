package bbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;








public class BbcDAO {
	
	
	private Connection conn;
	private ResultSet rs;
	
	public BbcDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
			String dbID = "root";
			String dbPassword ="qwer1234";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL,dbID,dbPassword);					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// 날짜 
	/*
	public String getDate() {
		String SQL = "SELECT NOW()";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			
	} catch(Exception e) {
		e.printStackTrace();
	}
	return "";//데이터베이스 오류
}
	*/
	// bbcID 순서
	public int getNext() {
		String SQL = "SELECT bbcID FROM BBC ORDER BY bbcID DESC";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1; // 첫 번째 게시물인 경우
			
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1;//데이터베이스 오류
}
	
	// 글쓰기 
	
	public int write(String bbcTitle, String userID, String bbcContent) {
		String SQL = "INSERT INTO BBC (userID, bbcID, bbcTitle, bbcDate, bbcContent, bbcAvailable, boardHit, boardGroup, boardSeq, boardLevel)" +
				     "SELECT ?,IFNULL((SELECT MAX(bbcID) + 1 FROM BBC), 1), ?, NOW(), ?, 1, 0, IFNULL((SELECT MAX(boardGroup) + 1 FROM BBC), 0), 0, 0";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);			
			pstmt.setString(2, bbcTitle);		
			pstmt.setString(3, bbcContent);							
			return pstmt.executeUpdate();								
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1;//데이터베이스 오류	
	}
		
	
	// 목록 불러오기
	public ArrayList<BbcDTO> getList(int pageNumber){
		
	//	String SQL = "SELECT * FROM BBC WHERE bbcID < ? AND bbcAvailable = 1 ORDER BY bbcID DESC LIMIT 10";	
		String SQL = "SELECT * FROM BBC WHERE bbcID < ? AND bbcAvailable = 1 ORDER BY boardGroup DESC, boardSeq ASC LIMIT 10";		

	//	String SQL = "SELECT * FROM BBC WHERE boardGroup > (SELECT MAX(boardGroup) FROM BBC) - ? AND boardGroup <= (SELECT MAX(boardGroup) FROM BBC) - ? ORDER BY boardGroup DESC, boardSeq ASC";
		
		ArrayList<BbcDTO> list = new ArrayList<BbcDTO>();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext()-(pageNumber - 1)* 10);				
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BbcDTO bbc = new BbcDTO();
				bbc.setUserID(rs.getString(1));
				bbc.setBbcID(rs.getInt(2));
				bbc.setBbcTitle(rs.getString(3));				
				bbc.setBbcDate(rs.getString(4));
				bbc.setBbcContent(rs.getString(5));
				bbc.setBbcAvailable(rs.getInt(6));
				bbc.setBoardHit(rs.getInt(7));
				bbc.setBoardGroup(rs.getInt(8));
				bbc.setBoardSeq(rs.getInt(9));
				bbc.setBoardLevel(rs.getInt(10));
				list.add(bbc);										
			}						
	} catch(Exception e) {
		e.printStackTrace();
	}
	return list;		
}
	//댓글 관련 쿼리
	
	public ArrayList<BbcDTO> getList(String pageNumber) {
		ArrayList<BbcDTO> boardList = null;	
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM BBC WHERE boardGroup > (SELECT MAX(boardGroup) FROM BBC) - ? AND boardGroup <= (SELECT MAX(boardGroup) FROM BBC) - ? ORDER BY boardGroup DESC, boardSeq ASC";
		try {
			String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
			String dbID = "root";
			String dbPassword ="qwer1234";
			Class.forName("com.mysql.cj.jdbc.Driver");
    		conn = DriverManager.getConnection(dbURL,dbID,dbPassword);
			
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(pageNumber) * 10);
			pstmt.setInt(2, (Integer.parseInt(pageNumber) -1) * 10);
			rs = pstmt.executeQuery();			
			boardList = new ArrayList<BbcDTO>();
			while(rs.next()) {
				BbcDTO board = new BbcDTO();
				
				board.setUserID(rs.getString("userID"));
				board.setBbcID(rs.getInt("bbcID"));
				board.setBbcTitle(rs.getString("bbcTitle").replaceAll(" ", "&nbs;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBbcDate(rs.getString("bbcDate").substring(0,11));
				board.setBbcContent(rs.getString("bbcContent").replaceAll(" ", "&nbs;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBbcAvailable(rs.getInt("bbcAvailable"));
				board.setBoardHit(rs.getInt("boardHit"));				
				board.setBoardGroup(rs.getInt("boardGroup"));
				board.setBoardSeq(rs.getInt("boardSeq"));
				board.setBoardLevel(rs.getInt("boardLevel"));
				boardList.add(board);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) rs.close();
					if(conn != null) rs.close();					
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			return boardList;
		}
	
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM BBC WHERE bbcID < ? AND bbcAvailable = 1";		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext()-(pageNumber - 1)* 10);				
			rs = pstmt.executeQuery();
			if (rs.next())	{
				return true;
			}						
	} catch(Exception e) {
		e.printStackTrace();
	}
	return false;	
	}
	
	// view page 보기
	public BbcDTO getBbc(int bbcID) {
		String SQL = "SELECT * FROM BBC WHERE bbcID = ?";		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbcID);				
			rs = pstmt.executeQuery();
			if (rs.next())	{
				BbcDTO bbc = new BbcDTO();
				bbc.setUserID(rs.getString(1));
				bbc.setBbcID(rs.getInt(2));
				bbc.setBbcTitle(rs.getString(3));				
				bbc.setBbcDate(rs.getString(4));
				bbc.setBbcContent(rs.getString(5));
				bbc.setBbcAvailable(rs.getInt(6));
				bbc.setBoardHit(rs.getInt(7));
				bbc.setBoardGroup(rs.getInt(8));
				bbc.setBoardSeq(rs.getInt(9));
				bbc.setBoardLevel(rs.getInt(10));
				return bbc;	
			}						
	} catch(Exception e) {
		e.printStackTrace();
	}
	return null;	
	}
	
	// update 
	public int update(int bbcID, String bbcTitle, String bbcContent) {
		String SQL = "UPDATE BBC SET bbcTitle = ?, bbcContent = ? WHERE bbcID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbcTitle);			
			pstmt.setString(2, bbcContent);
			pstmt.setInt(3, bbcID);							
			return pstmt.executeUpdate();								
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1;//데이터베이스 오류
	}
		//삭제하기
	public int delete(int bbcID) {
						
		String SQL = "UPDATE BBC SET bbcAvailable = 0 WHERE bbcID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbcID);										
			return pstmt.executeUpdate();								
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1;//데이터베이스 오류
				
	}
	
    // (페이지) 전체 페이지 수를 얻어오는 메서드 
    public int getTotalPages() {
        int totalPages = 0;
        
            String SQL = "SELECT MAX(bbcID) FROM bbc";
            try(
            	PreparedStatement pstmt = conn.prepareStatement(SQL);
              	ResultSet resultSet = pstmt.executeQuery()){
            	 if (resultSet.next()) {
                     totalPages = resultSet.getInt(1);
                 }          
                	                	            
            } catch (Exception e) {
            e.printStackTrace(); // 실제로는 예외 처리를 더 구체적으로 수행해야 합니다.
        }
        return totalPages;
    }
    	    
   
    // 제목 검색 기능 AJAX 이용
    public ArrayList<BbcDTO> search(String bbcTitle){
		String SQL = "SELECT * FROM bbc WHERE bbcTitle LIKE ?";			
		ArrayList<BbcDTO> titleList = new ArrayList<BbcDTO>();			
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, "%" + bbcTitle + "%");			
			rs = pstmt.executeQuery();				
			while (rs.next()) {
				BbcDTO newBbc = new BbcDTO();
				newBbc.setUserID(rs.getString(1));
				newBbc.setBbcID(rs.getInt(2));
				newBbc.setBbcTitle(rs.getString(3));			
				newBbc.setBbcDate(rs.getString(4));
				newBbc.setBbcContent(rs.getString(5));
				newBbc.setBbcAvailable(rs.getInt(6));
				newBbc.setBoardHit(rs.getInt(7));
				newBbc.setBoardGroup(rs.getInt(8));
				newBbc.setBoardSeq(rs.getInt(9));
				newBbc.setBoardLevel(rs.getInt(10));
				titleList.add(newBbc);					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return titleList;				
    } 
    
	// 조회수
    public int hit(String bbcID) {
    	Connection conn= null;
    	PreparedStatement pstmt = null;		
    	String SQL = "UPDATE BBC SET boardHit = boardHit + 1 WHERE bbcID = ?";
    	try {
    		String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
			String dbID = "root";
			String dbPassword ="qwer1234";
			Class.forName("com.mysql.cj.jdbc.Driver");
		
    		conn = DriverManager.getConnection(dbURL,dbID,dbPassword);
			pstmt = conn.prepareStatement(SQL);    	
    		pstmt.setString(1, bbcID);
    		return pstmt.executeUpdate();					
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(pstmt != null) pstmt.close();
    			if(conn != null) conn.close();
    		}	catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return -1;//데이터베이스 오류
    }
    
    // 조회수
    public BbcDTO getBoard(String bbcID) {
		BbcDTO board = new BbcDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM BBC WHERE bbcID = ?";
		try {
    		String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
			String dbID = "root";
			String dbPassword ="qwer1234";
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			conn = DriverManager.getConnection(dbURL,dbID,dbPassword);
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbcID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				board.setUserID(rs.getString("userID"));
				board.setBbcID(rs.getInt("bbcID"));
				board.setBbcTitle(rs.getString("bbcTitle").replaceAll(" ", "&nbs;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBbcDate(rs.getString("bbcDate").substring(0,11));
				board.setBbcContent(rs.getString("bbcContent").replaceAll(" ", "&nbs;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBbcAvailable(rs.getInt("bbcAvailable"));
				board.setBoardHit(rs.getInt("boardHit"));			
				board.setBoardGroup(rs.getInt("boardGroup"));
				board.setBoardSeq(rs.getInt("boardSeq"));
				board.setBoardLevel(rs.getInt("boardLevel"));
				
				}				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) rs.close();
					if(conn != null) rs.close();					
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			return board;
		}
 // 답변 작성
 		public int reply(String userID, String bbcTitle, String bbcContent, BbcDTO parent) {
 			
 			Connection conn = null; 			
 			PreparedStatement pstmt = null;
 			String SQL ="INSERT INTO BBC (userID, bbcID, bbcTitle, bbcDate, bbcContent, bbcAvailable, boardHit, boardGroup, boardSeq, boardLevel) " +
 	                 "SELECT ?, IFNULL((SELECT MAX(bbcID) + 1 FROM BBC), 1), ?, NOW(), ?, 1, 0, ?, ?, ?"; 				 			
 			try {
 				
 				String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
 				String dbID = "root";
 				String dbPassword ="qwer1234";
 				Class.forName("com.mysql.cj.jdbc.Driver");
 				
 				conn = DriverManager.getConnection(dbURL,dbID,dbPassword);
 			
 				pstmt = conn.prepareStatement(SQL);
 				pstmt.setString(1, userID); 				
 				pstmt.setString(2, bbcTitle);
 				pstmt.setString(3, bbcContent);
 				pstmt.setInt(4, parent.getBoardGroup());
 				pstmt.setInt(5, parent.getBoardSeq() + 1);
 				pstmt.setInt(6, parent.getBoardLevel() + 1); 				
 				return pstmt.executeUpdate();			
 			} catch(Exception e) {
 				e.printStackTrace();
 			} finally {
 				try {
 					if(pstmt != null) pstmt.close();			
 					if(conn != null) conn.close();					
 				} catch (Exception e) {
 					e.printStackTrace();
 				}	
 			}
 			return -1;//데이터베이스 오류
 		}
 		
		// 답변 업데이드 
		public int replyUpdate(BbcDTO parent) {
			Connection conn= null;
			PreparedStatement pstmt = null;		
			String SQL = "UPDATE BBC SET boardSeq = boardSeq + 1 WHERE boardGroup = ? AND boardSeq > ?";
			try {
				
 				String dbURL = "jdbc:mysql://localhost/bbc?serverTimezone=UTC&characterEncoding=utf8&useSSL=FALSE";
 				String dbID = "root";
 				String dbPassword ="qwer1234";
 				Class.forName("com.mysql.cj.jdbc.Driver");
 				
 				conn = DriverManager.getConnection(dbURL,dbID,dbPassword);
			
				pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, parent.getBoardGroup());
				pstmt.setInt(2, parent.getBoardSeq());			
				return pstmt.executeUpdate();			
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(pstmt != null) pstmt.close();			
					if(conn != null) conn.close();					
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			return -1;//데이터베이스 오류
		}
	   
		
	

}//End

