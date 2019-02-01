/*
 * DB 커넥션을 얻고, 관련된 자원을 닫는 중복된 코드를
 * 방지하기 위한 클래스..
 * */
package common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionManager {
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	
	public Connection connect( String user, String password) {
		Connection con=null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con=DriverManager.getConnection(url, user, password);
			if(con==null) {
				//JOptionPane.showMessageDialog(this, "접속실패");
				System.out.println("접속실패");
			}else {
				//JOptionPane.showMessageDialog(this, "접속성공");
				System.out.println("접속성공");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	//데이터베이스 관련 자원 닫기 
	public void closeDB(Connection con) {//Connection con
		if(con !=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//dml
	public void closeDB(PreparedStatement pstmt) {
		if(pstmt !=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
	//select
	public void closeDB(PreparedStatement pstmt, ResultSet rs) {
		if(rs !=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		if(pstmt !=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
	
}










