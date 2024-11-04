import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class ChatApp {
	private final static Logger LOGGER = LoggerFactory.getLogger(ChatApp.class);
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter username:");
		String username = scanner.nextLine();

		System.out.println("Do you want to start a new chat or join an existing chat?");
		System.out.println("1 - Start new chat");
		System.out.println("2 - Join existing chat");
		int choice = scanner.nextInt();
		scanner.nextLine();

		if (choice == 1) {
			System.out.println("Enter port number to start chat on:");
			int port = scanner.nextInt();
			scanner.nextLine(); // consume newline
			ChatServer server = new ChatServer(port);
			// Start the server in a separate thread
			new Thread(() -> {
				try {
					server.start();
				} catch (IOException e) {
					LOGGER.error("Error starting chat server", e);
				}
			}).start();

			// Start the client in the main thread
			ChatClient client = new ChatClient("localhost", port, username);
			client.execute();
		} else if (choice == 2) {
			System.out.println("Enter port number to connect to:");
			int port = scanner.nextInt();
			scanner.nextLine();

			ChatClient client = new ChatClient("localhost", port, username);
			client.execute();
		} else {
			System.out.println("Invalid choice");
		}
	}
}
