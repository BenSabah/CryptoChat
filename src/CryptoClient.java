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

	static int port;
	private String ip;
	private Socket socket;
	private PrintStream streamOutput;
	private BufferedReader streamInput;

	public CryptoClient(Socket socket) throws IOException {
		this.socket = socket;
		ip = socket.getInetAddress().getHostAddress();
		streamOutput = new PrintStream(socket.getOutputStream());
		streamInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public String getIp() {
		return ip;
	}

	public Socket getSocket() {
		return socket;
	}

	public String readMessage() throws IOException {
		return streamInput.readLine();
	}

	public void sendMessage(String msg) throws IOException {
		streamOutput.write(msg.getBytes());
		streamOutput.flush();
	}

	public void run() {
		try {
			
		} catch (Exception e) {
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
