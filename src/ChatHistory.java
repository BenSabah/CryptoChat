import java.awt.ComponentOrientation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChatHistory extends JTable {
	private static final long serialVersionUID = -5910362845911618735L;
	String[] titles = { "Time", "IP \\ Username", "Message" };
	int[] columnWidth = { 53, 85 };
	TableModel model;
	int latestLoc = 0;
	boolean isLTR = true;
	Object[] emptyCells;
	int expandBy = 10;

	public ChatHistory(int rows, int columns) {
		// Start a new JTable.
		super(rows, columns);
		model = getModel();

		// Set the table headers.
		getColumnModel().getColumn(0).setHeaderValue(titles[0]);
		getColumnModel().getColumn(1).setHeaderValue(titles[1]);
		getColumnModel().getColumn(2).setHeaderValue(titles[2]);

		// Set the table properties.
		setEnabled(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);

		// Set the table column size settings.
		getColumnModel().getColumn(0).setMaxWidth(columnWidth[0]);
		getColumnModel().getColumn(0).setMinWidth(columnWidth[0]);
		getColumnModel().getColumn(1).setMaxWidth(columnWidth[1]);
		getColumnModel().getColumn(1).setMinWidth(columnWidth[1]);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		getColumnModel().getColumn(0).setResizable(false);
		getColumnModel().getColumn(1).setResizable(false);
		getColumnModel().getColumn(2).setResizable(false);
	}

	/**
	 * Use this method to add a massage to the table it will also add a
	 * time-stamp to the message.
	 * 
	 * @param user
	 *            The user that adds the message to the table.
	 * @param msg
	 *            The message to add to the table.
	 */
	public void append(String user, String msg) {
		// Adding the message to the message to the table
		synchronized (model) {
			model.setValueAt(CoreUtils.getTime(), latestLoc, 0);
			model.setValueAt(user, latestLoc, 1);
			model.setValueAt(msg, latestLoc, 2);
		}

		// Move the selected line to the latest message we have.
		changeSelection(latestLoc, 1, false, false);
		latestLoc++;

		// Checking if we need to add more lines to the table.
		if (latestLoc == getRowCount()) {
			expandChat();
		}

		if (latestLoc % 5 == 4) {
			System.out.println("sdfsdf");
			setHeaders("זמן", "שם משתמש", "הודעה");
		}
	}

	/**
	 * Use this method to rename the table headers.
	 * 
	 * @param time
	 *            The new name to the time column.
	 * @param username
	 *            The new name to the user-name column.
	 * @param msg
	 *            The new name to the message column.
	 */
	public void setHeaders(String time, String username, String msg) {
		getColumnModel().getColumn(0).setHeaderValue(time);
		getColumnModel().getColumn(1).setHeaderValue(username);
		getColumnModel().getColumn(2).setHeaderValue(msg);
		updateUI();
	}

	/**
	 * This method is used to set by how much to expand the table when we reach
	 * the last cell.
	 * 
	 * @param expandBy
	 *            Set by how much to auto expand the table when the table is
	 *            full
	 */
	public void setExpandBy(int expandBy) {
		this.expandBy = expandBy;
	}

	private void expandChat() {
		synchronized (this) {
			for (int i = 0; i < expandBy; i++) {
				((DefaultTableModel) getModel()).addRow(emptyCells);
			}
		}
	}

	/**
	 * This method instructs the table to switch sides (from right-to-left TO
	 * left-to-right and vice-versa). for languages that read the other way.
	 */
	public void switchSides() {
		if (isLTR) {
			setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer())
					.setHorizontalAlignment(SwingConstants.RIGHT);
			((DefaultTableCellRenderer) getDefaultRenderer(Object.class)).setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer())
					.setHorizontalAlignment(SwingConstants.LEFT);
			((DefaultTableCellRenderer) getDefaultRenderer(Object.class)).setHorizontalAlignment(SwingConstants.LEFT);
		}
		isLTR = !isLTR;
	}

	/**
	 * This method returns the current state of the table as a single long
	 * string of plain text, the rows of the table are separated by a newline
	 * (machine-dependent).
	 * 
	 * @return The current state of the table, stripped of the HTML styling
	 */
	public String toString() {
		// Some needed variables.
		StringBuilder sb = new StringBuilder();
		String spacer = "%-15s";
		String newLine = System.lineSeparator();

		// Adding each line to the string builder with appropriate spacing to
		// each column.
		for (int i = 0; i < latestLoc; i++) {
			sb.append(String.format(spacer, model.getValueAt(i, 0)));
			sb.append(String.format(spacer, CoreUtils.clearHtml(model.getValueAt(i, 1).toString())));
			sb.append(model.getValueAt(i, 2) + newLine);
		}
		return sb.toString();
	}

	public String exportToFile(File path, String filenamePrefix, String filenameSuffix) {
		// Check if the path is indeed a directory.
		if (path.isFile()) {
			return "Please select a folder, not a file.";
		}

		// Check if the given path exists.
		if (!path.isDirectory() || !path.exists()) {
			return "Please select a valid path.";
		}

		File file;

		// Check if the file already exists, if so, try creating with a new
		// filename.
		String filePath = String.format("%s%s%s.%s", path.getAbsolutePath(), File.separator, filenamePrefix,
				filenameSuffix);
		file = new File(filePath);
		int j = 1;
		while (true) {
			if (file.exists()) {
				filePath = String.format("%s%s%s_Ver%d.%s", path.getAbsolutePath(), File.separator, filenamePrefix,
						j++, filenameSuffix);
				file = new File(filePath);
			} else {
				break;
			}
		}

		// Check if we can create the file in the given place.
		try {
			file.createNewFile();
		} catch (IOException e) {
			return "Couldn't Create the file.";
		}

		// Check if we can create an Excel object.
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook();
		} catch (Exception e) {
			return "Excel creator failed.";
		}

		// Start the excel file creation.
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow curRow;
		for (int i = 0; i < latestLoc; i++) {
			curRow = sheet.createRow(i);
			curRow.createCell(0).setCellValue(model.getValueAt(i, 0).toString());
			curRow.createCell(1).setCellValue(CoreUtils.clearHtml(model.getValueAt(i, 1).toString()));
			curRow.createCell(2).setCellValue(model.getValueAt(i, 2).toString());
			curRow = null;
		}

		try {
			workbook.write(new FileOutputStream(file));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Couldn't write the data into the file.";
		}

		// Return success if finished successfully.
		return "Export successful !";
	}
}