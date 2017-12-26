package com.shuishou.sysmgr.ui.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Category2Printer;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Permission;
import com.shuishou.sysmgr.beans.Printer;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;

public class Category2Panel extends JPanel implements CommonDialogOperatorIFC{
	private final Logger logger = Logger.getLogger(Category2Panel.class.getName());
	private MenuMgmtPanel parent;
	private JTextField tfFirstLanguageName= new JTextField(155);
	private JTextField tfSecondLanguageName= new JTextField(155);
	private JTextField tfDisplaySeq= new JTextField(155);
	private JComboBox<Category1> cbCategory1 = new JComboBox();
	private JRadioButton rbPrintTogether = new JRadioButton(Messages.getString("Category2Panel.PrintTogether"), false);
	private JRadioButton rbPrintSeparately = new JRadioButton(Messages.getString("Category2Panel.PrintSeparately"), true);
	private JList<PrinterChoosed> listPrinter = new JList<>();
	private DefaultListModel<PrinterChoosed> modelPrinter = new DefaultListModel<>();
	private Category2 c2;
	private Gson gson = new Gson();
	private Category1 parentCategory1;
	public Category2Panel(MenuMgmtPanel parent){
		this.parent = parent;
		initUI();
		initData();
	}
	
	public Category2Panel(MenuMgmtPanel parent, Category1 parentCategory1){
		this.parent = parent;
		this.parentCategory1 = parentCategory1;
		initUI();
		initData();
	}
	
