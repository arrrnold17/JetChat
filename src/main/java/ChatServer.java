import model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
	a chat server that listens for incoming connections from clients and broadcasts messages to all connected clients\n
	enable multiple clients to connect to the server and send messages to each other\n
 */
public class ChatServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);
	private final int port;
	private ServerSocket serverSocket;
	private final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
	private volatile boolean isRunning = true;

	public ChatServer(int port) {
		this.port = port;
	}

	/**
	 * Starts the chat server on the specified port
	 * @throws IOException if there is an error starting the server
	 */
	public void start() throws IOException {
		try {
			serverSocket = new ServerSocket(port);
			LOGGER.info("Chat server started on port " + port);
			while (isRunning) {
				try {
					Socket socket = serverSocket.accept();
					LOGGER.info("New client connected");
					// 创建客户端处理器
					ClientHandler clientHandler = new ClientHandler(socket, this);
					addClient(clientHandler);

					Thread.ofVirtual().start(clientHandler);
				} catch (IOException e) {
					if (isRunning) {
						LOGGER.error("Error accepting client connection: " + e.getMessage());
					} else {
						LOGGER.info("Server is stopping, stop accepting new connections.");
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Could not start server: " + e.getMessage());
		} finally {
			stop();
		}
	}

	private void stop() {
		isRunning = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			LOGGER.info("Chat server stopped.");
		} catch (IOException e) {
			LOGGER.error("Error closing server socket: " + e.getMessage());
		}
	}

	/**
	 * Broadcasts a message to all connected clients except the client that sent the message
	 * @param message: message to broadcast
	 * @param excludeClient: client to exclude from broadcast, might be clientHandler itself
	 */
	public void broadcast(Message message, ClientHandler excludeClient) {
		readLock.lock();
		try {
			for (ClientHandler client : clientHandlers) {
				if (client != excludeClient) {
					client.sendMessage(message);
				}
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Adds a client handler to the list of connected clients
	 * @param clientHandler: client handler to add
	 */
	private void addClient(ClientHandler clientHandler) {
		writeLock.lock();
		try {
			clientHandlers.add(clientHandler);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes a client handler from the list of connected clients
	 * @param clientHandler: client handler to remove
	 */
	public void removeClient(ClientHandler clientHandler) {
		writeLock.lock();
		try {
			clientHandlers.remove(clientHandler);
		} finally {
			writeLock.unlock();
		}

		// Stop the server if there are no more clients
		if (clientHandlers.isEmpty()) {
			stop();
		}
	}
}
