/**
 * This class run and handles the CryptoChat Server.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class CryptoServer extends Thread {

	private static ServerSocket serverSocket;
	static int port = 9229;
	static ArrayList<CryptoClient> usersList;
	static ArrayList<String> bannedUsersList;
	static ArrayList<String> probationUsersList;
	private static int tries = 3;

	/**
	 * This constructor starts the CryptoChat-Server, using the specified port &
	 * session-phrase.
	 */
	public CryptoServer() throws IOException {
		serverSocket = new ServerSocket(port);
		usersList = new ArrayList<CryptoClient>();
		bannedUsersList = new ArrayList<String>();
		probationUsersList = new ArrayList<String>();
	}

	/**
	 * Adds a chat member to the server.
	 * 
	 * @param socketMember
	 *            The client that we want to add to the server.
	 */
	private void addChatMember(Socket socketMember) {
		String ip = socketMember.getLocalAddress().getHostAddress();
		PrintStream clientStream = null;

		try {
			clientStream = new PrintStream(socketMember.getOutputStream());
			// Check if the user is banned.
			if (checkIfBanned(ip)) {
				clientStream.print("you are banned!");
				clientStream.flush();
				clientStream.close();
				closeConnection(socketMember, clientStream);
				return;
			}

			CryptoClient cryptoClient = new CryptoClient(socketMember);
			String userSessionPhrase = cryptoClient.getMemberInput().readLine();
			boolean isValidSessionPhrase = checkTheSessionPhrase(userSessionPhrase.getBytes());

			if (!isValidSessionPhrase) {
				// add to Probation, check # of tries and kick if necessary.
				addToProbation(ip);
				if (appearancesInProbation(ip) >= tries) {
					removeFromProbation(ip);
					addToBanned(ip);
					closeConnection(socketMember, clientStream);
					return;
				}

				// TODO report to HOST that kicked tried to connect.
				clientStream.print("you've been kicked! you have "
						+ (tries - appearancesInProbation(ip)) + " more tries.");

				// Kinda lame way to jump to the finally part.
				throw new Exception();
			}
			// If successful remove from probation.
			removeFromProbation(ip);

			// Start the connection.
			cryptoClient.start();

			// TODO Greet this current added client. IN CRYPTO !!!.
			// "Welcome to the Crypto Server! There are " + usersList.size()
			// + " users connected."

			// Inform all chat members that the new client joined IN CRYPTO !!!.
			messageAllMembers(cryptoClient.getIp() + " joined" + System.lineSeparator());
			usersList.add(cryptoClient);
		} catch (Exception e) {
		} finally {
			try {
				closeConnection(socketMember, clientStream);
			} catch (Exception e) {
			}
		}
	}

	private void closeConnection(Socket socketMember, PrintStream stream) {
		try {
			stream.close();
			socketMember.close();
		} catch (Exception e) {
		}

	}

	boolean addToProbation(String ip) {
		return probationUsersList.add(ip);
	}

	boolean addToBanned(String ip) {
		return bannedUsersList.add(ip);
	}

	void removeFromProbation(String ip) {
		for (int i = 0; i < probationUsersList.size(); i++) {
			if (probationUsersList.get(i).equals(ip)) {
				probationUsersList.remove(i);
				i = -1; // Restart the check from the start.
			}
		}
	}

	void removeFromBannedList(String ip) {
		for (int i = 0; i < bannedUsersList.size(); i++) {
			if (bannedUsersList.get(i).equals(ip)) {
				bannedUsersList.remove(i);
				i = -1; // Restart the check from the start.
			}
		}
	}

	int appearancesInProbation(String ip) {
		if (probationUsersList.isEmpty()) {
			return 0;
		}
		int appearances = 0;
		for (String s : probationUsersList) {
			if (s.equals(ip)) {
				appearances++;
			}
		}
		return appearances;
	}

	int appearancesInBanned(String ip) {
		if (bannedUsersList.isEmpty()) {
			return 0;
		}
		int appearances = 0;
		for (String s : bannedUsersList) {
			if (s.equals(ip)) {
				appearances++;
			}
		}
		return appearances;
	}

	boolean checkIfBanned(String ip) {
		return appearancesInBanned(ip) > 0;
	}

	boolean checkIfProbation(String ip) {
		return appearancesInProbation(ip) > 0;
	}

	/**
	 * This method checks if the given encrypted phrase the same as our
	 * encrypted phrase.
	 * 
	 * @param phraseToCheck
	 *            The phrase we want to compare to our encrypted phrase.
	 * @return True if the given phrase is the same as ours, False otherwise.
	 */
	private boolean checkTheSessionPhrase(byte[] phraseToCheck) {
		// Encrypt our session phrase,
		byte[] encryptInitPhrase = Feistel.encrypt(Feistel.sessionPhrase, Feistel.key);

		// Check for different sizes first.
		if (encryptInitPhrase.length != Feistel.sessionPhrase.length) {
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
				user.getMemberOutput().flush();
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
