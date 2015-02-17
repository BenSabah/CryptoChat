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
	public void sendMessageToRoom(String message) {
		CryptoServer.messageAllMembers(ip + ": " + message + System.lineSeparator());
	}

	public void sendMsg(String msg) {
		try {
			getMemberOutput().write(msg.getBytes());
			getMemberOutput().flush();
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		String sentence;
		try {
			sentence = memberInput.readLine();
			while (true) {
				// Send sentence to server
				if (sentence != null) {
					sendMessageToRoom(sentence);
				}
				sentence = memberInput.readLine();
			}
		} catch (Exception e) {
		} finally {
			try {
				memberSocket.close();
				CryptoServer.removeChatMember(this);
			} catch (IOException e) {
			}
		}
	}
}
