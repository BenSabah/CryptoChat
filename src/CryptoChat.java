/**
 * The main class that runs and handles the CryptoChat class.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.ComponentOrientation;
import java.awt.event.ComponentAdapter;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

public class CryptoChat extends JFrame {
	// General fields.
	private static final long serialVersionUID = 4297662718521661000L;
	Point startScreenSize = new Point(400, 220);
	LinkedList<Component> startScreenList;
	LinkedList<Component> hostScreenList;
	LinkedList<Component> joinScreenList;
	LinkedList<String> chatHistory = new LinkedList<String>();
	static JPanel panel = new JPanel();
	static CryptoChat gui;

	// Shared components and settings.
	private byte[] key;
	int port = 9229;

	// START-screen hosting components.
	JToggleButton serverButton;
	JLabel hostPortLabelOpt;
	JTextField hostPortFieldOpt;
	JLabel hostKeyLabelOpt;
	JTextField hostKeyFieldOpt;
	JCheckBox hostPhraseBoxOpt;
	JButton hostButtonStartOpt;

	// START-screen join components.
	JToggleButton joinButton;

	// HOSTING-screen settings and components.
	Point hostScreenSize = new Point(700, 450);
	CryptoServer hostServer;
	JLabel hostChatLabel;
	JScrollPane hostChatTextboxFrame;
	JTextArea hostChatTextbox;
	JButton hostAlignChat;
	boolean hostChatLTRAlignment = true;
	JTextField hostInputBox;

	// JOIN-screen settings and components.
	Point joinScreenSize = new Point(500, 500);
	JButton alignChatJoin;
	private JTextField hostPhraseFieldOpt;

	public CryptoChat() {
		// Setup main screen style, location and behavior.
		try {
			GuiUtils.setWinSevenStyle();
		} catch (GuiUtils.GuiException e) {
			GuiUtils.PopUpMessages.errorMsg("can't display in Win7 style!");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(startScreenSize.x, startScreenSize.y);
		setLocation((GuiUtils.getScreenWidth() - getWidth()) / 2,
				(GuiUtils.getsScreenHeight() - getHeight()) / 2);
		setResizable(false);
		setTitle("CryptoChat");

		// Configuring the different components.
		setupStartScreen();
		setupHostScreen();
		setupJoinScreen();

		// Linking the components to the modes and set mode-visibility.
		linkListAllComponents();
		setComponentsToState(startScreenList, true);
		setComponentsToState(hostScreenList, false);
		setComponentsToState(joinScreenList, false);

		// Starting the window.
		panel = new JPanel();
		add(panel);
		setVisible(true);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				// TODO add resizing stuff here.
			}
		});
	}

	private void linkListAllComponents() {
		// Linking START-screen;
		startScreenList = new LinkedList<Component>();
		startScreenList.add(serverButton);
		startScreenList.add(joinButton);

		// Linking HOST-screen;
		hostScreenList = new LinkedList<Component>();
		hostScreenList.add(hostChatLabel);
		hostScreenList.add(hostAlignChat);
		hostScreenList.add(hostChatTextboxFrame);
		hostScreenList.add(hostChatTextbox);
		hostScreenList.add(hostInputBox);

		// Linking JOIN-screen;
		joinScreenList = new LinkedList<Component>();
		// joinScreenList.add(alignChatJoin);
	}

	private void setupStartScreen() {
		// Setting the HOST button.
		serverButton = new JToggleButton();
		serverButton.setText("<html><center><u>HOST</u><br><br>a secure chat room</center></html>");
		serverButton.setToolTipText("select this option to host a chat");
		serverButton.setLocation(10, 10);
		serverButton.setSize(180, 173);
		serverButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0,
				0, 0, 0)));
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (serverButton.isSelected()) {
					// show HOSTing options.
					joinButton.setVisible(false);
					hostPortLabelOpt.setVisible(true);
					hostPortFieldOpt.setVisible(true);
					hostKeyLabelOpt.setVisible(true);
					hostKeyFieldOpt.setVisible(true);
					hostPhraseBoxOpt.setVisible(true);
					hostPhraseFieldOpt.setVisible(true);
					hostButtonStartOpt.setVisible(true);
				} else {
					// hide HOSTing options.
					joinButton.setVisible(true);
					hostPortLabelOpt.setVisible(false);
					hostPortFieldOpt.setVisible(false);
					hostKeyLabelOpt.setVisible(false);
					hostKeyFieldOpt.setVisible(false);
					hostPhraseBoxOpt.setVisible(false);
					hostPhraseFieldOpt.setVisible(true);
					hostButtonStartOpt.setVisible(false);
				}
			}
		});

		// Setting the port label.
		hostPortLabelOpt = new JLabel("Server port:");
		hostPortLabelOpt.setVisible(false);
		hostPortLabelOpt.setLocation(202, 10);
		hostPortLabelOpt.setSize(60, 25);

		// Setting the port field.
		hostPortFieldOpt = new JTextField(port + "");
		hostPortFieldOpt.setLocation(270, 10);
		hostPortFieldOpt.setSize(115, 25);
		hostPortFieldOpt.setVisible(false);
		hostPortFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPortFieldOpt.addKeyListener(new KeyListener() {
			String input;

			private void update() {
				input = hostPortFieldOpt.getText();
				try {
					port = Integer.parseInt(input);
					if (port > 65536) {
						hostPortFieldOpt.setText("65536");
						port = 65536;
					}
				} catch (Exception e) {
					if (input.length() > 1) {
						hostPortFieldOpt.setText(input.substring(0, input.length() - 1));
					} else {
						hostPortFieldOpt.setText("");
					}
				}
			}

			public void keyTyped(KeyEvent e) {
				update();
			}

			public void keyReleased(KeyEvent e) {
				update();
			}

			public void keyPressed(KeyEvent e) {
				update();
			}
		});

		hostKeyLabelOpt = new JLabel("8-letter key:");
		hostKeyLabelOpt.setVisible(false);
		hostKeyLabelOpt.setLocation(202, 40);
		hostKeyLabelOpt.setSize(60, 45);

		// Setting the port field.
		hostKeyFieldOpt = new JTextField("superman");
		hostKeyFieldOpt.setLocation(270, 50);
		hostKeyFieldOpt.setSize(115, 25);
		hostKeyFieldOpt.setVisible(false);
		hostKeyFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostKeyFieldOpt.addKeyListener(new KeyListener() {
			String input;

			private void update() {
				input = hostKeyFieldOpt.getText();
				if (input.length() > 8) {
					input = input.substring(0, 8);
					hostKeyFieldOpt.setText(input);
				}
				key = input.getBytes();
			}

			public void keyTyped(KeyEvent e) {
				update();
			}

			public void keyReleased(KeyEvent e) {
				update();
			}

			public void keyPressed(KeyEvent e) {
				update();
			}
		});

		hostPhraseBoxOpt = new JCheckBox("change default phrase:");
		hostPhraseBoxOpt.setToolTipText("change the session phrase for extra security");
		hostPhraseBoxOpt.setLocation(198, 90);
		hostPhraseBoxOpt.setSize(180, 17);
		hostPhraseBoxOpt.setVisible(false);
		hostPhraseBoxOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hostPhraseBoxOpt.isSelected()) {
					hostPhraseFieldOpt.setEnabled(true);
				} else {
					hostPhraseFieldOpt.setEnabled(false);
				}
			}
		});

		// Setting the port field.
		hostPhraseFieldOpt = new JTextField(new String(CryptoServer.sessionPhrase));
		hostPhraseFieldOpt.setLocation(202, 110);
		hostPhraseFieldOpt.setSize(182, 25);
		hostPhraseFieldOpt.setEnabled(false);
		hostPhraseFieldOpt.setVisible(false);
		hostPhraseFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPhraseFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				CryptoServer.sessionPhrase = hostPhraseFieldOpt.getText().getBytes();
			}

			public void keyTyped(KeyEvent e) {
				update();
			}

			public void keyReleased(KeyEvent e) {
				update();
			}

			public void keyPressed(KeyEvent e) {
				update();
			}
		});

		// Setting the start button.
		hostButtonStartOpt = new JButton("<html><center><h1>Start!</h1></center></html>");
		hostButtonStartOpt.setVisible(false);
		hostButtonStartOpt.setLocation(200, 140);
		hostButtonStartOpt.setSize(185, 43);
		hostButtonStartOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				serverButton.setSelected(false);
				setComponentsToState(startScreenList, false);
				setWinSizeTo(hostScreenSize.x, hostScreenSize.y);
				setComponentsToState(hostScreenList, true);
				// TODO add threaded server here.
				// server = new CryptoServer(port, key);
			}
		});

		// Setting the JOIN button.
		joinButton = new JToggleButton();
		joinButton.setText("<html><center><u>JOIN</u><br><br>a secure chat room</center></html>");
		joinButton.setToolTipText("select this option to host a chat");
		joinButton.setLocation(203, 10);
		joinButton.setSize((startScreenSize.x - 40) / 2, startScreenSize.y - 47);
		joinButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0,
				0, 0)));
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setComponentsToState(startScreenList, false);
				setWinSizeTo(joinScreenSize.x, joinScreenSize.y);
				setComponentsToState(joinScreenList, true);
				// TODO join server commands here.
			}
		});

		// add HOST button and its options.
		add(serverButton);
		add(hostPortLabelOpt);
		add(hostPortFieldOpt);
		add(hostKeyLabelOpt);
		add(hostKeyFieldOpt);
		add(hostPhraseBoxOpt);
		add(hostPhraseFieldOpt);
		add(hostButtonStartOpt);

		// add JOIN button and its options.
		add(joinButton);
	}

	private void setupHostScreen() {
		// Setup the chat title.
		hostChatLabel = new JLabel("Chat:");
		hostChatLabel.setLocation(10, 1);
		hostChatLabel.setSize(100, 25);

		// Setup the chat alignment button.
		hostAlignChat = new JButton("LTR");
		hostAlignChat.setLocation(380, 6);
		hostAlignChat.setSize(30, 15);
		hostAlignChat.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0,
				0, 0, 0)));
		hostAlignChat.addKeyListener(new KeyListener() {
			private void update() {
				if (hostChatLTRAlignment) {
					hostChatLTRAlignment = false;
					hostAlignChat.setText("RTL");
					hostChatTextbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					hostChatTextboxFrame
							.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				} else {
					hostChatLTRAlignment = true;
					hostAlignChat.setText("LTR");
					hostChatTextbox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					hostChatTextboxFrame
							.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				}
			}

			public void keyTyped(KeyEvent e) {
				update();
			}

			public void keyReleased(KeyEvent e) {
				update();
			}

			public void keyPressed(KeyEvent e) {
				update();
			}

		});

		// Setup the chat history window.
		hostChatTextbox = new JTextArea();
		hostChatTextbox.setLineWrap(true);
		hostChatTextbox.setEditable(false);
		hostChatTextboxFrame = new JScrollPane(hostChatTextbox);
		hostChatTextboxFrame.setLocation(10, 25);
		hostChatTextboxFrame.setSize(400, 355);
		hostChatTextbox.setFont(new Font(hostChatTextbox.getFont().getFontName(), hostChatTextbox
				.getFont().getStyle(), 12));

		// Setup the user input field.
		hostInputBox = new JTextField();
		hostInputBox.setLocation(10, 390);
		hostInputBox.setSize(400, 25);

		add(hostChatTextboxFrame);
		add(hostAlignChat);
		add(hostChatLabel);
		add(hostInputBox);
	}

	private void setupJoinScreen() {

	}

	private void setComponentsToState(LinkedList<Component> list, boolean show) {
		for (Component curComp : list) {
			// if (curComp == null) {
			// System.out.println("need to initilize a component");
			// return;
			// }
			curComp.setVisible(show);
		}
	}

	private void setWinSizeTo(int x, int y) {
		if (GuiUtils.getScreenWidth() <= x || GuiUtils.getsScreenHeight() <= y) {
			return;
		}

		while (getWidth() != x && getHeight() != y) {
			if (x > getWidth()) {
				setSize(getWidth() + 1, getHeight());
			} else {
				setSize(getWidth() - 1, getHeight());
			}
			if (y > getHeight()) {
				setSize(getWidth(), getHeight() + 1);
			} else {
				setSize(getWidth(), getHeight() - 1);
			}

			this.setLocation((GuiUtils.getScreenWidth() - this.getWidth()) / 2,
					(GuiUtils.getsScreenHeight() - this.getHeight()) / 2);
		}

	}

	//
	//
	//
	//
	//
	//
	//
	//
	public static void main(String[] args) {
		gui = new CryptoChat();
		gui.chatHistory.add("dsafsdf");
		gui.chatHistory.add("dsafsdf");
		gui.chatHistory.add("dsafsdf");
		gui.chatHistory.add("dsafsdf");
		gui.chatHistory.add("dsafsdf");
	}
}
