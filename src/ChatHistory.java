import java.awt.Font;
import java.awt.Component;
import java.awt.ComponentOrientation;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class ChatHistory extends JTable {
	DefaultTableCellRenderer timeAndUserNameRenderer = new DefaultTableCellRenderer();
	TextAreaRenderer msgRenderer = new TextAreaRenderer();
	private static final long serialVersionUID = -5910362845911618735L;
	String[] titles = { "Time", "IP \\ Username", "Message" };
	int[] columnWidth = { 53, 85 };
	TableModel model;
	int curRow = 0;
	boolean isLTR = true;
	Object[] emptyCells;
	int expandBy = 10;
	static boolean showVerbose = true;

	public ChatHistory() {
		// Start a new JTable.
		super(20, 3);
		model = getModel();

		// Set the table headers.
		columnModel.getColumn(0).setHeaderValue(titles[0]);
		columnModel.getColumn(1).setHeaderValue(titles[1]);
		columnModel.getColumn(2).setHeaderValue(titles[2]);

		// Set the table properties.
		setEnabled(false);
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		timeAndUserNameRenderer.setHorizontalAlignment(JLabel.CENTER);
		setFont(new Font(getFont().getFontName(), getFont().getStyle(), 12));

		// Set the table renderers.
		getColumnModel().getColumn(0).setCellRenderer(timeAndUserNameRenderer);
		getColumnModel().getColumn(1).setCellRenderer(timeAndUserNameRenderer);
		getColumnModel().getColumn(2).setCellRenderer(msgRenderer);

		// Set the table column size settings.
		columnModel.getColumn(0).setMaxWidth(columnWidth[0]);
		columnModel.getColumn(0).setMinWidth(columnWidth[0]);
		columnModel.getColumn(1).setMaxWidth(columnWidth[1]);
		columnModel.getColumn(1).setMinWidth(columnWidth[1]);
		columnModel.getColumn(0).setResizable(false);
		columnModel.getColumn(1).setResizable(false);
		columnModel.getColumn(2).setResizable(false);
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
			model.setValueAt(CoreUtils.getTime(true), curRow, 0);
			model.setValueAt(user, curRow, 1);
			model.setValueAt(msg, curRow, 2);
		}

		// Move the selected line to the latest message we have.
		changeSelection(curRow, 1, false, false);

		curRow++;
		// Checking if we need to add more lines to the table.
		if (curRow == getRowCount()) {
			expandChat();
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
			getColumnModel().getColumn(0).setCellRenderer(timeAndUserNameRenderer);
			getColumnModel().getColumn(1).setCellRenderer(timeAndUserNameRenderer);
			getColumnModel().getColumn(2).setCellRenderer(msgRenderer);
			updateUI();
		}
	}

	/**
	 * This method instructs the table to switch sides (from right-to-left TO
	 * left-to-right and vice-versa). for languages that read the other way.
	 */
	public void switchSides() {
		// Check the current direction and reverse it.
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
		for (int i = 0; i < curRow; i++) {
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
		XSSFRow row;
		for (int i = 0; i < curRow; i++) {
			row = sheet.createRow(i);
			row.createCell(0).setCellValue(model.getValueAt(i, 0).toString());
			row.createCell(1).setCellValue(CoreUtils.clearHtml(model.getValueAt(i, 1).toString()));
			row.createCell(2).setCellValue(CoreUtils.clearHtml(model.getValueAt(i, 2).toString()));
			row = null;
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

	/**
	 * The standard class for rendering (displaying) individual cells in a
	 * JTable. This class inherits from JTextArea, a standard component class.
	 * However JTextArea is a multi-line area that displays plain text.
	 * 
	 * This class implements TableCellRenderer , i.e. interface. This interface
	 * defines the method required by any object that would like to be a
	 * renderer for cells in a JTable.
	 * 
	 * @author Manivel F
	 */
	public class TextAreaRenderer extends JTextArea implements TableCellRenderer {
		// General variables.
		private static final long serialVersionUID = -3645487575566489913L;
		private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		private final Map<JTable, Map<Object, Map<Object, Integer>>> tableCellSizes = new HashMap<JTable, Map<Object, Map<Object, Integer>>>();

		/**
		 * Creates a text area renderer.
		 */
		public TextAreaRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		/**
		 * Returns the component used for drawing the cell. This method is used
		 * to configure the renderer appropriately before drawing.
		 * 
		 * @param table
		 *            - JTable object
		 * @param value
		 *            - the value of the cell to be rendered.
		 * @param isSelected
		 *            - isSelected true if the cell is to be rendered with the
		 *            selection highlighted; otherwise false.
		 * @param hasFocus
		 *            - if true, render cell appropriately.
		 * @param row
		 *            - The row index of the cell being drawn.
		 * @param column
		 *            - The column index of the cell being drawn.
		 * @return - Returns the component used for drawing the cell.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			// Set the Font, Color, etc.
			renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setForeground(renderer.getForeground());
			setBackground(renderer.getBackground());
			setBorder(renderer.getBorder());
			setFont(renderer.getFont());
			setText(renderer.getText());

			TableColumnModel columnModel = table.getColumnModel();
			setSize(columnModel.getColumn(column).getWidth(), 0);
			int height_wanted = (int) getPreferredSize().getHeight();
			addSize(table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize(table, row);
			if (height_wanted != table.getRowHeight(row)) {
				table.setRowHeight(row, height_wanted);
			}
			return this;
		}

		/**
		 * @param table
		 *            - JTable object
		 * @param row
		 *            - The row index of the cell being drawn.
		 * @param column
		 *            - The column index of the cell being drawn.
		 * @param height
		 *            - Row cell height as int value This method will add size
		 *            to cell based on row and column number
		 */
		private void addSize(JTable table, int row, int column, int height) {
			Map<Object, Map<Object, Integer>> rowsMap = tableCellSizes.get(table);
			if (rowsMap == null) {
				tableCellSizes.put(table, rowsMap = new HashMap<Object, Map<Object, Integer>>());
			}
			Map<Object, Integer> rowheightsMap = rowsMap.get(row);
			if (rowheightsMap == null) {
				rowsMap.put(row, rowheightsMap = new HashMap<Object, Integer>());
			}
			rowheightsMap.put(column, height);
		}

		/**
		 * Look through all columns and get the renderer. If it is also a
		 * TextAreaRenderer, we look at the maximum height in its hash table for
		 * this row.
		 * 
		 * @param table
		 *            -JTable object
		 * @param row
		 *            - The row index of the cell being drawn.
		 * @return row maximum height as integer value
		 */
		private int findTotalMaximumRowSize(JTable table, int row) {
			int maximum_height = 0;
			Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tc = columns.nextElement();
				TableCellRenderer cellRenderer = tc.getCellRenderer();
				if (cellRenderer instanceof TextAreaRenderer) {
					TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
					maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
				}
			}
			return maximum_height;
		}

		/**
		 * This will find the maximum row size
		 * 
		 * @param table
		 *            - JTable object
		 * @param row
		 *            - The row index of the cell being drawn.
		 * @return row maximum height as integer value
		 */
		private int findMaximumRowSize(JTable table, int row) {
			Map<Object, Map<Object, Integer>> rows = tableCellSizes.get(table);
			if (rows == null) {
				return 0;
			}
			Map<Object, Integer> rowheights = rows.get(row);
			if (rowheights == null) {
				return 0;
			}
			int maximum_height = 0;
			for (Map.Entry<Object, Integer> entry : rowheights.entrySet()) {
				int cellHeight = entry.getValue();
				maximum_height = Math.max(maximum_height, cellHeight);
			}
			return maximum_height;
		}
	}

}