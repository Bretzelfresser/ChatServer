package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * listener-thread reads from messages coming
 * 
 * @author roegerhe
 *
 */
public class ChatClientReceiver extends Thread {
	private boolean runValue = true;

	private BufferedReader clientIn;
	private final Socket clientSocket;


	ChatClientReceiver(Socket socket) throws IOException {
		/*
		 * TODO: 
		 * Initialize the reader to listen to messages from the server.
		 */
		this.clientSocket = socket;
		clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}


	public void run() {
		try {
			clientIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (runValue) {
			/*
			 * TODO: Read messages from server
			 * Print them on the comamnd line.
			 */
			setRunValue(!clientSocket.isClosed());
			String line;
			try {
				if ((line = clientIn.readLine()) != null){
					System.out.print(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setRunValue(boolean runValue) {
		this.runValue = runValue;
	}
}
