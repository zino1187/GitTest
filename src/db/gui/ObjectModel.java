/*���� ������ ������ ������ ��ü���� ����ϴ� �� ��Ű����
 ���̺�� �����ϴ� ��!!*/
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








