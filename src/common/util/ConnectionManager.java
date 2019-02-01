/*
 * DB Ŀ�ؼ��� ���, ���õ� �ڿ��� �ݴ� �ߺ��� �ڵ带
 * �����ϱ� ���� Ŭ����..
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
				//JOptionPane.showMessageDialog(this, "���ӽ���");
				System.out.println("���ӽ���");
			}else {
				//JOptionPane.showMessageDialog(this, "���Ӽ���");
				System.out.println("���Ӽ���");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	//�����ͺ��̽� ���� �ڿ� �ݱ� 
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










