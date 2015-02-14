/**
 * The main class that runs and handles the CryptoChat class.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

import java.util.LinkedList;
import java.awt.Font;
import java.awt.List;
import java.awt.Point;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

public class CryptoChat extends JFrame {
	// General fields.
	private static final long serialVersionUID = 4297662718521661000L;
	LinkedList<Component> startScreenList;
	LinkedList<Component> hostScreenList;
	LinkedList<Component> joinScreenList;
	static JPanel panel = new JPanel();
	static CryptoChat gui;
	LinkedList<String> chatHistory = new LinkedList<String>();

	// START-screen settings and components.
	Point startScreenSize = new Point(400, 150);
	JButton hostButton;
	JButton joinButton;

	// HOSTING-screen settings and components.
	Point hostScreenSize = new Point(700, 500);
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
		hostButton = new JButton();
		hostButton.setText("<html><center><u>HOST</u><br><br>a secure chat room</center></html>");
		hostButton.setToolTipText("select this option to host a chat");
		hostButton.setLocation(10, 10);
		hostButton.setSize(180, 100);
		hostButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0,
				0, 0)));
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setComponentsToState(startScreenList, false);
				repaint();
				setWinSizeTo(hostScreenSize.x, hostScreenSize.y);
				setComponentsToState(hostScreenList, true);
				// TODO start server commands here.
			}
		});

		// Setting the JOIN button.
		joinButton = new JButton();
		joinButton.setText("<html><center><u>JOIN</u><br><br>a secure chat room</center></html>");
		joinButton.setToolTipText("select this option to host a chat");
		joinButton.setLocation(203, 10);
		joinButton.setSize(180, 100);
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

		add(hostButton);
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
		alignChatHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
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
				chatWindowHostFrame.repaint();
				chatWindowHost.repaint();
			}
		});

		// Setup the chat history window.
		chatWindowHost = new JTextArea();
		chatWindowHost.setLineWrap(true);
		chatWindowHost.setEditable(false);
		chatWindowHostFrame = new JScrollPane(chatWindowHost);
		chatWindowHostFrame.setLocation(10, 25);
		chatWindowHostFrame.setSize(400, hostScreenSize.y - 145);
		chatWindowHost.setFont(new Font(chatWindowHost.getFont().getFontName(), chatWindowHost
				.getFont().getStyle(), 12));

		// Setup the user input field.
		userInput = new JTextField();
		userInput.setLocation(10, hostScreenSize.y - 110);
		userInput.setSize(400, 25);
		// userInput.setFont(new Font(userInput.getFont().getFontName(),
		// userInput.getFont()
		// .getStyle(), 12));

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
			repaint(1);
			;
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
	}
}
