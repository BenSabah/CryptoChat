/**
 * This class run and handles the CryptoChat Server.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class CryptoServer extends Thread {

	static byte[] sessionPhrase = "This is the initial CryptoChat phrase".getBytes();
	static ArrayList<CryptoClient> usersList;
	static ArrayList<String> bannedUserList;
	private static ServerSocket serverSocket;
	static int port = 9229;

	/**
	 * This constructor starts the CryptoChat-Server, with a specified
	 * initialization phrase.
	 * 
	 * @param portNumber
	 *            The port that the server will use.
	 * @param key
	 *            The Key to use in the CryptoChat server.
	 * @param initPhrase
	 *            Set this to a different (non-default) initialization phrase
	 *            for added security.
	 */
	public CryptoServer() {
		usersList = new ArrayList<CryptoClient>();
		bannedUserList = new ArrayList<String>();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("that port is in use!");
		}
	}

	/**
	 * Adds a chat member to the server.
	 * 
	 * @param newMemberSocket
	 *            The client that we want to add to the server.
	 */
	static boolean addChatMember(Socket newMemberSocket) {
		// TODO check for number of connections from the given IP here.

		try {
			CryptoClient newClient = new CryptoClient(newMemberSocket);
			synchronized (usersList) {
				newClient.start();

				String userSessionPhrase = newClient.getMemberInput().readLine();
				boolean isValidSessionPhrase = checkTheInitPhrase(userSessionPhrase.getBytes());
				if (!isValidSessionPhrase) {
					// TODO update the number of tries for this IP here.
					System.out.println("illegal key");
					newClient.sendMessage("illegal key");
					newMemberSocket.close();
					return false;
				}

				// Greet this current added client.
				newClient.getMemberOutput().print("Welcome to the Crypto Server! There are ");
				newClient.getMemberOutput().println(usersList.size() + " users connected.");

				// Inform all chat members that this current client joined.
				messageAllMembers(newClient.getIp() + " joined" + System.lineSeparator());
				usersList.add(newClient);
			}
		} catch (IOException e1) {
			try {
				newMemberSocket.close();
			} catch (IOException e2) {
			}
		}
		return true;
	}

	/**
	 * This method checks if the given encrypted phrase the same as our
	 * encrypted phrase.
	 * 
	 * @param phraseToCheck
	 *            The phrase we want to compare to our encrypted phrase.
	 * @return True if the given phrase is the same as ours, False otherwise.
	 */
	static boolean checkTheInitPhrase(byte[] phraseToCheck) {
		// Encrypt our session phrase,
		byte[] encryptInitPhrase = Feistel.encrypt(sessionPhrase, Feistel.key);

		// Check for different sizes first.
		if (encryptInitPhrase.length != sessionPhrase.length) {
			return false;
		}

		// Check each byte. return false if encounter a difference.
		for (int i = 0; i < phraseToCheck.length; i++) {
			if (encryptInitPhrase[i] != phraseToCheck[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes a member from the chat.
	 * 
	 * @param member
	 *            The member that we want to remove from the server.
	 */
	static void removeChatMember(CryptoClient member) {
		synchronized (usersList) {
			usersList.remove(member);
		}
		messageAllMembers(member.getIp() + " left the CryptoServer." + System.lineSeparator());
	}

	/**
	 * Send a Massage to all connected users.
	 * 
	 * @param message
	 *            The message that we want to send to all the server's members.
	 */
	static void messageAllMembers(String message) {
		synchronized (usersList) {
			for (CryptoClient user : usersList) {
				user.getMemberOutput().print(message);
			}
		}
	}

	public void run() {
		try {
			// Handle a client login
			while (true) {
				Socket newMemberSocket = serverSocket.accept();
				addChatMember(newMemberSocket);
			}
		} catch (IOException e) {
			System.out.println("Could not create the new connections");
		} catch (NullPointerException e) {
			System.out.println("the server never started!");
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

}
