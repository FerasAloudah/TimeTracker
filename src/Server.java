import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Server {
	
	private Stage window;
	private TextArea serverWindow;
	private ServerSocket server;
	private Socket connection;
	private ClientThread threads[];
	
	public Server() {
		window = new Stage();
		serverWindow = new TextArea();
		serverWindow.setEditable(false);
		VBox vbox = new VBox(serverWindow);
		Scene serverScene = new Scene(vbox, 300, 150);
		window.setScene(serverScene);
		window.setResizable(false);
		window.setTitle(ClientThread.nbUsers + (ClientThread.nbUsers == 1 ? " User " : " Users ") + "Connected");
		window.setOnCloseRequest(e -> {
			try {
				for (int i = 0; i < threads.length; i++) {
					if (threads[i] != null) {
						threads[i].closeConnections();
						break;
					}
				}
				window.close();
				server.close();
				if (connection != null && connection.isConnected())
					connection.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
			}
		});
		window.show();
	}
	
	public void startRunning(int port, int size) {
		try {
			server = new ServerSocket(port);
			threads = new ClientThread[size];
			new Timer().scheduleAtFixedRate(new TimerTask() {
	            @Override
	            public void run() {
					try {
						waitForConnection();
					} catch(EOFException e) {
						showMessage("\nServer ended the connection!");
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }, 0, 1000);
		} catch (BindException e) {
			AlertBox.display("", "Address already in use.");
			window.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Timeline titleChange = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	changeTitle(ClientThread.nbUsers + (ClientThread.nbUsers == 1 ? " User " : " Users ") + "Connected");
		    }
		}));
		
		titleChange.setCycleCount(Timeline.INDEFINITE);
		titleChange.play();
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		if (!server.isClosed()) {
			try {
				showMessage("Waiting for someone to connect...\n");
				connection = server.accept();
				for (int i = 0; i < threads.length; i++) {
					if (threads[i] == null) {
						(threads[i] = new ClientThread(connection, threads)).start();
						break;
					}
				}
				showMessage("Now connected to " + connection.getInetAddress().getHostName() + "\n");
			} catch (SocketException e) {
				e.printStackTrace();
			}
 		}
	}

	//update chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						serverWindow.appendText(text);
					}
				});
	}
	
	private void changeTitle(String newTitle) {
		window.setTitle(newTitle);
	}
	
}