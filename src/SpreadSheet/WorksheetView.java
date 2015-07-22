package SpreadSheet;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class WorksheetView extends JTable implements TableModel{
	private static final int NUMCOL = 26;
	private static final int FIRSTCOLUMNWIDTH = 30;
	private static final int COLUMNWIDTH = 60;
	private static int NUMROW = 20;
	private static final long serialVersionUID = 1L;

	WorkSheet worksheet;

	ArrayList<TableModelListener> listeners;
	ArrayList<SelectionObserver> observers;

	private Map currentCopyItems = new HashMap(); 
	private Map currentShape = new HashMap();// 

	public WorksheetView(WorkSheet worksheet) {
		this.worksheet = worksheet;
		listeners = new ArrayList<TableModelListener>();
		observers = new ArrayList<SelectionObserver>();
		this.setModel(this);
		this.setCellSelectionEnabled(true);
		this.setRowSelectionAllowed(true);
		this.setColumnSelectionAllowed(true);
		this.setShowGrid(true);
		this.setGridColor(Color.LIGHT_GRAY);
		this.getColumnModel().getSelectionModel().addListSelectionListener(this);
		(this.getSelectionModel()).addListSelectionListener(this);
		this.getColumnModel().setColumnSelectionAllowed(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int col = 0; col < NUMCOL + 1; col++) {
			this.getColumnModel().getColumn(col).setPreferredWidth(col == 0 ? FIRSTCOLUMNWIDTH : COLUMNWIDTH);
		}
		this.addMouseListener(new TableMouseListener());
		this.getTableHeader().addMouseListener(new TabelHeaderMouseListener());
	}
	
	// getCellRenderer - provides the renderers for the cells. It overrides the default method in JTable
	// Note the first column is just the index.
	public TableCellRenderer getCellRenderer(int row, int column) {
		if ((column == 0)) {
			return new TableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,int row, int column) {
					JLabel lab = new JLabel("" + (row + 1));
					lab.setOpaque(true);
					lab.setBackground(Color.gray);
					return lab;
				}
			};
		}
		
		return new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,int row, int column) {
				JLabel lab = new JLabel("" + ((Cell) value).show());
				Color color = ((Cell) value).getBackground();
				if (isSelected || hasFocus || (table.getSelectedColumn() == column && table.getSelectedRow() == row)) {
					//lab.setOpaque(true);
					//lab.setBackground(Color.lightGray);
					lab.setBorder(BorderFactory.createLineBorder(Color.BLUE));
				}
				
				if (color!=null){
					lab.setOpaque(true);
					lab.setBackground(color);
				}
				return lab;
			}
		};
	}

	@Override
	public int getColumnCount() {
		return NUMCOL + 1;
	}

	@Override
	public int getRowCount() {
		return NUMROW;
	}
	
	public void setRowCount(int rowNum){
		NUMROW = rowNum+NUMROW;
		this.repaint();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return worksheet.lookup(new CellIndex(columnIndex - 1, rowIndex));
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "";
		return (char) ('A' + (char) (columnIndex - 1)) + "";
	}

	// Make the cell editable
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		CellIndex index = new CellIndex(columnIndex-1,rowIndex);
		Cell cell = worksheet.lookup(index);
		cell.setText(aValue.toString());
		// automatically calculate:
		worksheet.calculate();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		informObservers();
	}

	public void addSelectionObserver(SelectionObserver o) {
		observers.add(o);
	}

	private void informObservers() {
		for (SelectionObserver o : observers) {
			o.update();
		}
	}

	public CellIndex getSelectedIndex() {
		return new CellIndex(this.getSelectedColumn() - 1,
				this.getSelectedRow());
	}

	public void setWorksheet(WorkSheet worksheet) {
		this.worksheet = worksheet;
		this.repaint();
	}
	
	
	//
	private class TabelHeaderMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				JTableHeader header = (JTableHeader) e.getSource();
				int pick = header.columnAtPoint(e.getPoint());
				setColumnSelectionInterval(pick, pick);
				setRowSelectionInterval(0, NUMROW - 1);
			}
		}
	}
	
	
	//
	private class TableMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {//
				CellIndex index = getSelectedIndex();
				if (null != index) {
					if (index.column == -1) {
						WorksheetView.this.setRowSelectionInterval(index.row,index.row);
						WorksheetView.this.setColumnSelectionInterval(0, NUMCOL);
					}
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu popupmenu = new JPopupMenu();
				JMenuItem menuItem1 = new JMenuItem("copy");
				JMenuItem menuItem2 = new JMenuItem("paste");
				menuItem1.setActionCommand("copy");
				menuItem2.setActionCommand("paste");
				menuItem1.addActionListener(new MenuListner());
				menuItem2.addActionListener(new MenuListner());
				popupmenu.add(menuItem1);
				popupmenu.add(menuItem2);
				popupmenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

	}
	
	
	//
	private class MenuListner implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("copy")) {
				currentShape.put("width", getSelectedColumns().length);
				currentShape.put("height", getSelectedRows().length);
				for(int i=0;i<getSelectedRows().length;i++){
					int row  = getSelectedRows()[i];
					for(int j=0;j<getSelectedColumns().length;j++){
						int column  = getSelectedColumns()[j];
						currentCopyItems.put(i+";"+j, getValueAt(row, column));
					}
				}
			}else{
				//
				if(!currentCopyItems.isEmpty()){
					int choseWidth = getSelectedColumns().length;
					int choseHeight = getSelectedRows().length;
					int copyWidth = (Integer) currentShape.get("width");
					int copyHeight = (Integer) currentShape.get("height");
					if((copyWidth==1&&copyHeight==1)||(copyWidth==choseWidth&&copyHeight==choseHeight)){//
						for(int i=0;i<getSelectedRows().length;i++){
							int row  = getSelectedRows()[i];
							for(int j=0;j<getSelectedColumns().length;j++){
								int column  = getSelectedColumns()[j];
								Object value= 	currentCopyItems.get(i+";"+j);
								if(copyWidth==1&&copyHeight==1){
									value = currentCopyItems.get("0;0");
								}
								setValueAt(value, row, column);
								repaint();
							}
						}
					}else{
						
					}
				}
			}
		}

	}
}
