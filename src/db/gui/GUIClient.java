package db.gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import common.util.ConnectionManager;

public class GUIClient extends JFrame{
	JPanel p_west;//서쪽 영역
	JPanel p_center;//센터 영역
	JTextField t_url; //localhost
	JTextField t_sid; //XE
	JTextField t_port;
	JTextField t_user;
	Choice ch_user;
	JPasswordField t_password;
	JButton bt_connect;
	JTable table_object;//현재 계정이 보유한 객체들
	JTable table_result;//select 수행 결과 출력
	JTextArea area;
	JScrollPane scroll,sc1,sc2;
	JButton bt_sql;
	
	Connection con;
	ConnectionManager connectionManager;
	ObjectModel objectModel;
	ResultModel resultModel;
	
	public GUIClient() {
		p_west = new JPanel();
		p_center = new JPanel();
		t_url = new JTextField("localhost");
		t_sid = new JTextField("XE");
		t_port = new JTextField("1521");
		t_user = new JTextField("adams");
		ch_user = new Choice();
		t_password = new JPasswordField();
		bt_connect = new JButton("접속");
		table_object = new JTable(); 
		table_result = new JTable();
		area=new JTextArea();
		scroll=new JScrollPane(area);
		sc1=new JScrollPane(table_object);
		sc2=new JScrollPane(table_result);
		bt_sql=new JButton("쿼리실행");
		
		area.setFont(new Font("돋움",Font.BOLD,30));
		//서쪽 영역의 컴포넌트 크기를 150으로...
		Dimension d = new Dimension(150,25);
		
		t_url.setPreferredSize(d);
		t_sid.setPreferredSize(d);
		t_port.setPreferredSize(d);
		t_user.setPreferredSize(d);
		ch_user.setPreferredSize(d);
		t_password.setPreferredSize(d);
		bt_connect.setPreferredSize(d);

		p_west.setPreferredSize(new Dimension(160, 700));
		p_west.setBackground(Color.YELLOW);
		//서쪽 패널에 부착
		p_west.add(t_url);
		p_west.add(t_sid);
		p_west.add(t_port);
		//p_west.add(t_user);
		p_west.add(ch_user);
		
		p_west.add(t_password);
		p_west.add(bt_connect);
		//frame에 부착 
		add(p_west, BorderLayout.WEST);
		
		//센터 영역에 들어갈 컴포넌트들의 크기 지정 
		sc1.setPreferredSize(new Dimension(780, 190));
		sc2.setPreferredSize(new Dimension(780, 190));
		scroll.setPreferredSize(new Dimension(780, 240));
		
		p_center.setBackground(Color.GREEN);
		p_center.add(sc1);
		p_center.add(sc2);
		p_center.add(scroll);
		p_center.add(bt_sql);
		add(p_center);
		
		//버튼과 리스너 연결 
		bt_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//choice 에서 선택한 계정으로 재 접속!!
				connectionManager.closeDB(con);
				String user=ch_user.getSelectedItem();
				String pass=new String(t_password.getPassword());
				
				con=connectionManager.connect(user, pass);
				if(con==null) {
					JOptionPane.showMessageDialog(GUIClient.this , "올바른 접속 정보를 입력하세요");
				}else {
					getUserTables();
				}
			}
		});
		
		//table_object와 마우스 리스너 연결 
		table_object.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row=table_object.getSelectedRow();
				int col=0;
				System.out.println(table_object.getValueAt(row, col));
				
				String tableName=(String)table_object.getValueAt(row, col);
				
				getRecords("select * from "+tableName);
			}	
		});
		
		//쿼리실행 버튼과 리스너 연결
		bt_sql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(area.getText().length()>0){
					executeSQL();
				}else {
					JOptionPane.showMessageDialog(GUIClient.this,"쿼리문을 입력하세요");
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(con !=null) {
					try {
						con.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);				
			}
		});
		
		connectionManager = new ConnectionManager();
		con=connectionManager.connect("system","manager");//버튼에 의해 접속하지 말고, 미리 연결해놓는다!!
		getUsers();
						//그래야 user 정보를 가져오니깐!!
		setSize(960 , 750);
		setVisible(true);		
		
	}
	
	//유져 계정 가져오기 
	public void getUsers() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select username from dba_users order by username asc";
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				ch_user.add(rs.getString("username"));
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			connectionManager.closeDB(pstmt, rs);
		}
		
	}
	
	//현재 계정이 보유한 테이블 목록 가져오기
	public void getUserTables() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select table_name,tablespace_name from user_tables";
		try {
			pstmt=con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			//컬럼 조사하기 
			ResultSetMetaData meta=rs.getMetaData();
			String[] columName=new String[meta.getColumnCount()];
			for(int i=0;i<meta.getColumnCount();i++) {
				columName[i]=meta.getColumnName(i+1);
				System.out.println(columName[i]);
			}
			//이차원 배열에 레코드 채워넣기!!
			rs.last();
			int total=rs.getRow();
			String[][] data=new String[total][columName.length];
			
			rs.beforeFirst();//원위치!!
			
			for(int i=0;i<total;i++) {
				rs.next();
				for(int a=0;a<columName.length;a++){
					data[i][a]=rs.getString(a+1);
				}
			}
			objectModel=new ObjectModel();
			objectModel.columName=columName;
			objectModel.data=data;//교체!!
			
			table_object.setModel(objectModel);
			table_object.updateUI();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			connectionManager.closeDB(pstmt, rs);
		}		
	}
	
	//선택한 테이블의 레코드 가져오기!!
	public void getRecords(String sql) {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			pstmt=con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			//메타 정보 얻어서 모델에 적용하자!!
			ResultSetMetaData meta=rs.getMetaData();
			String[] columnName=new String[meta.getColumnCount()];
			for(int i=0;i<columnName.length;i++) {
				columnName[i]=meta.getColumnName(i+1);
			}
			
			System.out.println(columnName.length);
			
			rs.last();
			int total=rs.getRow();//레코드 총 갯수
			String[][] data=new String[total][columnName.length];
			rs.beforeFirst();//원상 위치로
			for(int i=0;i<total;i++) {
				rs.next();//커서 한칸 전진!!
				for(int a=0;a<columnName.length;a++) {
					data[i][a]=rs.getString(a+1);
				}
			}
			//모델의 멤버변수에 덮어쓰기!!	
			resultModel= new ResultModel();
			resultModel.columnName=columnName;
			resultModel.data=data;
			
			//setModel의 적용시점은 모든 업무가 끝날때
			table_result.setModel(resultModel);
			table_result.updateUI();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			connectionManager.closeDB(pstmt, rs);
		}
		
	}
	
	public void executeSQL() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql=area.getText();
		
		try {
			pstmt=con.prepareStatement(sql);
			boolean result=pstmt.execute();
			if(result) {//true인 경우 select !!
				System.out.println("select문 수행했어?");
			}else {//DML 을 수행..
				System.out.println("DML 수행했어?");
			}
			getRecords(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new GUIClient();
	}
}







