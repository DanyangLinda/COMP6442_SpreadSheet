
package SpreadSheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Spreadsheet implements Runnable, ActionListener,SelectionObserver, DocumentListener {
	private static final String EXITCOMMAND = "exitcommand";
	private static final String CLEARCOMMAND = "clearcommand";
	private static final String SAVECOMMAND = "savecommand";
	private static final String OPENCOMMAND = "opencommand";
	private static final String EDITFUNCTIONCOMMAND = "editfunctioncommand";
	private static final String CALCULATE = "calculate";
	private static final String ADDSHEET = "addsheet";
	private static final String ADDROW = "addrow";
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	
	JFrame jframe;
	FunctionEditor functioneditor;
	JButton calculateButton;
	JButton addsheetButton;
	JButton addrowButton;
	JTextField rowField;
	JTextField cellEditTextField;
	JLabel selectedCellLabel;
	JFileChooser filechooser = new JFileChooser();
	JTabbedPane sheetTabPanel = new JTabbedPane(JTabbedPane.BOTTOM);
	WorkSheetGroup sheetMap = new WorkSheetGroup();
	Map sheetViewMap = new LinkedHashMap();
	WorksheetView currentWorkSheetView;
	WorkSheet currentWorkSheet;
//	WorkSheet worksheet;
//	WorksheetView worksheetview;
	JPopupMenu popMenu = new JPopupMenu();

	public Spreadsheet() {
		SwingUtilities.invokeLater(this);
	}

	public static void main(String[] args) {
		new Spreadsheet();
	}

	public void run() {
		jframe = new JFrame("Spreadsheet");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set up the menu bar
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");
		bar.add(menu);
		makeMenuItem(menu, "New", CLEARCOMMAND);
		makeMenuItem(menu, "Open", OPENCOMMAND);
		makeMenuItem(menu, "Save", SAVECOMMAND);
		makeMenuItem(menu, "Exit", EXITCOMMAND);
		menu = new JMenu("Edit");
		bar.add(menu);
		makeMenuItem(menu, "EditFunction", EDITFUNCTIONCOMMAND);
		jframe.setJMenuBar(bar);

		// set up the tool area
		JPanel toolarea = new JPanel();
		addsheetButton = new JButton("Add Sheet");
		addsheetButton.addActionListener(this);
		addsheetButton.setActionCommand(ADDSHEET);
		toolarea.add(addsheetButton);
		addrowButton = new JButton("Add Row");
		addrowButton.addActionListener(this);
		addrowButton.setActionCommand(ADDROW);
		toolarea.add(addrowButton);
		rowField = new JTextField(5);
		rowField.setText("1");
		toolarea.add(rowField);
		calculateButton = new JButton("Calculate");
		calculateButton.addActionListener(this);
		calculateButton.setActionCommand(CALCULATE);
		toolarea.add(calculateButton);
		selectedCellLabel = new JLabel("--");
		selectedCellLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		toolarea.add(selectedCellLabel);
		cellEditTextField = new JTextField(20);
		cellEditTextField.getDocument().addDocumentListener(this);
		toolarea.add(cellEditTextField);
		makeColorLabel("red",Color.red,toolarea);
		makeColorLabel("blue",Color.blue,toolarea);
		makeColorLabel("yellow",Color.yellow,toolarea);
		makeColorLabel("green",Color.green,toolarea);
		
		
		//set pop Menu
		JMenuItem item1 = new JMenuItem("delete");
		item1.setActionCommand("delete");
		item1.addActionListener(new SheetActionListener());
		JMenuItem item2 = new JMenuItem("rename");
		item2.setActionCommand("rename");
		item2.addActionListener(new SheetActionListener());
		popMenu.add(item1);
		popMenu.add(item2);
		sheetTabPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				changeSheetByTab();
			}
		});
		
		jframe.getContentPane().add(sheetTabPanel, BorderLayout.CENTER);
		refreshsheetTabPanel();
		jframe.getContentPane().add(toolarea, BorderLayout.PAGE_START);
		
		//make the frame appear in the center of the window
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		int x = (width - WIDTH) / 2;
		int y = (height - HEIGHT) / 2;
		jframe.setSize(WIDTH, HEIGHT);
		jframe.setLocation(x, y);	
		jframe.setVisible(true);

		// automatically calculate and refresh:
		currentWorkSheet.calculate();
		for(Object sheet : this.sheetViewMap.keySet()) {
			WorksheetView view = (WorksheetView) this.sheetViewMap.get(sheet);
			view.repaint();
		}
		//this.worksheet = this.currentWorkSheet;
		//this.worksheetview = this.currentWorkSheetView;
	}

	private void changeSheetByTab() {
		if (sheetTabPanel.getSelectedIndex() >= 0) {
			String title = sheetTabPanel.getTitleAt(sheetTabPanel.getSelectedIndex());
			currentWorkSheet = (WorkSheet) sheetMap.get(title);
			currentWorkSheetView = (WorksheetView) sheetViewMap.get(title);
			functioneditor = new FunctionEditor(currentWorkSheet);
			this.worksheetChange();
		}
	}

	private void refreshsheetTabPanel() {
		sheetTabPanel.removeAll();
		if (sheetMap.isEmpty()) {
			WorkSheet sheet1 = new WorkSheet();
			sheetMap.put("sheet1", sheet1);
			sheetViewMap.put("sheet1", new WorksheetView(sheet1));
		}
		for (Object o : sheetViewMap.keySet()) {
			WorksheetView worksheetView = (WorksheetView) sheetViewMap.get(o);
			worksheetView.addSelectionObserver(this);
			sheetTabPanel.add(o.toString(), new JScrollPane(worksheetView));
		}
		
		sheetTabPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON3){
					popMenu.show(e.getComponent(),e.getX(),e.getY());
					return;
				}
			}
		});

		sheetTabPanel.revalidate();
		sheetTabPanel.repaint();
		sheetTabPanel.setSelectedIndex(sheetTabPanel.getComponentCount()-1);
		changeSheetByTab();
	}

	private void makeMenuItem(JMenu menu, String name, String command) {
		JMenuItem menuitem = new JMenuItem(name);
		menu.add(menuitem);
		menuitem.addActionListener(this);
		menuitem.setActionCommand(command);
	}
	
	private void makeColorLabel(String name, final Color color, JPanel toolarea){
		JLabel label= new JLabel();
		label.setOpaque(true);
		label.setBackground(color);	
		label.setPreferredSize(new Dimension(15,15));
		toolarea.add(label);
		label.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				CellIndex index = currentWorkSheetView.getSelectedIndex();
				Cell cell = currentWorkSheet.lookup(index);
				cell.setBackground(color);
    			currentWorkSheetView.repaint();
    			jframe.repaint();
    			}
            });	
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(EXITCOMMAND)) {
			exit();
		} 
		else if (ae.getActionCommand().equals(SAVECOMMAND)) {
			filechooser.setCurrentDirectory(new java.io.File("."));
			filechooser.setDialogTitle("Save");
			filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int res = filechooser.showSaveDialog(jframe);
			if (res == JFileChooser.APPROVE_OPTION) {
				this.sheetMap.save(filechooser.getSelectedFile());
			}
		} 
		else if (ae.getActionCommand().equals(OPENCOMMAND)) {
			int res = filechooser.showOpenDialog(jframe);
			if (res == JFileChooser.APPROVE_OPTION) {
				this.sheetMap = new WorkSheetGroup().load(filechooser.getSelectedFile());
				sheetViewMap.clear();
				for(String ws: sheetMap.keySet()){
					sheetViewMap.put(ws, new WorksheetView(sheetMap.get(ws)));				
				}
						
				refreshsheetTabPanel();
				worksheetChange();
			}
		} 
		else if (ae.getActionCommand().equals(CLEARCOMMAND)) {
			sheetMap.clear();
			sheetViewMap.clear();
			refreshsheetTabPanel();
			worksheetChange();
		} 
		else if (ae.getActionCommand().equals(EDITFUNCTIONCOMMAND)) {
			functioneditor.setVisible(true);

		} 
		else if (ae.getActionCommand().equals(CALCULATE)) {
			//this.worksheet.calculate();
			currentWorkSheet.calculate();
			currentWorkSheetView.repaint();
		}
		else if (ae.getActionCommand().equals(ADDSHEET)){
			WorkSheet newsheet = new WorkSheet();
			sheetMap.put("sheet"+Integer.toString(sheetMap.size()+1), newsheet);
			sheetViewMap.put("sheet"+Integer.toString(sheetMap.size()), new WorksheetView(newsheet));
			refreshsheetTabPanel();
		}
		else if (ae.getActionCommand().equals(ADDROW)){
			int rowNum = Integer.parseInt(rowField.getText());
			currentWorkSheetView.setRowCount(rowNum);	
			currentWorkSheetView.revalidate();
			currentWorkSheetView.repaint();			
        }
	}

	private void worksheetChange() {
		currentWorkSheet.calculate();
		currentWorkSheetView.setWorksheet(currentWorkSheet);
		functioneditor.setWorksheet(currentWorkSheet);
		currentWorkSheetView.repaint();
	}

	private void exit() {
		System.exit(0);
	}

	@Override
	public void update() {
		CellIndex index = currentWorkSheetView.getSelectedIndex();
		selectedCellLabel.setText(index.show());
		cellEditTextField.setText(currentWorkSheet.lookup(index).getText());	
		currentWorkSheetView.repaint();
	}

	// The following three override methods implement DocumentListener which is listening the change of the JTextField
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		textChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		textChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		textChanged();
	}

	private void textChanged() {
		CellIndex index = currentWorkSheetView.getSelectedIndex();
		Cell current = currentWorkSheet.lookup(index);
		current.setText(cellEditTextField.getText());
		currentWorkSheetView.repaint();
	}

	private class SheetActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("delete")) {
				if (sheetMap.size() == 1) {
					JOptionPane.showMessageDialog(jframe, "This is the last sheet!");
					return;
				}
				String title = sheetTabPanel.getTitleAt(sheetTabPanel.getSelectedIndex());
				sheetMap.remove(title);
				sheetViewMap.remove(title);
				refreshsheetTabPanel();
			}
			if (e.getActionCommand().equals("rename")) {
				String title = sheetTabPanel.getTitleAt(sheetTabPanel.getSelectedIndex());
				String result = JOptionPane.showInputDialog("Please input the name.");
				Map tempMap = new LinkedHashMap();
				WorkSheet rSheet = (WorkSheet) sheetMap.get(title);
				WorksheetView rSheetView = (WorksheetView) sheetViewMap
						.get(title);

				for (Object o : sheetViewMap.keySet()) {
					if (o.toString().equals(title)) {
						break;
					}
					tempMap.put(o, sheetViewMap.get(o));
				}
				if (null != result && !result.trim().isEmpty()) {
					sheetMap.remove(title);
					sheetMap.put(result, rSheet);
					tempMap.put(result, rSheetView);
					sheetViewMap.remove(title);
					tempMap.putAll(sheetViewMap);
					sheetViewMap = tempMap;
					refreshsheetTabPanel();
				}
			}

		}

	}
}
