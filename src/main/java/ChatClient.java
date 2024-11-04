// ChatClient.java
import model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// A chat client that connects to a chat server and sends and receives messages
public class ChatClient {
	private String hostname;
	private int port;
	private String username;
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

	public ChatClient(String hostname, int port, String username) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
	}

	/**
	 * Starts the chat client, connects to the server, and starts reading and writing messages
	 */
	public void execute() {
		try {
			socket = new Socket(hostname, port);
			System.out.println("Connected to the chat server");

			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());

			// Send username to server
			output.writeObject(username);
			output.flush();

			new Thread(new ReadThread()).start();
			new Thread(new WriteThread()).start();

		} catch (IOException ex) {
			LOGGER.error("Error connecting to server", ex);
		}
	}

	/**
	 * Thread to read messages from the server
	 */
	private class ReadThread implements Runnable {
		@Override
		public void run() {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				while (true) {
					Message message = (Message) input.readObject();
					String timeString = message.getTimestamp().format(formatter);
					System.out.println("[" + timeString + "] " + message.getSender() + ": " + message.getContent());
				}
			} catch (IOException | ClassNotFoundException ex) {
				System.out.println("Connection closed");
			}
		}
	}

	/**
	 * Thread to write messages to the server
	 */
	private class WriteThread implements Runnable {
		@Override
		public void run() {
			try {
				Scanner scanner = new Scanner(System.in);
				while (true) {
					System.out.println("Me:");
					String text = scanner.nextLine();
					if (text.equalsIgnoreCase("quit")) {
						socket.close();
						break;
					}
					Message message = new Message(username, text);
					output.writeObject(message);
					output.flush();
				}
			} catch (IOException ex) {
				System.out.println("Disconnected from server.");
			}
		}
	}
}
