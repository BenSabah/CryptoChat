import java.net.Socket;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class handles the clients that wants to add to the CryptoChat.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

public class CryptoClient extends Thread {
	private String ip;
	private Socket socket;
	private byte[] byteKey;
	private PrintStream toHost;
	private BufferedReader fromHost;

	public CryptoClient(String ip, int port, String strKey, String strPhrase) throws IOException {
		this.ip = ip;
		byteKey = strKey.getBytes();
		socket = new Socket(ip, port);
		toHost = new PrintStream(socket.getOutputStream(), true);
		fromHost = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// Check if banned already.
		String firstResponse = readMessage();

		System.out.println("111 - " + firstResponse + "\n");

		// Send the session phrase to the host.
		sendMessage(new String(Feistel.encrypt(strPhrase.getBytes(), byteKey)));

		System.out.println("222 - " + firstResponse);

		String secondResponse;
		try {
			// Get the returned message from the server.
			secondResponse = readMessage();
			// if disconnected it jumps here.
			synchronized (CryptoChat.joinChatTextbox) {
				CryptoChat.joinChatTextbox.append(secondResponse);
			}
		} catch (Exception e) {

			throw new IOException(firstResponse);
		}
	}

	public void run() {
		String cryptoMSG;
		String plainMSG;

		// Keep receiving messages from the server IN CRYPTO-TEXT.
		for (;;) {
			try {
				System.out.println("asdfsd");
				cryptoMSG = readMessage();
				plainMSG = new String(Feistel.decrypt(cryptoMSG.getBytes(), Feistel.key));
				CryptoChat.joinChatTextbox.append(plainMSG);
			} catch (IOException e) {
				closeClient();
			}
		}
	}

	private void closeClient() {
		try {
			toHost.close();
		} catch (Exception e) {
		}
		try {
			fromHost.close();
		} catch (Exception e) {
		}
		try {
			socket.close();
		} catch (Exception e) {
		}
	}

	public String getIp() {
		return ip;
	}

	public Socket getSocket() {
		return socket;
	}

	/**
	 * This method blocks until there is something to read from the stream.
	 * 
	 * @return The string that the host sent.
	 * @throws IOException
	 *             is thrown when there's an issue while trying to read from the
	 *             server.
	 */
	public String readMessage() throws IOException {
		return fromHost.readLine();
	}

	public void sendMessage(String msg) throws IOException {
		toHost.println(msg);
	}
}
