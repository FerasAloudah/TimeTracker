import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

class ClientThread extends Thread {

	public static int nbUsers = 0;
	
	private ObjectInputStream is = null;
	private ObjectOutputStream os = null;
	private Socket clientSocket = null;
	private ClientThread[] threads;
	private int maxClientsCount;

	public ClientThread(Socket clientSocket, ClientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public void run() {
		int maxClientsCount = this.maxClientsCount;
		ClientThread[] threads = this.threads;
	  
		try {
			is = new ObjectInputStream(clientSocket.getInputStream());
			os = new ObjectOutputStream(clientSocket.getOutputStream());
			nbUsers++;
			Note note = null;
			
			while (true) {
				try {
					note = (Note) is.readObject();
		    	  	if (note != null) {
			    	  	for (int i = 0; i < maxClientsCount; i++) {
			    	  		if (threads[i] != null && threads[i] != this) {
			    	  			threads[i].os.writeObject(note);
			    	  			os.flush();
			    	  		}
			    	  	}
			    	  	note = null;
		    	  	}
				} catch (SocketException | EOFException e) {
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] == this) {
					threads[i] = null;
				}
			}

			is.close();
			os.close();
			clientSocket.close();
			nbUsers--;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	}

	public void closeConnections() throws IOException {
		for (int i = 0; i < maxClientsCount; i++) {
	  		if (threads[i] != null) {
	  			threads[i].os.writeObject(new Note("CLOSED", 0, 0));
	  			os.flush();
	  		}
		}
	}
  
}