	private void initUI(){
		JLabel lbFirstLanguageName = new JLabel("First Language Name");
		JLabel lbSecondLanguageName = new JLabel("Second Language Name");
		JLabel lbDisplaySeq = new JLabel("Display Sequence");
		JLabel lbCategory1 = new JLabel("Category1");
		ButtonGroup bgPrintStyle = new ButtonGroup();
		bgPrintStyle.add(rbPrintSeparately);
		bgPrintStyle.add(rbPrintTogether);
		JPanel pPrintStyle = new JPanel(new GridLayout(0, 1));
		pPrintStyle.add(rbPrintSeparately);
		pPrintStyle.add(rbPrintTogether);
		
		listPrinter.setBorder(BorderFactory.createTitledBorder("Printer"));
		listPrinter.setModel(modelPrinter);
		listPrinter.setCellRenderer(new PrinterListRenderer());
		listPrinter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPrinter.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = listPrinter.locationToIndex(e.getPoint());

				if (index != -1) {
					PrinterChoosed pc = listPrinter.getModel().getElementAt(index);
					pc.isChoosed = !pc.isChoosed;
					repaint();
				}
			}
		});
		this.setLayout(new GridBagLayout());
		add(lbFirstLanguageName, 	new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfFirstLanguageName, 	new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbSecondLanguageName, 	new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfSecondLanguageName, 	new GridBagConstraints(1, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbDisplaySeq, 	new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfDisplaySeq, 	new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbCategory1, 	new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(cbCategory1, 	new GridBagConstraints(1, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(listPrinter,	new GridBagConstraints(0, 4, 2, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(pPrintStyle,	new GridBagConstraints(1, 4, 2, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(new JPanel(), new GridBagConstraints(0, 5, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

		tfDisplaySeq.setMinimumSize(new Dimension(180,25));
		tfFirstLanguageName.setMinimumSize(new Dimension(180,25));
		tfSecondLanguageName.setMinimumSize(new Dimension(180,25));
		cbCategory1.setMinimumSize(new Dimension(180,25));
		
		tfDisplaySeq.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9'))) {
					getToolkit().beep();
					e.consume();
				} 
			}
		});
	}

	private void initData(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		ArrayList<Printer> listPrinter = parent.getMainFrame().getListPrinters();
		cbCategory1.removeAllItems();
		for(Category1 c1 : listCategory1){
			cbCategory1.addItem(c1);
		}
		if (parentCategory1 != null){
			cbCategory1.setSelectedItem(parentCategory1);
		}
		if (listPrinter != null){
			for(Printer p : listPrinter){
				PrinterChoosed pc = new PrinterChoosed(p, false);
				modelPrinter.addElement(pc);
			}
		}
		
	}
	
	@Override
	public boolean doSave() {
		if (!doCheckInput())
			return false;
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("firstLanguageName", tfFirstLanguageName.getText());
		if (tfSecondLanguageName.getText() != null)
			params.put("secondLanguageName", tfSecondLanguageName.getText());
		params.put("sequence", tfDisplaySeq.getText());
		params.put("category1Id", ((Category1)cbCategory1.getSelectedItem()).getId() + "");
		if (rbPrintSeparately.isSelected()){
			params.put("sequence", String.valueOf(ConstantValue.CATEGORY2_PRINT_TYPE_SEPARATELY));
		} else if (rbPrintTogether.isSelected()){
			params.put("sequence", String.valueOf(ConstantValue.CATEGORY2_PRINT_TYPE_TOGETHER));
		}
		ArrayList<Integer> printerIds = new ArrayList<>();
		for (int i = 0; i < modelPrinter.size(); i++) {
			if (modelPrinter.getElementAt(i).isChoosed) {
				printerIds.add(modelPrinter.get(i).printer.getId());
			}
		}
		params.put("printerIds", gson.toJson(printerIds));
		String url = "menu/add_category2";
		if (c2 != null){
			url = "menu/update_category2";
			params.put("id", c2.getId() + "");
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update category2. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update category2. URL = " + url);
			return false;
		}
		HttpResult<Category2> result = gson.fromJson(response, new TypeToken<HttpResult<Category2>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update category2. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while add/update category2. URL = " + url + ", response = "+response);
			return false;
		}
		result.data.setCategory1((Category1)cbCategory1.getSelectedItem());
		if (c2 == null){
			parent.insertNode(result.data);
		} else {
			parent.updateNode(result.data, c2);
		}
		return true;
	}
	
	private boolean doCheckInput(){
		if (tfFirstLanguageName.getText() == null || tfFirstLanguageName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input First Language Name");
			return false;
		}
//		if (tfSecondLanguageName.getText() == null || tfSecondLanguageName.getText().length() == 0){
//			JOptionPane.showMessageDialog(this, "Please input Second Language Name");
//			return false;
//		}
		if (tfDisplaySeq.getText() == null || tfDisplaySeq.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Display Sequence");
			return false;
		}
		boolean hasPrinter = false;
		for (int i = 0; i < modelPrinter.size(); i++) {
			if (modelPrinter.getElementAt(i).isChoosed) {
				hasPrinter = true;
				break;
			}
		}
		if (!hasPrinter){
			JOptionPane.showMessageDialog(this, "Please choose Printer");
			return false;
		}
		if (cbCategory1.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(this, "Please input Cagetory1");
			return false;
		}
		return true;
	}
	
	public void setObjectValue(Category2 c2){
		this.c2 = c2;
		tfFirstLanguageName.setText(c2.getFirstLanguageName());
		tfSecondLanguageName.setText(c2.getSecondLanguageName());
		tfDisplaySeq.setText(c2.getSequence()+"");
		if (c2.getPrintStyle() == ConstantValue.CATEGORY2_PRINT_TYPE_SEPARATELY){
			rbPrintSeparately.setSelected(true);
		} else if (c2.getPrintStyle() == ConstantValue.CATEGORY2_PRINT_TYPE_TOGETHER){
			rbPrintTogether.setSelected(true);
		}
		cbCategory1.setSelectedItem(c2.getCategory1());
		for (int i = 0; i < modelPrinter.size(); i++) {
			modelPrinter.getElementAt(i).isChoosed = false;
		}
		List<Category2Printer> cps = c2.getCategory2PrinterList();
		if (cps != null && !cps.isEmpty()){
			for(Category2Printer cp : cps){
				for (int i = 0; i < modelPrinter.size(); i++) {
					if (cp.getPrinter().getId() == modelPrinter.getElementAt(i).printer.getId()) {
						modelPrinter.getElementAt(i).isChoosed = true;
					}
				}
			}
		}
		listPrinter.updateUI();
	}
	
	public void refreshCategory1List(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		cbCategory1.removeAllItems();
		for(Category1 c1 : listCategory1){
			cbCategory1.addItem(c1);
		}
	}
	
	public void refreshPrinterList(){
		modelPrinter.clear();
		ArrayList<Printer> listPrinter = parent.getMainFrame().getListPrinters();
		if (listPrinter != null){
			for(Printer p : listPrinter){
				modelPrinter.addElement(new PrinterChoosed(p, false));
			}
		}
		
	}

	class Category1ListRender extends JLabel implements ListCellRenderer{
		
		public Category1ListRender(){}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			setText(((Category1)value).getFirstLanguageName());
			return this;
		}
	}
	
	class PrinterListRenderer extends JCheckBox implements ListCellRenderer{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			PrinterChoosed pc = (PrinterChoosed)value;
			this.setText(pc.printer.getName());
			this.setSelected(pc.isChoosed);
			return this;
		}
	}
	
	class PrinterChoosed{
		Printer printer;
		boolean isChoosed = false;
		public PrinterChoosed(Printer p, boolean c){
			printer = p;
			isChoosed = c;
		}
	}
}
