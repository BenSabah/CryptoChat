import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.awt.ComponentOrientation;

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class ChatHistory extends JTable {
	private static final long serialVersionUID = -5910362845911618735L;
	String[] titles = { "Time", "IP \\ Username", "Message" };
	TableModel model;
	int latestLoc = 0;
	boolean isLTR = true;
	Object[] emptyCells;
	Calendar currentTime = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public ChatHistory() {
		// Start a new JTable.
		super(20, 3);
		model = getModel();
		
		// Set the table headers.
		getColumnModel().getColumn(0).setHeaderValue(titles[0]);
		getColumnModel().getColumn(1).setHeaderValue(titles[1]);
		getColumnModel().getColumn(2).setHeaderValue(titles[2]);
		
		// Set the table property.
		setEnabled(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);
		getColumnModel().getColumn(2).setPreferredWidth(200);
		getColumnModel().getColumn(1).setPreferredWidth(10);
		getColumnModel().getColumn(0).setPreferredWidth(5);
		getColumnModel().getColumn(0).setResizable(false);
		getColumnModel().getColumn(1).setResizable(false);
	}

	public int getCurrentRow() {
		return latestLoc;
	}

	public void append(String user, String msg) {
		String time = sdf.format(Calendar.getInstance().getTime());
		synchronized (model) {
			model.setValueAt(time, latestLoc, 0);
			model.setValueAt(user, latestLoc, 1);
			model.setValueAt(msg, latestLoc, 2);
		}
		changeSelection(latestLoc, 1, false, false);
		latestLoc++;
		if (latestLoc == getRowCount()) {
			expandChat();
		}
	}

	private void expandChat() {
		synchronized (this) {
			for (int i = 0; i < 10; i++) {
				((DefaultTableModel) getModel()).addRow(emptyCells);
			}
		}
	}

	public void switchSides() {
		if (isLTR) {
			setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			((DefaultTableCellRenderer) getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.RIGHT);
		} else {
			setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			((DefaultTableCellRenderer) getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.LEFT);
		}
		isLTR = !isLTR;
	}

	public String toString() {
		return "";
	}
}