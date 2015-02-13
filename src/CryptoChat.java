/**
 * The main class that runs and handles the CryptoChat class.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

public class CryptoChat extends JFrame {
	private static final long serialVersionUID = 4297662718521661000L;
	static CryptoChat gui;
	static JPanel panel = new JPanel();
	private JButton hostButton;

	public CryptoChat() {
		// Setup the style.
		try {
			GuiUtils.setWinSevenStyle();
		} catch (GuiUtils.GuiException e) {
			GuiUtils.PopUpMessages.errorMsg("can't display in Win7 style!");
		}

		// Setup screen location.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 150);
		this.setLocation((GuiUtils.getScreenWidth() - this.getWidth()) / 2,
				(GuiUtils.getsScreenHeight() - this.getHeight()) / 2);
		this.setResizable(false);

		// Adding the starting screen.
		setupStartScreen();

		// Starting the window.
		panel = new JPanel();
		this.add(panel);
		this.setVisible(true);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				// TODO add resizing stuff here.
			}
		});
	}

	private void setupStartScreen() {
		// Setting the HOST button.
		hostButton = new JButton("<html><u>H</u>OST a secure chat room</html>");
		hostButton.setToolTipText("select this option to host a chat");
		hostButton.setLocation(10, 10);
		hostButton.setSize(170, 100);
		hostButton.setMnemonic('h');
		hostButton.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0,
				0, 0)));
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO add actions here.
			}
		});
		this.add(hostButton);
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
