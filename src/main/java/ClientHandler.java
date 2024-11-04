import model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

/**
 * Used by the server to handle communication with a client, including
 * receiving messages from the client and broadcasting them to all other clients
 */
public class ClientHandler implements Runnable {
	private final Socket socket;
	private final ChatServer server;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String username;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

	public ClientHandler(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}

	/**
	 * Reads messages from the client and broadcasts them to all other clients
	 */
	@Override
	public void run() {
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());

			// First message from client is the username
			username = (String) input.readObject();
			LOGGER.info("User connected: " + username);

			Message message;
			while ((message = (Message) input.readObject()) != null) {
				LOGGER.info("Received message from " + message.getSender() + ": " + message.getContent());
				server.broadcast(message, this);
			}
		} catch (EOFException ex) {
			LOGGER.error("Client: " + username + " disconnected.");
		} catch (IOException | ClassNotFoundException ex) {
			LOGGER.error("Error handling client", ex);
		} finally {
			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException ex) {
				LOGGER.error("Error closing client socket", ex);
			}
			server.removeClient(this);
		}
	}

	/**
	 * Sends a message to the client
	 * @param message: message to send
	 */
	public void sendMessage(Message message) {
		try {
			output.writeObject(message);
			output.flush();
		} catch (IOException ex) {
			LOGGER.error("Error sending message to client", ex);
		}
	}
}

