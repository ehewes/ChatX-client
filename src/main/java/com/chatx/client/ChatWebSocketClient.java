package com.chatx.client;

    import org.java_websocket.client.WebSocketClient;
    import org.java_websocket.handshake.ServerHandshake;

    import java.net.URI;

    public class ChatWebSocketClient extends WebSocketClient {
        public ChatWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connected to server");
        }

        @Override
        public void onMessage(String message) {
            System.out.println("Received: " + message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Disconnected from server");
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }
    }