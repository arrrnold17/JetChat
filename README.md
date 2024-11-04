# JetChat

This is a demo for Jetbrains 2025 internship application. 
It is a chat application that allows users to send messages to each other in real time.

## Architecture
- Server: Once the user start a new chat, the server will be created. It includes a set of ClientHandler, which is responsible for receiving the messaging and boardcasting to all other clients.
- Client: The client will connect to the server and send messages to the server as well as receive the boardcasted messages from the server.
- ChatApp: A simple GUI.

## Example
- Start the ChatApp
```shell
Enter username:
$ Alex
Do you want to start a new chat or join an existing chat?
1 - Start new chat
2 - Join existing chat
$ 1
Enter port number to start chat on:
$ 8080
Connected to the chat server
[20:43:37] Martin: Hi
$ Hi, Martin
```

- Start another
```shell
Enter username:
$ Martin
Do you want to start a new chat or join an existing chat?
1 - Start new chat
2 - Join existing chat
$ 2
Enter port number to connect to:
$ 8080
Connected to the chat server
$ Hi
[20:43:51] Alex: Hi, Martin
```

