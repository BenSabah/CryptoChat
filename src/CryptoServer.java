/**
 * This class run and handles the CryptoChat Server.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CryptoServer {

	private static byte[] sessionPhrase = "This is the initial CryptoChat phrase".getBytes();
	static ArrayList<CryptoClient> usersList;
	private static ServerSocket serverSocket;
	private static byte[] key;

	/**
	 * This constructor starts the CryptoChat-Server, with the default
	 * initialization phrase.
	 * 
	 * @param portNumber
	 *            The port that the server will use.
	 * @param key
	 *            The Key to use in the CryptoChat server.
	 */

	public CryptoServer(int portNumber, byte[] key) {
		this(portNumber, key, sessionPhrase);
	}

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
	public CryptoServer(int portNumber, byte[] key, byte[] initPhrase) {
		sessionPhrase = initPhrase;
		usersList = new ArrayList<CryptoClient>();
		try {
			serverSocket = new ServerSocket(portNumber);

			// Handle a client login
			while (true) {
				Socket newMemberSocket = serverSocket.accept();
				addChatMember(newMemberSocket);
			}
		} catch (IOException e) {
			System.out.println("Could not create a new server socket.");
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}

	}

	/**
	 * Adds a chat member to the server.
	 * 
	 * @param newMemberSocket
	 *            The client that we want to add to the server.
	 */
	private static boolean addChatMember(Socket newMemberSocket) {
		// TODO check for number of connections from the given IP here.

		try {
			CryptoClient newClient = new CryptoClient(newMemberSocket);
			synchronized (usersList) {
				newClient.start();

				boolean isValidSessionPhrase = checkTheInitPhrase(newClient.getMemberInput()
						.readLine().getBytes());
				if (!isValidSessionPhrase) {
					// TODO update the number of tries for this IP here.
					System.out.println("illegal key");
					newMemberSocket.close();
					return false;
				}

				// Greet this current added client.
				newClient.getMemberOutput().println("Welcome to the Crypto Server! There are ");
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
	private static boolean checkTheInitPhrase(byte[] phraseToCheck) {
		// Encrypt our session phrase,
		byte[] encryptInitPhrase = Feistel.encrypt(sessionPhrase, key);

		// Check for different sizes first.
		if (encryptInitPhrase.length != sessionPhrase.length) {
			return false;
		}

		// Check each byte. return false if encounter a difference.
		for (int i = 0; i < key.length; i++) {
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

}
