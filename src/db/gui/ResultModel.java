//유저가 선택한 테이블에 대한 모델!!
package db.gui;

import javax.swing.table.AbstractTableModel;

public class ResultModel extends AbstractTableModel{
	String[] columnName=new String[1];
	String[][] data=new String[1][1];
	
	public int getRowCount() {
		return data.length;
	}
	public int getColumnCount() {
		return columnName.length;
	}
	public String getColumnName(int col) {
		return columnName[col];
	}
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	
}

