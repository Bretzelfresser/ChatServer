package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

/**
 * Chat server that handles message communication with multiple clients.
 *
 * @author u.a.: roegerhe
 */
public class ChatServer extends Thread {

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9001;

    /**
     * The set of all registered users with their output printers
     */
    private static HashMap<String, PrintWriter> registeredUsers = new HashMap<String, PrintWriter>();

    private final ServerSocket serverSocket;


    public ChatServer() throws IOException {
        // TODO initialize a Server
        this.serverSocket = new ServerSocket(PORT);
    }

    public void run() {
        // TODO accept new connections and instantiate a new ClientHandler for each session
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client has connected");
                new ClientHandler(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public static void main(String[] args) throws Exception {
        ChatServer server = new ChatServer();
        server.start();



        /* command line interaction for server */
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line;
            if ((line = consoleReader.readLine()) != null) {
                if (line.equals("quit")) {
                    server.serverSocket.close();
                    break;
                }
            }
        }

        // TODO: listen for commands from commandLine and shut down server on
        // "quit" command.

    }


    /********************* session class ******************************************************/

    /**
     * A handler thread class to start a new session for each client.
     * Responsible for a dealing with a single client and sending its messages
     * to the respective user.
     */
    private class ClientHandler extends Thread {


        private final Socket client;
        private final BufferedReader clientIn;
        private final PrintWriter clientOut;
        private String username;

        /**
         * Constructs a handler thread.
         */
        public ClientHandler(Socket socket) throws IOException {
            this.client = socket;
            this.clientOut = new PrintWriter(socket.getOutputStream(), true);
            this.clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // TODO init handler
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name
         * until a unique one has been submitted, then acknowledges the name and
         * registers the output stream for the client in a global set.
         */
        public void run() {
            while (username == null && !client.isClosed()) {
                clientOut.print("enter new username: ");
                System.out.println("user needs to enter a username");
                String line;
                try {
                    if (!((line = clientIn.readLine()) != null) && line.length() > 0) {
                        System.out.println("line");
                        if (line == null)
                            return;
                        if (line.toLowerCase(Locale.ROOT).equals("quit")){
                            client.close();
                            return;
                        }
                        synchronized (this) {
                            if (!registeredUsers.containsKey(line)) {
                                this.username = line;
                                registeredUsers.put(username, clientOut);
                                clientOut.println("username successful set");
                                clientOut.println("List of available users:");
                                for (String user : registeredUsers.keySet()){
                                    if (!user.equals(username))
                                        clientOut.println(user);
                                }
                            }else {
                                clientOut.println("username already taken");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            while (!client.isClosed()) {
                String line;
                try {
                    if ((line = clientIn.readLine()) == null){
                        if (line.toLowerCase(Locale.ROOT).equals("quit")){
                            client.close();
                            return;
                        }
                        String[] data = line.split(":");
                        if (data.length != 2){
                            clientOut.println("invalid format of the message: " + line);
                            continue;
                        }
                        String reciever = data[0];
                        String message = data[1];
                        if (!registeredUsers.containsKey(reciever)){
                            clientOut.println("no user with username: " + reciever + " exists");
                            continue;
                        }
                        synchronized (this){
                            registeredUsers.get(reciever).println(username + ": " + message);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            /*
             * TODO: Request a name from this client. Keep requesting until a
             * name is submitted that is not already used. Note that checking
             * for the existence of a name and adding the name must be done
             * while locking the set of names.
             */

            /*
             * TODO Now that a successful name has been chosen, display all
             * available users to the client.
             */

            /*
             * TODO: Here comes the interesting part: Listen for input from the
             * client. Falls "quit": User von der Liste der User entfernen,
             * Thread schließen
             * Ansonsten: Nachricht deserialisieren und an den
             * Zieluser weiterleiten. Falls der User nicht verfügbar ist, eine
             * Fehlermeldung auf der Server-Konsole ausgeben.
             */
        }
    }
}