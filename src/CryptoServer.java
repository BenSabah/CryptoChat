import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import java.net.Socket;
import java.net.ServerSocket;

import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class run and handles the CryptoChat Server.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

public class CryptoServer extends Thread {
	ServerSocket serverSocket;
	int port = 9229;
	private byte[] serverPhrase;
	ArrayList<Client> usersList;
	ArrayList<String> bannedUsersList;
	ArrayList<String> probationUsersList;
	int tries = 3;
	static boolean run;
	Random rnd = new Random();
	static final long ACCEPTABLE_TIME = 5000;
	Map<Client, Integer> saltMap = new HashMap<Client, Integer>(32);
	String adminName = "Admin";
	String systemName = "SYSTEM";
	String GREETING_FORMAT = "Hello %s, welcome to the CryptoServer! There %s %d %s connected";
	private static final int START_SALT = 7557;

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
		Feistel.key = strKey.getBytes();
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
		// TODO make this thing to a runnable object to prevent DOS attacking.
		String ip = socket.getLocalAddress().getHostAddress();

		// Check if the user is banned.
		if (isClientBanned(ip)) {
			closeClientConnection(socket);
			System.out.println("failed at " + 1);
			return;
		}

		// Check if already connected.
		if (isClientAlreadyConnected(ip)) {
			closeClientConnection(socket);
			System.out.println("failed at " + 2);
			return;
		}

		System.out.println("failed at " + 22);

		// Send the user the welcoming message.
		PrintStream clientStream = new PrintStream(socket.getOutputStream());
		clientStream.println("Please enter the encrypted session phrase, you have " + tries + " tries.");
		clientStream.flush();
		Client curClient = new Client(socket);

		System.out.println("b4 getting the ses phrase");
		// Get the session phrase from the client.
		if (CryptoChat.isTesting) {
			curClient.sendEncMsgToClient("test", new String(serverPhrase));
		}

		String[] data = curClient.readDecMsgFromClient();
		if (data == null) {
			System.out.println("fgffff");
		}
		System.out.println("after getting the ses phrase");

		String userDecPhrase = data[1];

		// Check if the ENCRYPTED phrase is bad.
		if (data == null || !CoreUtils.cmprByteArray(serverPhrase, userDecPhrase.getBytes())) {
			// Add to Probation
			addToProbation(ip);

			// Check the number of tries and ban if necessary.
			if (appearancesInProbation(ip) >= tries) {
				removeFromProbation(ip);
				addToBanned(ip);
				clientStream.println("Too many bad tries, you are banned!");
				clientStream.flush();
				closeClientConnection(socket);
				if (ChatHistory.showVerbose) {
					// Report to HOST that kicked tried to connect.
					CryptoChat.hostChatHistBox.append(CoreUtils.boldTextHTML(systemName), "Banned: " + ip
							+ " tried to connect and failed.");
				}
				System.out.println("failed at " + 3);
				return;
			}

			// Message the client that he is kicked.
			clientStream.println("you've been kicked! you have " + (tries - appearancesInProbation(ip))
					+ " more chance\\s.");
			clientStream.flush();
			closeClientConnection(socket);
			if (ChatHistory.showVerbose) {
				// Report to HOST that kicked tried to connect.
				CryptoChat.hostChatHistBox.append(CoreUtils.boldTextHTML(systemName), "Kicked: " + ip
						+ " tried to connect and failed.");
			}
			System.out.println("failed at " + 4);
			return;
		}

		// Since we passed the tests start the connection.
		curClient.start();

