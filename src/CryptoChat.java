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
	LinkedList<Component> startScreenList;
	LinkedList<Component> hostScreenList;
	LinkedList<Component> joinScreenList;
	LinkedList<String> chatHistory = new LinkedList<String>();
	static JPanel panel = new JPanel();
	static CryptoChat gui;

	// Shared components and settings.
	private byte[] key;
	int port;

	// START-screen hosting components.
	Point startScreenSize = new Point(400, 220);
	JToggleButton hostButton;
	JLabel serverPortLabel;
	JTextField portField;
	JButton hostButtonStart;

	// START-screen join components.
	JToggleButton joinButton;

	// HOSTING-screen settings and components.
	Point hostScreenSize = new Point(700, 500);
	CryptoServer server;
	JLabel membersTitle;
	JScrollPane chatWindowHostFrame;
	JTextArea chatWindowHost;
	JButton alignChatHost;
	boolean ltrAlignment = true;
	JTextField userInput;

	// JOIN-screen settings and components.
	Point joinScreenSize = new Point(500, 500);
	JButton alignChatJoin;

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
		startScreenList.add(hostButton);
		startScreenList.add(joinButton);

		// Linking HOST-screen;
		hostScreenList = new LinkedList<Component>();
		hostScreenList.add(membersTitle);
		hostScreenList.add(alignChatHost);
		hostScreenList.add(chatWindowHostFrame);
		hostScreenList.add(chatWindowHost);
		hostScreenList.add(userInput);

		// Linking JOIN-screen;
		joinScreenList = new LinkedList<Component>();
		joinScreenList.add(alignChatJoin);
	}

	private void setupStartScreen() {
		// Setting the HOST button.
		hostButton = new JToggleButton();
		hostButton.setText("<html><center><u>HOST</u><br><br>a secure chat room</center></html>");
		hostButton.setToolTipText("select this option to host a chat");
		hostButton.setLocation(10, 10);
		hostButton.setSize((startScreenSize.x - 40) / 2, startScreenSize.y - 47);
		hostButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0,
				0, 0)));
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (hostButton.isSelected()) {
					// show HOSTing options.
					joinButton.setVisible(false);
					serverPortLabel.setVisible(true);
					portField.setVisible(true);
					hostButtonStart.setVisible(true);
				} else {
					// hide HOSTing options.
					joinButton.setVisible(true);
					serverPortLabel.setVisible(false);
					portField.setVisible(false);
					hostButtonStart.setVisible(false);
				}
			}
		});

		serverPortLabel = new JLabel("Server port:");
		serverPortLabel.setVisible(false);
		serverPortLabel.setLocation(202, 10);
		serverPortLabel.setSize(60, 25);

		portField = new JTextField(port);
		portField.setLocation(270, 10);
		portField.setSize(115, 25);
		portField.setVisible(false);
		portField.setHorizontalAlignment(JLabel.CENTER);
		portField.addKeyListener(new KeyListener() {
			String input;

			private void update() {
				input = portField.getText();
				try {
					port = Integer.parseInt(input);
					if (port > 65536) {
						portField.setText("65536");
						port = 65536;
					}
				} catch (Exception e) {
					if (input.length() > 1) {
						portField.setText(input.substring(0, input.length() - 1));
					} else {
						portField.setText("");
					}
				}
				System.out.println(port);
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

		hostButtonStart = new JButton("<html><center><h1>Start!</h1></center></html>");
		hostButtonStart.setVisible(false);
		hostButtonStart.setLocation(200, 140);
		hostButtonStart.setSize(185, 43);
		hostButtonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setComponentsToState(startScreenList, false);
				setWinSizeTo(hostScreenSize.x, hostScreenSize.y);
				setComponentsToState(hostScreenList, true);
				server = new CryptoServer(port, key);
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
		add(hostButton);
		add(hostButtonStart);
		add(serverPortLabel);
		add(portField);

		// add JOIN button and its options.
		add(joinButton);
	}

	private void setupHostScreen() {
		// Setup the chat title.
		membersTitle = new JLabel("Chat:");
		membersTitle.setLocation(10, 1);
		membersTitle.setSize(100, 25);

		// Setup the chat alignment button.
		alignChatHost = new JButton("LTR");
		alignChatHost.setLocation(380, 6);
		alignChatHost.setSize(30, 15);
		alignChatHost.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0,
				0, 0, 0)));
		alignChatHost.addKeyListener(new KeyListener() {
			private void update() {
				if (ltrAlignment) {
					ltrAlignment = false;
					alignChatHost.setText("RTL");
					chatWindowHost.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					chatWindowHostFrame.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				} else {
					ltrAlignment = true;
					alignChatHost.setText("LTR");
					chatWindowHost.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					chatWindowHostFrame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
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
		chatWindowHost = new JTextArea();
		chatWindowHost.setLineWrap(true);
		chatWindowHost.setEditable(false);
		chatWindowHostFrame = new JScrollPane(chatWindowHost);
		chatWindowHostFrame.setLocation(10, 25);
		chatWindowHostFrame.setSize(400, 355);
		chatWindowHost.setFont(new Font(chatWindowHost.getFont().getFontName(), chatWindowHost
				.getFont().getStyle(), 12));

		// Setup the user input field.
		userInput = new JTextField();
		userInput.setLocation(10, hostScreenSize.y - 110);
		userInput.setSize(400, 25);

		add(chatWindowHostFrame);
		add(alignChatHost);
		add(membersTitle);
		add(userInput);
	}

	private void setupJoinScreen() {

	}

	private void setComponentsToState(LinkedList<Component> list, boolean show) {
		for (Component curComp : list) {
			if (curComp == null) {
				System.out.println("need to initilize a component");
				return;
			}
			curComp.setVisible(show);
		}
	}

	private void setWinSizeTo(int x, int y) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
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
