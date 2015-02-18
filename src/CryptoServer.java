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
import java.util.Arrays;

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
		if (bannedUsersList == null) {
			bannedUsersList = new ArrayList<String>();
		}
		if (probationUsersList == null) {
			probationUsersList = new ArrayList<String>();
		}
	}

	/**
	 * Adds a chat member to the server.
	 * 
	 * @param clientSocket
	 *            The client that we want to add to the server.
	 */
	private void addChatMember(Socket clientSocket) {
		String ip = clientSocket.getLocalAddress().getHostAddress();
		PrintStream clientStream = null;

		try {
			clientStream = new PrintStream(clientSocket.getOutputStream());
			// Check if the user is banned.
			if (checkIfBanned(ip)) {
				clientStream.print("you are banned!");
				clientStream.flush();
				closeConnection(clientSocket, clientStream);
				return;
			}

			// Check if already connected.
			if (isClientAlreadyConnected(clientSocket)) {
				clientStream.print("you are already connected!");
				clientStream.flush();
				closeConnection(clientSocket, clientStream);
				return;
			}

			CryptoClient cryptoClient = new CryptoClient(clientSocket);
			String userSessionPhrase = cryptoClient.readMessage();
			boolean isValidSessionPhrase = checkTheSessionPhrase(userSessionPhrase.getBytes());

			if (!isValidSessionPhrase) {
				// add to Probation, check # of tries and kick if necessary.
				addToProbation(ip);
				if (appearancesInProbation(ip) >= tries) {
					removeFromProbation(ip);
					addToBanned(ip);
					closeConnection(clientSocket, clientStream);
					return;
				}

				// TODO report to HOST that kicked tried to connect.
				clientStream.print("you've been kicked! you have "
						+ (tries - appearancesInProbation(ip)) + " more tries.");

				// Close the connection.
				closeConnection(clientSocket, clientStream);
			}
			// If successful remove from probation.
			removeFromProbation(ip);

			// Start the connection.
			cryptoClient.start();

			// TODO Greet this current added client. IN CRYPTO !!!.
			// "Welcome to the CryptoServer! There are " + usersList.size()
			// + " users connected."

			// Inform all chat members that the new client joined IN CRYPTO !!!.
			messageAllMembers(cryptoClient.getIp() + " joined");

			// Add the user to the list
			synchronized (usersList) {
				usersList.add(cryptoClient);
				if (CryptoChat.hostUsersList != null) {
					CryptoChat.hostUsersList.setListData(getUsersIpArray());
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				closeConnection(clientSocket, clientStream);
			} catch (Exception e) {
			}
		}
	}

	private boolean isClientAlreadyConnected(Socket clientSocket) {
		for (CryptoClient curClient : usersList) {
			if (curClient.getIp().equals(clientSocket.getInetAddress().getHostName())) {
				return true;
			}
		}
		return false;
	}

	private String[] getUsersIpArray() {
		String[] result = new String[usersList.size()];
		for (int i = 0; i < usersList.size(); i++) {
			result[i] = usersList.get(i).getIp();
		}
		return result;
	}

	private void closeConnection(Socket socketMember, PrintStream stream) {
		try {
			stream.close();
			socketMember.close();
		} catch (Exception e) {
		}

	}

	boolean addToProbation(String ip) {
		synchronized (probationUsersList) {
			return probationUsersList.add(ip);
		}

	}

	boolean addToBanned(String ip) {
		synchronized (bannedUsersList) {
			return bannedUsersList.add(ip);
		}
	}

	void removeFromProbation(String ip) {
		synchronized (probationUsersList) {
			for (int i = 0; i < probationUsersList.size(); i++) {
				if (probationUsersList.get(i).equals(ip)) {
					probationUsersList.remove(i);
					i = -1; // Restart the check from the start.
				}
			}
		}
	}

	void removeFromBannedList(String ip) {
		synchronized (bannedUsersList) {
			for (int i = 0; i < bannedUsersList.size(); i++) {
				if (bannedUsersList.get(i).equals(ip)) {
					bannedUsersList.remove(i);
					i = -1; // Restart the check from the start.
				}
			}
		}
	}

	int appearancesInProbation(String ip) {
		synchronized (probationUsersList) {
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
	}

	int appearancesInBanned(String ip) {
		synchronized (bannedUsersList) {
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
	void removeChatMember(CryptoClient member, int mode) {
		synchronized (usersList) {
			usersList.remove(member);
		}
		String response = " got kicked out of the CryptoServer.";
		// if (mode == 0) just kick.
		if (mode == 1) {
			// Add to probation list.
			addToProbation(member.getIp());
			response = " got kicked and is on probation!";
		} else if (mode == 2) {
			// Add to banned list.
			addToBanned(member.getIp());
			response = " banned out of the CryptoServer.";
		}

		messageAllMembers(member.getIp() + response);
	}

	/**
	 * Send a Massage to all connected users.
	 * 
	 * @param msg
	 *            The message that we want to send to all the server's members.
	 * @throws IOException
	 */
	void messageAllMembers(String msg) {
		synchronized (usersList) {
			for (CryptoClient user : usersList) {
				try {
					user.sendMessage(msg + System.lineSeparator());
				} catch (IOException e) {
				}
			}
		}
	}

	boolean closeServer() {
		try {
			serverSocket.close();
			usersList.clear();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void run() {
		Socket newMemberSocket = null;
		try {
			// Handle a client login
			while (true) {
				newMemberSocket = serverSocket.accept();
				addChatMember(newMemberSocket);
			}
		} catch (IOException e) {
			System.out.println("Could not create the new connections");
		} catch (NullPointerException e) {
			System.out.println("the server never started!");
		} finally {
			try {
				serverSocket.close();
				usersList.remove(newMemberSocket);
			} catch (Exception e) {
			}
		}
	}
}
