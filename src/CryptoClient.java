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
	private static final int START_SALT = 7557;
	static final long ACCEPTABLE_TIME = 5000;
	private String ip;
	private Socket socket;
	String phrase;
	byte[] byteKey;
	private PrintStream toServer;
	private BufferedReader fromServer;
	int salt = START_SALT;
	String userName;

	public CryptoClient(String ip, int port, String strKey, String strPhrase, String userName) throws IOException {
		this.ip = ip;
		byteKey = strKey.getBytes();
		socket = new Socket(ip, port);
		phrase = strPhrase;
		toServer = new PrintStream(socket.getOutputStream());
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.userName = userName;
	}

	public void run() {
		// Use the try-catch to know when the server closed the connection.
		try {
			// Read the 1st response from the server (asking for phrase or
			// banned).
			fromServer.readLine();

			System.out.println("failed" + 1);
			// Send the session phrase to the host.
			if (CryptoChat.isTesting) {
				toServer.println(fromServer.readLine());
				toServer.flush();
			} else {
				sendEncMsgToServer(ip, phrase);
			}

			System.out.println("failed" + 2);

			// Send the user-name to the server.
			sendEncMsgToServer(ip, userName);

			System.out.println("failed" + 3);
			// Get the server greeting.
			String[] response = readDecMsgFromServer();
			System.out.println("failed" + 33);

			String greeting = response[1];

			System.out.println("failed" + 333);

			CryptoChat.joinChatHistBox.append(CoreUtils.boldTextHTML(userName), greeting);

			System.out.println("failed" + 4);
			// Get the user salt.
			salt = Integer.parseInt(readDecMsgFromServer()[1]);

			// Keep receiving messages from the server.
			String[] incoming;
			String user;
			String msg;
			while (true) {
				System.out.println("failed" + 5);
				incoming = readDecMsgFromServer();

				System.out.println("failed" + 6);
				// If the message is null (bad time-stamp) we close the
				// connection.
				if (incoming == null) {
					break;
				}
				System.out.println("failed" + 7);

				user = incoming[0];
				msg = incoming[1];
				CryptoChat.joinChatHistBox.append(user, msg);
				System.out.println("failed" + 8);
			}
		} catch (Exception e) {
		} finally {
			closeClient();
		}
	}

	/**
	 * This method waits and reads a message the client set, than breaks it up
	 * into 2 parts, the first part is the time-stamp, and the second part is
	 * the message itself. if the time-stamp is wrong we return null.
	 * 
	 * Thanks to Niki (rudi) for the muse.
	 * 
	 * @return the byte array of the given message, the first cell contains the
	 *         user-name & the 2nd cell contains the message.
	 * @throws IOException
	 *             is thrown when there's an issue while trying to read from the
	 *             server.
	 */
	public String[] readDecMsgFromServer() {
		int curIndex = 0;
		byte[] decMsg = null;
		System.out.println("a");

		try {
			if (CryptoChat.isTesting) {
				System.out.println("a1");
				decMsg = fromServer.readLine().getBytes();
			} else {
				System.out.println("a2");
				decMsg = Feistel.decrypt(fromServer.readLine().getBytes(), Feistel.key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("aa");

		// Check if the timing is acceptable.
		long receivedTime = CoreUtils.bytesToLong(CoreUtils.getBytes(decMsg, curIndex, 8));

		System.out.println("b");
		if (System.currentTimeMillis() - receivedTime > ACCEPTABLE_TIME) {
			System.out.println("c");

			return null;
		}
		curIndex += 8;
		System.out.println("d");
		// Check if the salt is correct.
		int salt = CoreUtils.bytesToInt(CoreUtils.getBytes(decMsg, curIndex, 4));
		if (this.salt != salt) {
			return null;
		}
		curIndex += 4;
		System.out.println("e");
		// Get the size of the user-name.
		int sizeOfUserName = CoreUtils.bytesToInt(CoreUtils.getBytes(decMsg, curIndex, 4));
		curIndex += 4;

		System.out.println("f");
		// Get the user-name.
		String user = new String(CoreUtils.getBytes(decMsg, curIndex, sizeOfUserName));
		curIndex += sizeOfUserName;

		System.out.println("g");
		// If passed all, return the message.
		String msg = new String(CoreUtils.getBytes(decMsg, curIndex, decMsg.length - curIndex));
		String[] result = { user, msg };

		System.out.println("h");
		return result;
	}

	/**
	 * This method add a time-stamp, the size of the user name, the user-name,
	 * user-specific salt to the give message, encrypt it, than it sends the
	 * combined message to the server.
	 * 
	 * Thanks to Niki (rudi) for the muse.
	 * 
	 * @param user
	 *            The user that sent the message.
	 * @param msg
	 *            The message that the user wanted to send.
	 */
	public void sendEncMsgToServer(String user, String msg) {
		// Add a time stamp, the size of the user-name, than the user-name
		// itself, and the salt to the beginning of the message.
		byte[] curTime = CoreUtils.longToBytes(System.currentTimeMillis());
		byte[] sizeOfUser = CoreUtils.intToBytes(userName.length());
		byte[] userName = user.getBytes();
		byte[] saltNumber = CoreUtils.intToBytes(salt);
		byte[] msgToSend = msg.getBytes();

		// Combine the five parts.
		byte[] combinedMsg = CoreUtils.combineArrays(curTime, sizeOfUser, userName, saltNumber, msgToSend);

		// Send the combined encrypted Message to the client.
		if (CryptoChat.isTesting) {
			toServer.println(new String(combinedMsg));
			toServer.flush();
			return;
		}
		toServer.println(new String(Feistel.encrypt(combinedMsg, Feistel.key)));
		toServer.flush();
	}

	public String getUserName() {
		return userName;
	}

	public void setAdminName(String name) {
		userName = name;
	}

	private void closeClient() {
		CryptoChat.joinChatHistBox.append(userName,
				CoreUtils.boldTextHTML("you have been disconnected from the server."));
		try {
			fromServer.close();
		} catch (Exception e) {
		}
		try {
			toServer.close();
		} catch (Exception e) {
		}
		try {
			socket.close();
		} catch (Exception e) {
		}
	}
}
