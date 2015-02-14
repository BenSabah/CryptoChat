import java.net.Socket;

import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class run handles the CryptoChat added clients.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

public class CryptoClient extends Thread {

	private String ip;
	private Socket memberSocket;
	private PrintStream outToMember;
	private BufferedReader memberInput;

	public CryptoClient(Socket memberSocket) throws IOException {
		ip = memberSocket.getInetAddress().getHostAddress();
		this.memberSocket = memberSocket;
		memberInput = new BufferedReader(new InputStreamReader(memberSocket.getInputStream()));
		outToMember = new PrintStream(memberSocket.getOutputStream());
	}

	public String getIp() {
		return ip;
	}

	public Socket getMemberSocket() {
		return memberSocket;
	}

	public BufferedReader getMemberInput() {
		return memberInput;
	}

	public PrintStream getMemberOutput() {
		return outToMember;
	}

	/**
	 * Sends the massage to the chat member.
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		CryptoServer.messageAllMembers(ip + ": " + message + System.lineSeparator());
	}

	@Override
	public void run() {
		String sentence;
		try {
			sentence = memberInput.readLine();
			while (!sentence.equalsIgnoreCase("exit!")) {
				// Send sentence to server
				if (sentence != null) {
					sendMessage(sentence);
				}
				sentence = memberInput.readLine();
			}
		} catch (Exception e) {
		} finally {
			try {
				memberSocket.close();
				CryptoServer.removeChatMember(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
