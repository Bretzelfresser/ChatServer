package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;


public class ChatClient {

	private int portNumber = 9001;
	private BufferedReader commandLineReader;

	private final Socket clientSocket;
	private final PrintWriter clientOut;
	private final ChatClientReceiver receiver;

	ChatClient() throws IOException {
		this.clientSocket = new Socket("127.0.0.1", portNumber);

		/*
		 * TODO
		 * make a connection to the chat-server
		 * start a ChatClientReceiver to listen to responses from the server and display them on the command line
		 * listen for command line inputs
		 */
		
		
		commandLineReader = new BufferedReader(new InputStreamReader(System.in));
		clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
		receiver = new ChatClientReceiver(clientSocket);
		receiver.start();
		
	}

	/**
	 * Start a new Chat client and listen to console inputs.
	 * "quit" is the command to stop the client
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();

		while (!client.clientSocket.isClosed()){
			String line;
			if ((line = client.commandLineReader.readLine()) != null){
				if (line.toLowerCase(Locale.ROOT).equals("quit")){
					client.clientOut.print("quit");
					client.clientSocket.close();
					return;
				}else {
					System.out.println("user send a message:" + line);
					client.clientOut.print(line);
				}
			}
		}
		/*
		 * TODO: Handle commandLine Input here and shut down ChatClient on "Quit"
		 * In order to quit the ChatClientReceiver-Thread, make use of the "runReceiver"-boolean.
		 */
	}

}