		// Remove from probation, add to the list of users & start the
		// connection.
		removeFromProbation(ip);
		addToUsersList(curClient);
		messageAllMembers(CoreUtils.boldTextHTML(adminName), ip + " joined the server.");
		System.out.println("failed at " + 5);
	}

	private boolean isClientAlreadyConnected(String ip) {
		synchronized (usersList) {
			for (Client curClient : usersList) {
				if (curClient.ip.equals(ip)) {
					return true;
				}
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	private String[] getUsersIpArray() {
		synchronized (usersList) {
			String[] result = new String[usersList.size()];
			for (int i = 0; i < usersList.size(); i++) {
				result[i] = usersList.get(i).ip;
			}
			return result;
		}
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

	public boolean addToUsersList(Client ip) {
		synchronized (usersList) {
			return usersList.add(ip);
		}
	}

	public boolean addToProbation(String ip) {
		synchronized (probationUsersList) {
			return probationUsersList.add(ip);
		}
	}

	public boolean addToBanned(String ip) {
		synchronized (bannedUsersList) {
			return bannedUsersList.add(ip);
		}
	}

	/**
	 * 
	 * Removes the given client from the chat, and if needed send a kick\ban
	 * message to that user.
	 * 
	 * @param client
	 *            The client that we want to remove from the server.
	 * @param mode
	 *            The mode to remove the user, 0-custom, 1-kick and add to
	 *            probation, 2-ban.
	 */
	public boolean removeFromUsersList(Client client, int mode) {
		// TODO change to add kick method and custom msg.
		boolean result;
		synchronized (usersList) {
			result = usersList.remove(client);
		}
		if (mode == 0) {
			// just kick as a warning.
		} else if (mode == 1) {
			// Add to probation list.
			addToProbation(client.ip);
			client.sendEncMsgToClient(CoreUtils.boldTextHTML(adminName),
					CoreUtils.boldTextHTML("You got kicked by the admin, and added to the probation list."));
			messageAllMembers(CoreUtils.boldTextHTML(adminName),
					CoreUtils.boldTextHTML(client.ip + " got kicked and is on probation!"));
		} else if (mode == 2) {
			// Add to banned list.
			addToBanned(client.ip);
			client.sendEncMsgToClient(CoreUtils.boldTextHTML(adminName),
					CoreUtils.boldTextHTML("You got banned by the admin."));
			messageAllMembers(CoreUtils.boldTextHTML(adminName),
					CoreUtils.boldTextHTML(client.ip + " got banned from the CryptoServer."));
		}
		client.closeClient();
		return result;
	}

	public void removeFromProbation(String ip) {
		synchronized (probationUsersList) {
			for (int i = 0; i < probationUsersList.size(); i++) {
				if (probationUsersList.get(i).equals(ip)) {
					probationUsersList.remove(i);
					i = -1; // Restart the check from the start.
				}
			}
		}
	}

	public void removeFromBannedList(String ip) {
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

	private boolean isClientBanned(String ip) {
		boolean isBanned = appearancesInBanned(ip) > 0;
		if (CryptoChat.isTesting && isBanned) {
			System.out.println(ip + ": should have been banned.");
			return false;
		}
		return isBanned;
	}

	@SuppressWarnings("unused")
	private boolean isClientOnProbation(String ip) {
		boolean isKicked = appearancesInProbation(ip) > 0;
		if (CryptoChat.isTesting && isKicked) {
			System.out.println(ip + ": should have been kicked.");
			return false;
		}
		return isKicked;
	}

	/**
	 * Send a Massage to all connected users.
	 * 
	 * @param msg
	 *            The plain massage to be encrypted and sent.
	 */
	public void messageAllMembers(String user, String msg) {
		// Send the message to all the users.
		synchronized (usersList) {
			for (Client client : usersList) {
				CryptoChat.hostChatHistBox.append(adminName, msg);
				// TODO need to NOT send the msg to the original sender.
				client.sendEncMsgToClient(user, msg);
			}
		}
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String name) {
		adminName = name;
	}

	public void closeServer() {
		run = false;
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
		private String userName;
		private int salt = START_SALT;;

		public Client(Socket socket) throws IOException {
			ip = socket.getInetAddress().getHostAddress();
			userName = ip;
			this.socket = socket;
			fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toClient = new PrintStream(socket.getOutputStream());
		}

		public void run() {
			// Use the try-catch to know when the client closed the connection.
			try {
				// Ask for user-name.
				userName = new String(readDecMsgFromClient()[1]);
				String greeting;

				// Greet the new client.
				if (usersList.size() == 1) {
					greeting = String.format(GREETING_FORMAT, userName, "is", usersList.size(), "user");
				} else {
					greeting = String.format(GREETING_FORMAT, userName, "are", usersList.size(), "users");
				}
				sendEncMsgToClient(CoreUtils.boldTextHTML(adminName), greeting);

				// Send the unique salt.
				salt = rnd.nextInt(Integer.MAX_VALUE);
				saltMap.put(this, salt);
				sendEncMsgToClient(adminName, salt + "");

				// Read messages from the client.
				String[] incoming;
				String user;
				String msg;
				while (true) {
					incoming = readDecMsgFromClient();

					// If the message is null (bad time-stamp) we close the
					// connection.
					if (incoming == null) {
						break;
					}

					user = incoming[0];
					msg = incoming[1];
					CryptoChat.hostChatHistBox.append(user, msg);
					messageAllMembers(user, msg);
				}
			} catch (Exception e) {
			} finally {
				closeClient();
			}
		}

		/**
		 * This method waits and reads a message the client set, than breaks it
		 * up into 2 parts, the first part is the time-stamp, and the second
		 * part is the message itself. if the time-stamp is wrong we return
		 * null.
		 * 
		 * Thanks to Niki (rudi) for the muse.
		 * 
		 * @return the byte array of the given message, the first cell contains
		 *         the user-name & the 2nd cell contains the message.
		 * @throws IOException
		 *             is thrown when there's an issue while trying to read from
		 *             the server.
		 */
		public String[] readDecMsgFromClient() throws IOException {
			int curIndex = 0;
			byte[] decMsg;
			if (CryptoChat.isTesting) {
				decMsg = fromClient.readLine().getBytes();
			} else {
				decMsg = Feistel.decrypt(fromClient.readLine().getBytes(), Feistel.key);
			}

			// Check if the timing is acceptable.
			long receivedTime = CoreUtils.bytesToLong(CoreUtils.getBytes(decMsg, curIndex, 8));
			if (Math.abs(System.currentTimeMillis() - receivedTime) > ACCEPTABLE_TIME) {
				System.out.println("msg failed at time");
				return null;
			}
			curIndex += 8;

			// Check if the salt is correct.
			int salt = CoreUtils.bytesToInt(CoreUtils.getBytes(decMsg, curIndex, 4));
			if (this.salt != salt) {
				System.out.println("msg failed at salt");
				return null;
			}
			curIndex += 4;

			// Get the size of the user-name.
			int sizeOfUserName = CoreUtils.bytesToInt(CoreUtils.getBytes(decMsg, curIndex, 4));
			curIndex += 4;

			// Get the user-name.
			String user = new String(CoreUtils.getBytes(decMsg, curIndex, sizeOfUserName));
			curIndex += sizeOfUserName;

			// If passed all, return the message.
			String msg = new String(CoreUtils.getBytes(decMsg, curIndex, decMsg.length - curIndex));
			String[] result = { user, msg };
			return result;
		}

		/**
		 * This method add a time-stamp, the size of the user name, the
		 * user-name, user-specific salt to the give message, encrypt it, than
		 * it sends the combined message to the client.
		 * 
		 * Thanks to Niki (rudi) for the muse.
		 * 
		 * @param user
		 *            The user that sent the message.
		 * @param msg
		 *            The message that the user wanted to send.
		 */
		public void sendEncMsgToClient(String user, String msg) {
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
				toClient.println(new String(combinedMsg));
				toClient.flush();
				return;
			}
			toClient.println(new String(Feistel.encrypt(combinedMsg, Feistel.key)));
			toClient.flush();
		}

		private void closeClient() {
			CryptoChat.joinChatHistBox.append(userName,
					CoreUtils.boldTextHTML(usersList + " have been disconnected from the server."));
			removeFromUsersList(this, 0);
			saltMap.remove(this);
			try {
				fromClient.close();
			} catch (Exception e) {
			}
			try {
				toClient.close();
			} catch (Exception e) {
			}
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}
