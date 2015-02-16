/**
 * The main class that runs and handles the CryptoChat class.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */
import java.io.IOException;
import java.util.LinkedList;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JCheckBox;
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
	// LinkedList<Component> startScreenList;
	// LinkedList<Component> hostScreenList;
	LinkedList<Component> joinScreenList;
	LinkedList<String> chatHistory = new LinkedList<String>();
	static JPanel panel = new JPanel();
	static CryptoChat gui;

	// Shared components and settings.

	// START-screen hosting components.
	JToggleButton serverButton;
	JLabel hostPortLabelOpt;
	JTextField hostPortFieldOpt;
	JLabel hostKeyLabelOpt;
	JTextField hostKeyFieldOpt;
	JCheckBox hostPhraseBoxOpt;
	JTextField hostPhraseFieldOpt;
	JButton hostStartButtonOpt;

	// START-screen join components.
	JToggleButton joinButton;
	JLabel joinIPLabelOpt;
	JTextField joinIPFieldOpt;
	JLabel joinPortLabelOpt;
	JTextField joinPortFieldOpt;
	JButton joinStartButtonOpt;

	// HOSTING-screen settings and components.
	Dimension hostScreenSize = new Dimension(700, 450);
	CryptoServer hostServer;
	JLabel hostChatLabel;
	JButton hostAlignChat;
	boolean hostChatLTRAlignment = true;
	JScrollPane hostChatTextboxFrame;
	JTextArea hostChatTextbox;
	JTextField hostInputBox;

	// JOIN-screen settings and components.
	Dimension joinScreenSize = new Dimension(500, 500);
	JLabel joinChatLabel;
	JButton joinAlignChat;
	boolean joinChatLTRAlignment = true;
	JScrollPane joinChatTextboxFrame;
	JTextArea joinChatTextbox;
	JTextField joinInputBox;

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
		setStartCompsTo(true);
		setHostCompsTo(false);
		setJoinCompsTo(false);

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

	private void setupStartScreen() {
		// Setting the HOST button.
		serverButton = new JToggleButton();
		serverButton
				.setText("<html><center><h1>HOST</h1><br><br>(a secure chat room)</center></html>");
		serverButton.setToolTipText("select this option to host a chat");
		serverButton.setLocation(10, 10);
		serverButton.setSize(185, 173);
		serverButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		serverButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0,
				0, 0, 0)));
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (serverButton.isSelected()) {
					// show HOSTing options.
					setHostOptCompsTo(true);
				} else {
					// hide HOSTing options.
					setHostOptCompsTo(false);
				}
			}
		});

		// Setting the port label.
		hostPortLabelOpt = new JLabel("Server port:");
		hostPortLabelOpt.setVisible(false);
		hostPortLabelOpt.setLocation(202, 10);
		hostPortLabelOpt.setSize(60, 25);

		// Setting the port field.
		hostPortFieldOpt = new JTextField(CryptoServer.port + "");
		hostPortFieldOpt.setLocation(270, 10);
		hostPortFieldOpt.setSize(115, 25);
		hostPortFieldOpt.setVisible(false);
		hostPortFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPortFieldOpt.addKeyListener(new KeyListener() {
			String input;
			int i = 0;

			private void update() {
				input = hostPortFieldOpt.getText();
				try {
					// Fool-proofing the port input.
					CryptoServer.port = Integer.parseInt(input);
					if (CryptoServer.port > 65535) {
						hostPortFieldOpt.setText("65535");
						CryptoServer.port = 65535;
					}
				} catch (Exception e) {
					if (input.length() > 1) {
						for (; i < input.length(); i++) {
							if (input.charAt(i) < '0' || input.charAt(i) > '9') {
								break;
							}
						}
						hostPortFieldOpt.setText(input.substring(0, i));
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
				Feistel.key = input.getBytes();
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
		hostPhraseBoxOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
		hostStartButtonOpt = new JButton("<html><center><h1>Start!</h1></center></html>");
		hostStartButtonOpt.setVisible(false);
		hostStartButtonOpt.setLocation(200, 140);
		hostStartButtonOpt.setSize(185, 43);
		hostStartButtonOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		hostStartButtonOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					hostServer = new CryptoServer();
					hostServer.start();
					setHostOptCompsTo(false);
					setStartCompsTo(false);
					repaint();
					setWinSizeTo(hostScreenSize);
					setHostCompsTo(true);
				} catch (IOException e) {
					GuiUtils.PopUpMessages.errorMsg("port " + CryptoServer.port
							+ " is already used!\nplease try using another port number.");
					hostPortFieldOpt.setText("");

				}
			}
		});

		// Setting the JOIN button.
		joinButton = new JToggleButton();
		joinButton
				.setText("<html><center><h1>JOIN</h1><br><br>(a secure chat room)</center></html>");
		joinButton.setToolTipText("select this option to join a chat");
		joinButton.setLocation(203, 10);
		joinButton.setSize(180, 173);
		joinButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0,
				0, 0)));
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (joinButton.isSelected()) {
					// show HOSTing options.
					setJoinOptCompsTo(true);
				} else {
					// hide HOSTing options.
					setJoinOptCompsTo(false);
				}
			}
		});

		// Setting the start button.
		joinStartButtonOpt = new JButton("<html><center><h1>Join!</h1></center></html>");
		// joinStartButtonOpt.setVisible(false);
		joinStartButtonOpt.setLocation(10, 140);
		joinStartButtonOpt.setSize(185, 43);
		joinStartButtonOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinStartButtonOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setJoinOptCompsTo(false);
				setStartCompsTo(false);
				repaint();
				setWinSizeTo(joinScreenSize);
				setJoinCompsTo(true);
				// TODO join server commands here.
			}
		});

		// add the HOST and its option components to the window.
		add(serverButton);
		add(hostPortLabelOpt);
		add(hostPortFieldOpt);
		add(hostKeyLabelOpt);
		add(hostKeyFieldOpt);
		add(hostPhraseBoxOpt);
		add(hostPhraseFieldOpt);
		add(hostStartButtonOpt);

		// add the JOIN and its option components to the window.
		add(joinButton);

		add(joinStartButtonOpt);
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
		hostAlignChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
		hostAlignChat.setBorder(new CompoundBorder(null, new EmptyBorder(0, 0, 0, 0)));
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

		// Add all the HOST-window components to the window.
		add(hostChatTextboxFrame);
		add(hostAlignChat);
		add(hostChatLabel);
		add(hostInputBox);
	}

	private void setupJoinScreen() {

		// Add all the JOIN-window components to the window.
	}

	private void setStartCompsTo(boolean state) {
		try {
			serverButton.setVisible(state);
			joinButton.setVisible(state);
		} catch (Exception e) {
			System.out.println("A - " + e.getClass());
		}
	}

	private void setHostOptCompsTo(boolean state) {
		try {
			joinButton.setVisible(!state);
			hostPortLabelOpt.setVisible(state);
			hostPortFieldOpt.setVisible(state);
			hostKeyLabelOpt.setVisible(state);
			hostKeyFieldOpt.setVisible(state);
			hostPhraseBoxOpt.setVisible(state);
			hostPhraseFieldOpt.setVisible(state);
			hostStartButtonOpt.setVisible(state);
		} catch (Exception e) {
			System.out.println("B - " + e.getClass());
		}
	}

	private void setJoinOptCompsTo(boolean state) {
		try {
			serverButton.setVisible(!state);
			joinStartButtonOpt.setVisible(state);
		} catch (Exception e) {
			System.out.println("C - " + e.getClass());
		}
	}

	private void setHostCompsTo(boolean state) {
		try {
			hostChatLabel.setVisible(state);
			hostAlignChat.setVisible(state);
			hostChatTextboxFrame.setVisible(state);
			hostChatTextbox.setVisible(state);
			hostInputBox.setVisible(state);
		} catch (Exception e) {
			System.out.println("D - " + e.getClass());
		}
	}

	private void setJoinCompsTo(boolean state) {
		try {
			joinStartButtonOpt.setVisible(state);
		} catch (Exception e) {
			System.out.println("E - " + e.getClass());
		}

	}

	private void setWinSizeTo(Dimension dim) {
		if (GuiUtils.getScreenWidth() <= dim.width || GuiUtils.getsScreenHeight() <= dim.height) {
			return;
		}

		while (getWidth() != dim.width || getHeight() != dim.height) {
			if (dim.width > getWidth()) {
				setSize(getWidth() + 1, getHeight());
			} else {
				setSize(getWidth() - 1, getHeight());
			}
			if (dim.height > getHeight()) {
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
		gui.chatHistory.add("TelAviv");
		gui.chatHistory.add("Jerusalem");
		gui.chatHistory.add("Pari");
		gui.chatHistory.add("OldYork");
		gui.chatHistory.add("Dusseldorf");
	}
}
