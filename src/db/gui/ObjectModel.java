/*현재 접속한 계정이 보유한 객체들을 출력하는 즉 스키마를
 테이블과 연결하는 모델!!*/
package db.gui;
import javax.swing.table.AbstractTableModel;
public class ObjectModel extends AbstractTableModel{
	String[] columName=new String[1];
	String[][] data=new String[1][1];
	
	public ObjectModel() {
		
	}
	public int getRowCount() {
		return data.length;
	}
	public int getColumnCount() {
		return columName.length;
	}
	public String getColumnName(int col) {
		return columName[col];
	}
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
}








