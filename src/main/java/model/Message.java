package model;

// Message.java
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
	private String sender;
	private String content;
	private LocalDateTime timestamp;

	public Message(String sender, String content) {
		this.sender = sender;
		this.content = content;
		this.timestamp = LocalDateTime.now(); // current time
	}

	public String getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
