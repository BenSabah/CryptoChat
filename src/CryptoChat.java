import java.io.IOException;

import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.ComponentOrientation;
import java.awt.event.ComponentAdapter;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

/**
 * The class that runs and handles the CryptoChat window.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

public class CryptoChat extends JFrame {
	// General fields.
	private static final long serialVersionUID = -4542806624866269957L;
	Dimension startScreenSize = new Dimension(400, 220);
	static JPanel panel = new JPanel();
	String title = "CryptoChat";
	String[] expFileName = { "History_Report", "xlsx" };
	static boolean isTesting = false;
	final String TESTING_IP = "localhost";
	final int TESTING_PORT = 9229;
	final String TESTING_KEY = "superman";
	final String TESTING_PHRASE = "simple";
	final String TESTING_USERNAME = "ME";

	// START-screen hosting components.
	JToggleButton serverButton;
	JLabel hostPortLabelOpt;
	int hostPort;
	JTextField hostPortFieldOpt;
	JLabel hostKeyLabelOpt;
	String hostKey;
	JTextField hostKeyFieldOpt;
	JLabel hostUsernameLabelOpt;
	String hostUserName;
	JTextField hostUsernameFieldOpt;
	JCheckBox hostPhraseBoxOpt;
	String hostPhrase = "This is the initial CryptoChat phrase";
	JTextField hostPhraseFieldOpt;
	JButton hostStartButtonOpt;

	// START-screen join components.
	JToggleButton joinButton;
	JLabel joinIPLabelOpt;
	String joinIp;
	JTextField joinIPFieldOpt;
	JLabel joinPortLabelOpt;
	int joinPort;
	JTextField joinPortFieldOpt;
	JLabel joinKeyLabelOpt;
	String joinKey;
	JTextField joinKeyFieldOpt;
	JLabel joinUsernameLabelOpt;
	String joinUserName;
	JTextField joinUsernameFieldOpt;
	JCheckBox joinPhraseBoxOpt;
	String joinPhrase = "This is the initial CryptoChat phrase";
	JTextField joinPhraseFieldOpt;
	JButton joinStartButtonOpt;

	// HOSTING-screen settings and components.
	Dimension hostScreenSize = new Dimension(700, 450);
	CryptoServer hostServer;
	JLabel hostChatLabel;
	boolean hostChatLTRAlignment = true;
	JButton hostChatAlignButton;
	static ChatHistory hostChatHistBox;
	JScrollPane hostChatHistBoxFrame;
	JTextField hostChatInputBox;
	static JList<String> hostUsersList;
	JButton hostExportToFileButton;

	// JOIN-screen settings and components.
	Dimension joinScreenSize = new Dimension(426, 450);
	CryptoClient joinServer;
	JLabel joinChatLabel;
	boolean joinChatLTRAlignment = true;
	JButton joinChatAlignButton;
	JScrollPane joinChatHistBoxFrame;
	static ChatHistory joinChatHistBox;
	JTextField joinChatInputBox;

	public CryptoChat(String mode) {
		System.out.println(mode);
		// Set the IP to local-host if test
		if (mode.equals("-test")) {
			isTesting = true;
			joinIp = TESTING_IP;
			joinPort = TESTING_PORT;
			joinKey = TESTING_KEY;
			joinUserName = TESTING_USERNAME;
			joinPhrase = TESTING_PHRASE;

			hostPort = TESTING_PORT;
			hostKey = TESTING_KEY;
			hostPhrase = TESTING_PHRASE;
		}

		// Setup main screen style, location and behavior.
		try {
			GuiUtils.setWinSevenStyle();
		} catch (GuiUtils.GuiException e) {
			GuiUtils.PopUpMessages.errorMsg("can't display in Win7 style!");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(startScreenSize.width, startScreenSize.height);
		setLocation((GuiUtils.getScreenWidth() - getWidth()) / 2, (GuiUtils.getsScreenHeight() - getHeight()) / 2);
		setResizable(false);
		setTitle(title);

		// Configuring the different components.
		setupStartScreenHostParts();
		setupStartScreenJoinParts();
		setupHostScreen();
		setupJoinScreen();

		// Linking the components to the modes and set mode-visibility.
		setStartJoinOptComponents(false);
		setStartHostOptComponents(false);
		setStartComponents(true);
		setHostComponents(false);
		setJoinComponents(false);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				// TODO add resizing stuff here.
			}
		});

		// Starting the window.
		panel = new JPanel();
		add(panel);
		setVisible(true);
	}

	private void setupStartScreenHostParts() {
		// Setting the main HOST button.
		serverButton = new JToggleButton();
		serverButton.setText("<html><center><h1>HOST</h1><br><br>(a secure chat room)</center></html>");
		serverButton.setToolTipText("select this option to host a chat");
		serverButton.setLocation(10, 10);
		serverButton.setSize(185, 173);
		serverButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		serverButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0, 0, 0)));
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (serverButton.isSelected()) {
					// show HOSTing options.
					joinButton.setVisible(false);
					setStartHostOptComponents(true);
					setTitle(title + " - HOST a chat room");
				} else {
					// hide HOSTing options.
					setStartHostOptComponents(false);
					joinButton.setVisible(true);
					setTitle(title);
				}
			}
		});

		// Setting the HOST port label.
		hostPortLabelOpt = new JLabel("Server port:");
		hostPortLabelOpt.setLocation(202, 10);
		hostPortLabelOpt.setSize(60, 25);

		// Setting the HOST port field.
		hostPortFieldOpt = new JTextField(hostPort + "");
		hostPortFieldOpt.setLocation(270, 10);
		hostPortFieldOpt.setSize(115, 25);
		hostPortFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPortFieldOpt.addKeyListener(new KeyListener() {
			String input;
			int i = 0;

			private void update() {
				input = hostPortFieldOpt.getText();
				try {
					// Fool-proofing the port input.
					hostPort = Integer.parseInt(input);
					if (hostPort > 65535) {
						hostPortFieldOpt.setText("65535");
						hostPort = 65535;
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
		hostKeyLabelOpt.setLocation(202, 40);
		hostKeyLabelOpt.setSize(60, 25);

		// Setting the HOST key field.
		hostKeyFieldOpt = new JTextField(hostKey);
		hostKeyFieldOpt.setLocation(270, 40);
		hostKeyFieldOpt.setSize(115, 25);
		hostKeyFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostKeyFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				String input = hostKeyFieldOpt.getText();
				if (input.length() > 8) {
					input = input.substring(0, 8);
					hostKeyFieldOpt.setText(input);
				}
				hostKey = input;
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
		hostPhraseFieldOpt = new JTextField(hostPhrase);
		hostPhraseFieldOpt.setLocation(202, 120);
		hostPhraseFieldOpt.setSize(182, 25);
		hostPhraseFieldOpt.setEnabled(false);
		hostPhraseFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		hostPhraseFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				hostPhrase = hostPhraseFieldOpt.getText();
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
		hostStartButtonOpt.setLocation(200, 150);
		hostStartButtonOpt.setSize(185, 33);
		hostStartButtonOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		hostStartButtonOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (isTesting) {
						System.out.println("running test server.");
					}
					hostServer = new CryptoServer(hostPort, hostKey, hostPhrase);
					hostServer.start();
					setStartHostOptComponents(false);
					setStartComponents(false);
					setWinSizeTo(hostScreenSize);
					setHostComponents(true);
				} catch (IOException e) {
					GuiUtils.PopUpMessages.errorMsg("Port " + hostPort
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
		joinButton.setText("<html><center><h1>JOIN</h1><br><br>(a secure chat room)</center></html>");
		joinButton.setToolTipText("select this option to join a chat");
		joinButton.setLocation(203, 10);
		joinButton.setSize(180, 173);
		joinButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0, 0, 0)));
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (joinButton.isSelected()) {
					// show HOSTing options.
					serverButton.setVisible(false);
					setStartJoinOptComponents(true);
					setTitle(title + " - JOIN a chat room");
				} else {
					// hide HOSTing options.
					setStartJoinOptComponents(false);
					serverButton.setVisible(true);
					setTitle(title);
				}
			}
		});

		// Setting the JOIN ip label.
		joinIPLabelOpt = new JLabel("Server ip:");
		joinIPLabelOpt.setLocation(10, 10);
		joinIPLabelOpt.setSize(70, 25);

		// Setting the JOIN ip field.
		joinIPFieldOpt = new JTextField(joinIp);
		joinIPFieldOpt.setLocation(80, 10);
		joinIPFieldOpt.setSize(115, 25);
		joinIPFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinIPFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				joinIp = joinIPFieldOpt.getText();
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

		// Setting the JOIN port label.
		joinPortLabelOpt = new JLabel("Server port:");
		joinPortLabelOpt.setLocation(10, 40);
		joinPortLabelOpt.setSize(70, 25);

		// Setting the JOIN port field.
		joinPortFieldOpt = new JTextField(joinPort + "");
		joinPortFieldOpt.setLocation(80, 40);
		joinPortFieldOpt.setSize(115, 25);
		joinPortFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinPortFieldOpt.addKeyListener(new KeyListener() {
			String input;
			int i = 0;

			private void update() {
				input = joinPortFieldOpt.getText();
				try {
					// Fool-proofing the port input.
					joinPort = Integer.parseInt(input);
					if (joinPort > 65535) {
						joinPortFieldOpt.setText("65535");
						joinPort = 65535;
					}
				} catch (Exception e) {
					if (input.length() > 1) {
						for (; i < input.length(); i++) {
							if (input.charAt(i) < '0' || input.charAt(i) > '9') {
								break;
							}
						}
						joinPortFieldOpt.setText(input.substring(0, i));
					} else {
						joinPortFieldOpt.setText("");
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

		// Setting the JOIN key label.
		joinKeyLabelOpt = new JLabel("8-letter key:");
		joinKeyLabelOpt.setLocation(10, 70);
		joinKeyLabelOpt.setSize(70, 25);

		// Setting the JOIN key field.
		joinKeyFieldOpt = new JTextField(joinKey);
		joinKeyFieldOpt.setLocation(80, 70);
		joinKeyFieldOpt.setSize(115, 25);
		joinKeyFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinKeyFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				String input = joinKeyFieldOpt.getText();
				if (input.length() > 8) {
					input = input.substring(0, 8);
					joinKeyFieldOpt.setText(input);
				}
				joinKey = input;
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

		// Setting the JOIN Phrase label.
		joinPhraseBoxOpt = new JCheckBox("change default phrase:");
		joinPhraseBoxOpt.setToolTipText("change the session phrase for extra security");
		joinPhraseBoxOpt.setLocation(6, 100);
		joinPhraseBoxOpt.setSize(180, 17);
		joinPhraseBoxOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinPhraseBoxOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (joinPhraseBoxOpt.isSelected()) {
					joinPhraseFieldOpt.setEnabled(true);
				} else {
					joinPhraseFieldOpt.setEnabled(false);
				}
			}
		});

		// Setting the JOIN Phrase field.
		joinPhraseFieldOpt = new JTextField(joinPhrase);
		joinPhraseFieldOpt.setLocation(10, 120);
		joinPhraseFieldOpt.setSize(183, 25);
		joinPhraseFieldOpt.setEnabled(false);
		joinPhraseFieldOpt.setHorizontalAlignment(JLabel.CENTER);
		joinPhraseFieldOpt.addKeyListener(new KeyListener() {
			private void update() {
				joinPhrase = joinPhraseFieldOpt.getText();
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
				try {
					if (isTesting) {
						System.out.println("joinning test server.");
					}
					joinServer = new CryptoClient(joinIp, joinPort, joinKey, joinPhrase, joinUserName);
					joinServer.start();
					setStartJoinOptComponents(false);
					setStartComponents(false);
					setWinSizeTo(joinScreenSize);
					setJoinComponents(true);
				} catch (IOException e) {
					GuiUtils.PopUpMessages.errorMsg(e.getMessage());
				}
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
		hostChatAlignButton = new JButton("<html><b>LTR</b></html>");
		hostChatAlignButton.setLocation(380, 6);
		hostChatAlignButton.setSize(30, 15);
		hostChatAlignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		hostChatAlignButton.setBorder(new CompoundBorder(null, new EmptyBorder(0, 0, 0, 0)));
		hostChatAlignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hostChatLTRAlignment) {
					hostChatAlignButton.setText("<html><b>RTL</b></html>");
					hostChatHistBoxFrame.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					hostChatHistBox.switchSides();
				} else {
					hostChatAlignButton.setText("<html><b>LTR</b></html>");
					hostChatHistBoxFrame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					hostChatHistBox.switchSides();
				}
				hostChatLTRAlignment = !hostChatLTRAlignment;
			}
		});

		// Setup the chat history window.
		hostChatHistBox = new ChatHistory();
		hostChatHistBoxFrame = new JScrollPane(hostChatHistBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		hostChatHistBoxFrame.setSize(400, 365);
		hostChatHistBoxFrame.setLocation(10, 23);

		// Setup the user input field.
		hostChatInputBox = new JTextField();
		hostChatInputBox.setLocation(10, 390);
		hostChatInputBox.setSize(400, 25);
		hostChatInputBox.setFont(new Font(hostChatInputBox.getFont().getFontName(), hostChatInputBox.getFont()
				.getStyle(), 12));
		hostChatInputBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = hostChatInputBox.getText();
				if (!msg.trim().isEmpty()) {
					hostServer.messageAllMembers(hostServer.getAdminName(), msg);
					hostChatHistBox.append(CoreUtils.boldTextHTML(hostServer.getAdminName()), msg);
					hostChatInputBox.setText("");
				}
			}
		});

		// Setup the window that display the connected users.
		hostUsersList = new JList<String>();
		hostUsersList.setLocation(420, 10);
		hostUsersList.setSize(120, 100);
		// TODO FIX THIS !!!

		hostExportToFileButton = new JButton("export history");
		hostExportToFileButton.setLocation(420, 120);
		hostExportToFileButton.setSize(120, 25);
		hostExportToFileButton.setFont(new Font(hostExportToFileButton.getFont().getFontName(), hostExportToFileButton
				.getFont().getStyle(), 12));
		hostExportToFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String ExpRes = hostChatHistBox.exportToFile(GuiUtils.folderSelector(), expFileName[0],
							expFileName[1]);
					GuiUtils.PopUpMessages.rawMsg(ExpRes, "Export result:", GuiUtils.PopUpMessages.ERROR);
				} catch (Exception e2) {
				}
			}
		});

		// Add all the HOST-window components to the window.
		add(hostChatHistBoxFrame);
		add(hostChatAlignButton);
		add(hostChatLabel);
		add(hostChatInputBox);
		add(hostUsersList);
		add(hostExportToFileButton);
	}

	private void setupJoinScreen() {
		// Setup the chat title.
		joinChatLabel = new JLabel("Chat:");
		joinChatLabel.setLocation(10, 1);
		joinChatLabel.setSize(100, 25);

		// Setup the chat alignment button.
		joinChatAlignButton = new JButton("<html><b>LTR</b></html>");
		joinChatAlignButton.setLocation(380, 6);
		joinChatAlignButton.setSize(30, 15);
		joinChatAlignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		joinChatAlignButton.setBorder(new CompoundBorder(null, new EmptyBorder(0, 0, 0, 0)));
		joinChatAlignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (joinChatLTRAlignment) {
					joinChatAlignButton.setText("<html><b>RTL</b></html>");
					joinChatHistBoxFrame.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					joinChatHistBox.switchSides();
				} else {
					joinChatAlignButton.setText("<html><b>LTR</b></html>");
					joinChatHistBoxFrame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					joinChatHistBox.switchSides();
				}
				joinChatLTRAlignment = !joinChatLTRAlignment;
			}
		});

		// Setup the chat history window.
		joinChatHistBox = new ChatHistory();
		joinChatHistBoxFrame = new JScrollPane(joinChatHistBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		joinChatHistBoxFrame.setSize(400, 365);
		joinChatHistBoxFrame.setLocation(10, 23);

		// Setup the user input field.
		joinChatInputBox = new JTextField();
		joinChatInputBox.setLocation(10, 390);
		joinChatInputBox.setSize(400, 25);
		joinChatInputBox.setFont(new Font(joinChatInputBox.getFont().getFontName(), joinChatInputBox.getFont()
				.getStyle(), 12));
		joinChatInputBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = joinChatInputBox.getText();
				if (!msg.trim().isEmpty()) {
					joinServer.sendEncMsgToServer(joinUserName, msg);
					joinChatHistBox.append(joinUserName, msg);
					joinChatInputBox.setText("");
				}
			}
		});

		// Add all the HOST-window components to the window.
		add(joinChatLabel);
		add(joinChatAlignButton);
		add(joinChatHistBoxFrame);
		add(joinChatInputBox);
	}

	private void setStartComponents(boolean state) {
		try {
			serverButton.setVisible(state);
			joinButton.setVisible(state);
		} catch (Exception e) {
			System.out.println("A - " + e.getClass());
		}
	}

	private void setStartHostOptComponents(boolean state) {
		try {
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

	private void setStartJoinOptComponents(boolean state) {
		try {
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

	private void setHostComponents(boolean state) {
		try {
			hostChatLabel.setVisible(state);
			hostChatAlignButton.setVisible(state);
			hostChatHistBoxFrame.setVisible(state);
			hostChatHistBox.setVisible(state);
			hostChatInputBox.setVisible(state);
			hostUsersList.setVisible(state);
			hostExportToFileButton.setVisible(state);
		} catch (Exception e) {
			System.out.println("D - " + e.getClass());
		}
	}

	private void setJoinComponents(boolean state) {
		try {
			joinChatAlignButton.setVisible(state);
			joinChatLabel.setVisible(state);
			joinChatHistBoxFrame.setVisible(state);
			joinChatInputBox.setVisible(state);
		} catch (Exception e) {
			System.out.println("E - " + e.getClass());
		}

	}

	private void setWinSizeTo(Dimension dim) {
		if (isTesting) {
			setSize(dim);
			setLocation((GuiUtils.getScreenWidth() - getWidth()) / 2, (GuiUtils.getsScreenHeight() - getHeight()) / 2);
			return;
		}

		// Make sure the given dimension isn't bigger than the screen size.
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

			setLocation((GuiUtils.getScreenWidth() - getWidth()) / 2, (GuiUtils.getsScreenHeight() - getHeight()) / 2);
		}
	}
}
