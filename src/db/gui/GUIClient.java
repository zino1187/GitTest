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
	JPanel p_west;//���� ����
	JPanel p_center;//���� ����
	JTextField t_url; //localhost
	JTextField t_sid; //XE
	JTextField t_port;
	JTextField t_user;
	Choice ch_user;
	JPasswordField t_password;
	JButton bt_connect;
	JTable table_object;//���� ������ ������ ��ü��
	JTable table_result;//select ���� ��� ���
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
		bt_connect = new JButton("����");
		table_object = new JTable(); 
		table_result = new JTable();
		area=new JTextArea();
		scroll=new JScrollPane(area);
		sc1=new JScrollPane(table_object);
		sc2=new JScrollPane(table_result);
		bt_sql=new JButton("��������");
		
		area.setFont(new Font("����",Font.BOLD,30));
		//���� ������ ������Ʈ ũ�⸦ 150����...
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
		//���� �гο� ����
		p_west.add(t_url);
		p_west.add(t_sid);
		p_west.add(t_port);
		//p_west.add(t_user);
		p_west.add(ch_user);
		
		p_west.add(t_password);
		p_west.add(bt_connect);
		//frame�� ���� 
		add(p_west, BorderLayout.WEST);
		
		//���� ������ �� ������Ʈ���� ũ�� ���� 
		sc1.setPreferredSize(new Dimension(780, 190));
		sc2.setPreferredSize(new Dimension(780, 190));
		scroll.setPreferredSize(new Dimension(780, 240));
		
		p_center.setBackground(Color.GREEN);
		p_center.add(sc1);
		p_center.add(sc2);
		p_center.add(scroll);
		p_center.add(bt_sql);
		add(p_center);
		
		//��ư�� ������ ���� 
		bt_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//choice ���� ������ �������� �� ����!!
				connectionManager.closeDB(con);
				String user=ch_user.getSelectedItem();
				String pass=new String(t_password.getPassword());
				
				con=connectionManager.connect(user, pass);
				if(con==null) {
					JOptionPane.showMessageDialog(GUIClient.this , "�ùٸ� ���� ������ �Է��ϼ���");
				}else {
					getUserTables();
				}
			}
		});
		
		//table_object�� ���콺 ������ ���� 
		table_object.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row=table_object.getSelectedRow();
				int col=0;
				System.out.println(table_object.getValueAt(row, col));
				
				String tableName=(String)table_object.getValueAt(row, col);
				
				getRecords("select * from "+tableName);
			}	
		});
		
		//�������� ��ư�� ������ ����
		bt_sql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(area.getText().length()>0){
					executeSQL();
				}else {
					JOptionPane.showMessageDialog(GUIClient.this,"�������� �Է��ϼ���");
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
		con=connectionManager.connect("system","manager");//��ư�� ���� �������� ����, �̸� �����س��´�!!
		getUsers();
						//�׷��� user ������ �������ϱ�!!
		setSize(960 , 750);
		setVisible(true);		
		
	}
	
	//���� ���� �������� 
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
	
	//���� ������ ������ ���̺� ��� ��������
	public void getUserTables() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select table_name,tablespace_name from user_tables";
		try {
			pstmt=con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			//�÷� �����ϱ� 
			ResultSetMetaData meta=rs.getMetaData();
			String[] columName=new String[meta.getColumnCount()];
			for(int i=0;i<meta.getColumnCount();i++) {
				columName[i]=meta.getColumnName(i+1);
				System.out.println(columName[i]);
			}
			//������ �迭�� ���ڵ� ä���ֱ�!!
			rs.last();
			int total=rs.getRow();
			String[][] data=new String[total][columName.length];
			
			rs.beforeFirst();//����ġ!!
			
			for(int i=0;i<total;i++) {
				rs.next();
				for(int a=0;a<columName.length;a++){
					data[i][a]=rs.getString(a+1);
				}
			}
			objectModel=new ObjectModel();
			objectModel.columName=columName;
			objectModel.data=data;//��ü!!
			
			table_object.setModel(objectModel);
			table_object.updateUI();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			connectionManager.closeDB(pstmt, rs);
		}		
	}
	
	//������ ���̺��� ���ڵ� ��������!!
	public void getRecords(String sql) {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			pstmt=con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			//��Ÿ ���� �� �𵨿� ��������!!
			ResultSetMetaData meta=rs.getMetaData();
			String[] columnName=new String[meta.getColumnCount()];
			for(int i=0;i<columnName.length;i++) {
				columnName[i]=meta.getColumnName(i+1);
			}
			
			System.out.println(columnName.length);
			
			rs.last();
			int total=rs.getRow();//���ڵ� �� ����
			String[][] data=new String[total][columnName.length];
			rs.beforeFirst();//���� ��ġ��
			for(int i=0;i<total;i++) {
				rs.next();//Ŀ�� ��ĭ ����!!
				for(int a=0;a<columnName.length;a++) {
					data[i][a]=rs.getString(a+1);
				}
			}
			//���� ��������� �����!!	
			resultModel= new ResultModel();
			resultModel.columnName=columnName;
			resultModel.data=data;
			
			//setModel�� ��������� ��� ������ ������
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
			if(result) {//true�� ��� select !!
				System.out.println("select�� �����߾�?");
			}else {//DML �� ����..
				System.out.println("DML �����߾�?");
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







