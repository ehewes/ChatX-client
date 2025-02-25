package com.chatx;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.chatx.config.GetClientDetails.checkExistingClient;

public class ChatxApplication {
    private static WebSocketClient client;
    private static String username;

    public static void main(String[] args) {
        try {
            username = checkExistingClient();
            connectToWebSocket();
            handleInput();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void connectToWebSocket() throws URISyntaxException, InterruptedException {
        URI serverUri = new URI("ws://localhost:8080/chatx");

        client = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Connected to: " + getURI());
                // Send the username to the server in JSON format
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonUsername = objectMapper.writeValueAsString(Collections.singletonMap("name", username));
                    client.send(jsonUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Server: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                String message;
                switch (code) {
                    case 1000:
                        message = "Normal closure: " + reason;
                        break;
                    case 1001:
                        message = "Server going away: " + reason;
                        break;
                    case 1002:
                        message = "Invalid status code received: " + reason;
                        break;
                    case 1006:
                        message = "Connection failed (check server)";
                        break;
                    case 1011:
                        message = "Server error or unexpected condition: " + reason;
                        break;
                    default:
                        message = "Closed with code: " + code + " - Reason: " + reason;
                }
                System.out.println(message);
            }

            @Override
            public void onError(Exception ex) {
                String errorMsg = "Error: " + ex.getMessage();
                if (ex instanceof java.net.ConnectException) {
                    errorMsg += "\nCheck if server is running at " + getURI();
                }
                System.out.println(errorMsg);

                if (!isOpen()) {
                    try {
                        System.out.println("Attempting reconnect...");
                        reconnectBlocking();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        // Start connection
        System.out.println("Connecting to " + serverUri + "...");
        client.connectBlocking();
    }

    private static void handleInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if (!message.isEmpty() && client.isOpen()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("name", username);
                    messageMap.put("message", message);
                    String jsonMessage = objectMapper.writeValueAsString(messageMap);
                    client.send(jsonMessage);
                    System.out.println("You: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Error: Not connected to server or empty message!");
            }
        }
    }
}