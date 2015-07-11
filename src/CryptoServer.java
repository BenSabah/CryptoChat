/**
 * This class run and handles the CryptoChat Server.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class CryptoServer extends Thread {
	ServerSocket serverSocket;
	int port = 9229;
	private byte[] serverPhrase;
	ArrayList<Client> usersList;
	ArrayList<String> bannedUsersList;
	ArrayList<String> probationUsersList;
	int tries = 3;
	boolean run;
	String systemName = "SYSTEM";
	
	/**
	 * This constructor starts the CryptoChat-Server, using the specified port &
	 * session-phrase.
	 * 
	 * @param port
	 *            The port to bind the server to.
	 * @param serverPhrase
	 *            The encrypted session phrase that grant clients access to our
	 *            server.
	 * @throws IOException
	 *             Thrown when we bind the server to the given port.
	 */
	public CryptoServer(int port, String strKey, String strPhrase) throws IOException {
		this.port = port;
		serverPhrase = strPhrase.getBytes();
		serverSocket = new ServerSocket(port);
		usersList = new ArrayList<Client>();
		bannedUsersList = new ArrayList<String>();
		probationUsersList = new ArrayList<String>();
	}

	public void run() {
		Socket socket = null;
		run = true;

		// Handle a client login
		while (run) {
			try {
				socket = serverSocket.accept();
				addChatMember(socket);
			} catch (Exception e) {
				// If clouldn't connect or add the client close and remove.
				closeClientConnection(socket);
				synchronized (usersList) {
					for (Client curClient : usersList) {
						if (curClient.getSocket() == socket) {
							removeChatMember(curClient, 0);
						}
					}
				}
			}
		}
		closeServer();
	}

	/**
	 * Adds a chat member to the server.
	 * 
	 * @param socket
	 *            The client that we want to add to the server.
	 * @throws IOException
	 */
	private void addChatMember(Socket socket) throws IOException {
		String ip = socket.getLocalAddress().getHostAddress();
		PrintStream clientStream = new PrintStream(socket.getOutputStream(), true);

		// Check if the user is banned.
		if (isClientBanned(ip)) {
			clientStream.println("you are banned!");
			closeClientConnection(socket);
			return;
		}

		// Check if already connected.
		if (isClientAlreadyConnected(ip)) {
			clientStream.println("you are already connected!");
			return;
		}

		// Get the session phrase from the client.
		Client curClient = new Client(socket);
		curClient.sendToClient("Please enter the session phrase.");

		// Check if the phrase is good.
		if (!isSessionPhraseGood(serverPhrase, curClient.readFromClient().getBytes())) {
			// add to Probation, check # of tries and kick if necessary.
			addToProbation(ip);
			if (appearancesInProbation(ip) >= tries) {
				removeFromProbation(ip);
				addToBanned(ip);
				clientStream.println("too many bad tries, you are banned!");
				closeClientConnection(socket);

				// Report to HOST that kicked tried to connect.
				synchronized (CryptoChat.hostChatHistoryBox) {
					CryptoChat.hostChatHistoryBox.append(systemName, ip + ": tried to connect and failed.");
				}
				return;
			}

			// message the client that he is kicked.
			if ((tries - appearancesInProbation(ip)) > 1) {
				clientStream.println("you've been kicked! you have " + (tries - appearancesInProbation(ip))
						+ " more chances.");
			} else {
				clientStream.println("you've been kicked! you have 1 last chance.");
			}
			closeClientConnection(socket);
			synchronized (usersList) {
				usersList.remove(curClient);
			}

			// Report to HOST that kicked tried to connect.
			synchronized (CryptoChat.hostChatHistoryBox) {
				CryptoChat.hostChatHistoryBox.append(systemName, ip + ": tried to connect and failed.");
			}
			return;
		}
		// If successful remove from probation.
		removeFromProbation(ip);

		// Start the connection.
		curClient.start();

		// Greet this current added client. IN CRYPTO !!!.
		String response = new String(Feistel.encrypt(
				("Welcome to the CryptoServer! There are " + usersList.size() + " users connected.").getBytes(),
				Feistel.key));
		curClient.sendToClient(response);

		// Inform all chat members that the new client joined IN CRYPTO !!!.
		messageAllMembers(curClient.getIp() + " joined");

		// Add the user to the list
		synchronized (usersList) {
			usersList.add(curClient);
			if (CryptoChat.hostUsersList != null) {
				CryptoChat.hostUsersList.setListData(getUsersIpArray());
			}
		}
	}

	private boolean isClientAlreadyConnected(String ip) {
		synchronized (usersList) {
			for (Client curClient : usersList) {
				if (curClient.getIp().equals(ip)) {
					return true;
				}
			}
			return false;
		}
	}

	private String[] getUsersIpArray() {
		synchronized (usersList) {
			String[] result = new String[usersList.size()];
			for (int i = 0; i < usersList.size(); i++) {
				result[i] = usersList.get(i).getIp();
			}
			return result;
		}
	}

	/**
	 * This method checks if the given encrypted phrase the same as our
	 * encrypted phrase.
	 * 
	 * @param phraseToCheck
	 *            The phrase we want to compare to our encrypted phrase.
	 * @return True if the given phrase is the same as ours, False otherwise.
	 */
	private boolean isSessionPhraseGood(byte[] serverPhrase, byte[] phraseToCheck) {
		// Check for different sizes first.
		if (serverPhrase.length != phraseToCheck.length) {
			return false;
		}

		// Check each byte. return false if encounter a difference.
		for (int i = 0; i < phraseToCheck.length; i++) {
			if (serverPhrase[i] != phraseToCheck[i]) {
				return false;
			}
		}
		return true;
	}

	private void closeClientConnection(Socket socket) {
		try {
			socket.getOutputStream().close();
		} catch (Exception e) {
		}
		try {
			socket.getInputStream().close();
		} catch (Exception e) {
		}
		try {
			socket.close();
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

	boolean isClientBanned(String ip) {
		if (CryptoChat.isTesting) {
			System.out.println("gggg");
			return false;
		}
		return appearancesInBanned(ip) > 0;
	}

	boolean isClientOnProbation(String ip) {
		if (CryptoChat.isTesting) {
			System.out.println("zdfsdf");
			return false;
		}
		return appearancesInProbation(ip) > 0;
	}

	/**
	 * Removes the given client from the chat.
	 * 
	 * @param client
	 *            The client that we want to remove from the server.
	 */
	void removeChatMember(Client client, int mode) {
		synchronized (usersList) {
			usersList.remove(client);
		}
		if (mode == 0) {
			// just kick as a warning.
		} else if (mode == 1) {
			// Add to probation list.
			addToProbation(client.getIp());
			client.sendToClient("You got kicked by the admin, and added to the probation list.");
			messageAllMembers(client.getIp() + " got kicked and is on probation!");
		} else if (mode == 2) {
			// Add to banned list.
			addToBanned(client.getIp());
			client.sendToClient("You got banned by the admin.");
			messageAllMembers(client.getIp() + " got banned from the CryptoServer.");
		}
		try {
			client.getSocket().close();
		} catch (IOException e) {
		}
	}

	/**
	 * Send a Massage to all connected users.
	 * 
	 * @param msg
	 *            The plain massage to be encrypted and send.
	 */

	void messageAllMembers(String msg) {
		// Encrypt the message first.
		String encMSG = new String(Feistel.encrypt(msg.getBytes(), Feistel.key));
		synchronized (usersList) {
			for (Client user : usersList) {
				user.sendToClient(encMSG);
			}
		}
	}

	void closeServer() {
		try {
			serverSocket.close();
		} catch (Exception e) {
		}
		try {
			usersList.clear();
		} catch (Exception e) {
		}
		try {
			probationUsersList.clear();
		} catch (Exception e) {
		}
		try {
			bannedUsersList.clear();
		} catch (Exception e) {
		}
		run = false;
	}

	public class Client extends Thread {
		/**
		 * This inner-class handles the CryptoChat connecting clients.
		 * 
		 * Happy cow says: "Muuuuuuu.."
		 * 
		 * @author Ben Sabah.
		 */

		private String ip;
		private Socket socket;
		private PrintStream toClient;
		private BufferedReader fromClient;

		public Client(Socket socket) throws IOException {
			ip = socket.getInetAddress().getHostAddress();
			this.socket = socket;
			fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toClient = new PrintStream(socket.getOutputStream(), true);
		}

		public void run() {
			try {
				String cryptoMSG;
				String plainMSG;
				for (;;) {
					// Get client Session Phrase.
					cryptoMSG = readFromClient();
					plainMSG = new String(Feistel.decrypt(cryptoMSG.getBytes(), Feistel.key));
					synchronized (CryptoChat.hostChatHistoryBox) {
						CryptoChat.hostChatHistoryBox.append(ip, plainMSG);
					}
				}
			} catch (Exception e) {
			} finally {
				removeChatMember(this, 0);
				closeClient();
			}
		}

		public String getIp() {
			return ip;
		}

		public Socket getSocket() {
			return socket;
		}

		public String readFromClient() throws IOException {
			return fromClient.readLine();
		}

		public void sendToClient(String msg) {
			toClient.println(msg);
		}

		private void closeClient() {
			try {
				toClient.close();
			} catch (Exception e) {
			}
			try {
				fromClient.close();
			} catch (Exception e) {
			}
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}
