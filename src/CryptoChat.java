/**
 * The main class that runs and handles the CryptoChat class.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */
import java.awt.Font;
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

import java.io.IOException;
import java.util.LinkedList;

public class CryptoChat extends JFrame {
	// General fields.
	private static final long serialVersionUID = 4297662718521661000L;
	Dimension startScreenSize = new Dimension(400, 220);
	LinkedList<String> chatHistory = new LinkedList<String>();
	static JPanel panel = new JPanel();
	String title = "CryptoChat";

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
	JLabel joinKeyLabelOpt;
	JTextField joinKeyFieldOpt;
	JCheckBox joinPhraseBoxOpt;
	JTextField joinPhraseFieldOpt;
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
	JLabel joinChatLabel; // to set
	JButton joinAlignChat;// to set
	boolean joinChatLTRAlignment = true;
	JScrollPane joinChatTextboxFrame;// to set
	JTextArea joinChatTextbox;// to set
	JTextField joinInputBox;// to set

	public CryptoChat() {
		// Setup main screen style, location and behavior.
		try {
			GuiUtils.setWinSevenStyle();
		} catch (GuiUtils.GuiException e) {
			GuiUtils.PopUpMessages.errorMsg("can't display in Win7 style!");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(startScreenSize.width, startScreenSize.height);
		setLocation((GuiUtils.getScreenWidth() - getWidth()) / 2,
				(GuiUtils.getsScreenHeight() - getHeight()) / 2);
		setResizable(false);
		setTitle(title);

		// Configuring the different components.
		setupStartScreenHostParts();
		setupStartScreenJoinParts();
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

	private void setupStartScreenHostParts() {
		// Setting the main HOST button.
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
					setTitle(title + " - HOST a chat room");
				} else {
					// hide HOSTing options.
					setHostOptCompsTo(false);
					setTitle(title);
				}
			}
		});

		// Setting the HOST port label.
		hostPortLabelOpt = new JLabel("Server port:");
		hostPortLabelOpt.setVisible(false);
		hostPortLabelOpt.setLocation(202, 10);
		hostPortLabelOpt.setSize(60, 25);

		// Setting the HOST port field.
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

		// Setting the HOST key label.
		hostKeyLabelOpt = new JLabel("8-letter key:");
		hostKeyLabelOpt.setVisible(false);
		hostKeyLabelOpt.setLocation(202, 40);
		hostKeyLabelOpt.setSize(60, 25);

		// Setting the HOST key field.
		hostKeyFieldOpt = new JTextField("superman");
		hostKeyFieldOpt.setLocation(270, 40);
		hostKeyFieldOpt.setSize(115, 25);
		hostKeyFieldOpt.setVisible(false);
		hostKeyFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostKeyFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				String input = hostKeyFieldOpt.getText();
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

		// Setting the HOST Phrase label.
		hostPhraseBoxOpt = new JCheckBox("change default phrase:");
		hostPhraseBoxOpt.setToolTipText("change the session phrase for extra security");
		hostPhraseBoxOpt.setLocation(198, 100);
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

		// Setting the HOST Phrase field.
		hostPhraseFieldOpt = new JTextField(new String(Feistel.sessionPhrase));
		hostPhraseFieldOpt.setLocation(202, 120);
		hostPhraseFieldOpt.setSize(182, 25);
		hostPhraseFieldOpt.setEnabled(false);
		hostPhraseFieldOpt.setVisible(false);
		hostPhraseFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPhraseFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				Feistel.sessionPhrase = hostPhraseFieldOpt.getText().getBytes();
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

		// Setting the HOST start button.
		hostStartButtonOpt = new JButton("<html><center><h1>Start!</h1></center></html>");
		hostStartButtonOpt.setVisible(false);
		hostStartButtonOpt.setLocation(200, 150);
		hostStartButtonOpt.setSize(185, 33);
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
					hostPortFieldOpt.requestFocus();
				}
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
	}

	private void setupStartScreenJoinParts() {
		// Setting the main JOIN button.
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
					setTitle(title + " - JOIN a chat room");
				} else {
					// hide HOSTing options.
					setJoinOptCompsTo(false);
					setTitle(title);
				}
			}
		});

		// Setting the JOIN ip label.
		joinIPLabelOpt = new JLabel("Server ip:");
		joinIPLabelOpt.setVisible(false);
		joinIPLabelOpt.setLocation(10, 10);
		joinIPLabelOpt.setSize(70, 25);

		// Setting the JOIN ip field.
		joinIPFieldOpt = new JTextField("ip goes here");
		joinIPFieldOpt.setLocation(80, 10);
		joinIPFieldOpt.setSize(115, 25);
		joinIPFieldOpt.setVisible(false);
		joinIPFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinIPFieldOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO make and modify the client ip field here.
			}
		});

		// Setting the JOIN port label.
		joinPortLabelOpt = new JLabel("Server port:");
		joinPortLabelOpt.setVisible(false);
		joinPortLabelOpt.setLocation(10, 40);
		joinPortLabelOpt.setSize(70, 25);

		// Setting the JOIN port field.
		joinPortFieldOpt = new JTextField("port goes here");
		joinPortFieldOpt.setLocation(80, 40);
		joinPortFieldOpt.setSize(115, 25);
		joinPortFieldOpt.setVisible(false);
		joinPortFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinPortFieldOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO make and modify the client port field here.
			}
		});

		// Setting the JOIN key label.
		joinKeyLabelOpt = new JLabel("8-letter key:");
		joinKeyLabelOpt.setVisible(false);
		joinKeyLabelOpt.setLocation(10, 70);
		joinKeyLabelOpt.setSize(70, 25);

		// Setting the JOIN key field.
		joinKeyFieldOpt = new JTextField("superman");
		joinKeyFieldOpt.setLocation(80, 70);
		joinKeyFieldOpt.setSize(115, 25);
		joinKeyFieldOpt.setVisible(false);
		joinKeyFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinKeyFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				String input = joinKeyFieldOpt.getText();
				if (input.length() > 8) {
					input = input.substring(0, 8);
					joinKeyFieldOpt.setText(input);
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

		// Setting the HOST Phrase label.
		joinPhraseBoxOpt = new JCheckBox("change default phrase:");
		joinPhraseBoxOpt.setToolTipText("change the session phrase for extra security");
		joinPhraseBoxOpt.setLocation(6, 100);
		joinPhraseBoxOpt.setSize(180, 17);
		joinPhraseBoxOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinPhraseBoxOpt.setVisible(false);
		joinPhraseBoxOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (joinPhraseBoxOpt.isSelected()) {
					joinPhraseFieldOpt.setEnabled(true);
				} else {
					joinPhraseFieldOpt.setEnabled(false);
				}
			}
		});

		// Setting the HOST Phrase field.
		joinPhraseFieldOpt = new JTextField(new String(Feistel.sessionPhrase));
		joinPhraseFieldOpt.setLocation(10, 120);
		joinPhraseFieldOpt.setSize(182, 25);
		joinPhraseFieldOpt.setEnabled(false);
		joinPhraseFieldOpt.setVisible(false);
		joinPhraseFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinPhraseFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				Feistel.sessionPhrase = joinPhraseFieldOpt.getText().getBytes();
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

		// Setting the JOIN start button.
		joinStartButtonOpt = new JButton("<html><center><h1>Join!</h1></center></html>");
		joinStartButtonOpt.setLocation(10, 150);
		joinStartButtonOpt.setSize(185, 33);
		joinStartButtonOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinStartButtonOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setJoinOptCompsTo(false);
				setStartCompsTo(false);
				repaint();
				setWinSizeTo(joinScreenSize);
				setJoinCompsTo(true);
				// TODO make and join client here.
			}
		});

		// add the JOIN and its option components to the window.
		add(joinButton);
		add(joinIPLabelOpt);
		add(joinIPFieldOpt);
		add(joinPortLabelOpt);
		add(joinPortFieldOpt);
		add(joinKeyLabelOpt);
		add(joinKeyFieldOpt);
		add(joinPhraseBoxOpt);
		add(joinPhraseFieldOpt);
		add(joinStartButtonOpt);
	}

	private void setupHostScreen() {
		// Setup the chat title.
		hostChatLabel = new JLabel("Chat:");
		hostChatLabel.setLocation(10, 1);
		hostChatLabel.setSize(100, 25);

		// Setup the chat alignment button.
		hostAlignChat = new JButton("<html><b>LTR</b></html>");
		hostAlignChat.setLocation(380, 6);
		hostAlignChat.setSize(30, 15);
		hostAlignChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
		hostAlignChat.setBorder(new CompoundBorder(null, new EmptyBorder(0, 0, 0, 0)));
		hostAlignChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hostChatLTRAlignment) {
					hostChatLTRAlignment = false;
					hostAlignChat.setText("<html><b>RTL</b></html>");
					hostChatTextbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					hostChatTextboxFrame
							.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				} else {
					hostChatLTRAlignment = true;
					hostAlignChat.setText("<html><b>LTR</b></html>");
					hostChatTextbox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					hostChatTextboxFrame
							.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				}
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
		hostInputBox.setFont(new Font(hostInputBox.getFont().getFontName(), hostInputBox.getFont()
				.getStyle(), 12));
		hostInputBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = hostInputBox.getText();
				if (!s.isEmpty()) {
					chatHistory.add(s + System.lineSeparator());
					hostInputBox.setText("");
					updateChat(s + System.lineSeparator());
				}
			}
		});

		// Add all the HOST-window components to the window.
		add(hostChatTextboxFrame);
		add(hostAlignChat);
		add(hostChatLabel);
		add(hostInputBox);
	}

	private void updateChat(String str) {
		hostChatTextbox.append(str);
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
			joinIPLabelOpt.setVisible(state);
			joinIPFieldOpt.setVisible(state);
			joinPortLabelOpt.setVisible(state);
			joinPortFieldOpt.setVisible(state);
			joinKeyLabelOpt.setVisible(state);
			joinKeyFieldOpt.setVisible(state);
			joinPhraseBoxOpt.setVisible(state);
			joinPhraseFieldOpt.setVisible(state);
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
		CryptoChat gui = new CryptoChat();

		System.out.println(gui.startScreenSize + " | " + gui.chatHistory + " | " + gui.panel
				+ " | " + gui.title + " | " + gui.serverButton + " | " + gui.hostPortLabelOpt
				+ " | " + gui.hostPortFieldOpt + " | " + gui.hostKeyLabelOpt + " | "
				+ gui.hostKeyFieldOpt + " | " + gui.hostPhraseBoxOpt + " | "
				+ gui.hostPhraseFieldOpt + " | " + gui.hostStartButtonOpt + " | " + gui.joinButton
				+ " | " + gui.joinIPLabelOpt + " | " + gui.joinIPFieldOpt + " | "
				+ gui.joinPortLabelOpt + " | " + gui.joinPortFieldOpt + " | " + gui.joinKeyLabelOpt
				+ " | " + gui.joinKeyFieldOpt + " | " + gui.joinPhraseBoxOpt + " | "
				+ gui.joinPhraseFieldOpt + " | " + gui.joinStartButtonOpt + " | "
				+ gui.hostScreenSize + " | " + gui.hostServer + " | " + gui.hostChatLabel + " | "
				+ gui.hostAlignChat + " | " + gui.hostChatLTRAlignment + " | "
				+ gui.hostChatTextboxFrame + " | " + gui.hostChatTextbox + " | " + gui.hostInputBox
				+ " | " + gui.joinScreenSize + " | " + gui.joinChatLTRAlignment + " | ");
	}
}
