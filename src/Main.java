import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage window;
	private Scene mainScene;
	private GridPane gridPane;
	private TextField fileBrowser;
	private TextField IPBox;
	private TextField PortBox;
	private Button startAndCloseButton;
	private Button browseButton;
	private Button connectToServer;
	private Button startServer;
	private TrackerWindow tracker;
	
	private Boolean foundFile;
	private Boolean onOrOff;
	
	private ObjectInputStream input;
	private Note note;
	private Socket connection;
	
	private static ObjectOutputStream output;
	private static boolean connected = false;	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		Image icon = new Image("file:test.png");
		window.getIcons().add(icon);
		tracker = new TrackerWindow();
		gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setVgap(8);
		gridPane.setHgap(10);
		tracker.runListener();
		
		//Some flags to use.
		onOrOff = true;
		foundFile = false;
		
		//File browser to get MyNotes location.
		fileBrowser = new TextField();
		fileBrowser.setPromptText("MyNotes.txt location.");
		fileBrowser.setMinWidth(400);
		fileBrowser.setEditable(false);
		GridPane.setConstraints(fileBrowser, 0, 0);
		
		//Checks if file path is already saved.
		checkFile();
		
		//Button to browse for MyNotes.txt.
		browseButton = new Button();
		browseButton.setText("Browse");
		browseButton.setMinSize(70, 20);
		GridPane.setConstraints(browseButton, 1, 0);
		
		browseButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(window);
			if (file != null) {
                readFile(file);
            }
		});
		
		//Button to start or stop the tracker.
		startAndCloseButton = new Button();
		startAndCloseButton.setText("Start");
		startAndCloseButton.setMinSize(70, 20);
		GridPane.setConstraints(startAndCloseButton, 2, 0);
		
		startAndCloseButton.setOnAction(e -> {
			if (!foundFile) {
				AlertBox.display("", "Please make sure you have selected \"MyNotes.txt\".");
			}
			else if (onOrOff) {
				tracker.runTracker();
				startAndCloseButton.setText("Stop");
				onOrOff = false;
			}
			else {
				tracker.closeTracker();
				startAndCloseButton.setText("Start");
				onOrOff = true;
			}
		});
		
		IPBox = new TextField();
		IPBox.setPromptText("Server IP");
		IPBox.setMaxWidth(110);
		
		
		PortBox = new TextField("5555");
		PortBox.setPromptText("Port");
		PortBox.setMaxWidth(50);
		
		connectToServer = new Button();
		connectToServer.setText("Connect");
		connectToServer.setMinSize(70, 20);
		
		startServer = new Button();
		startServer.setText("Start Server");
		startServer.setMinSize(80, 20);
		startServer.setOnAction(e -> {
			Server test = new Server();
			test.startRunning(5555, 10);
		});
		
		IPBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent e) {
	            if (e.getCode().equals(KeyCode.ENTER)) {
	            	connectToServer.fire();
	            }
	        }
	    });
		
		PortBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent e) {
	            if (e.getCode().equals(KeyCode.ENTER)) {
	                connectToServer.fire();
	            }
	        }
	    });
		
		connectToServer.setOnAction(e -> {
			if (!connected) {
				try {
					attemptToConnect(IPBox.getText(), PortBox.getText().length() > 0 ? Integer.parseInt(PortBox.getText()) : 5555);
					
				} catch (NumberFormatException exception) {
					AlertBox.display("", "Please use numbers only for the port.");
				}
			}
			else {
				closingConnection();
				connectToServer.setText("Connect");
				connected = false;
			}
		});
		
		HBox serverAddress = new HBox(5);
		serverAddress.getChildren().addAll(IPBox, PortBox, connectToServer, startServer);
		GridPane.setConstraints(serverAddress, 0, 1);
		
		gridPane.getChildren().addAll(fileBrowser, browseButton, startAndCloseButton, serverAddress);
		
		mainScene = new Scene(gridPane, 700, 150);
		window.setOnCloseRequest(e -> {
			e.consume();
			closeProgram();
		});
		window.setTitle("Time Tracker. v1.0");
		window.setScene(mainScene);
		window.setResizable(false);
		window.show();
	}

	private void closeProgram() {
		boolean answer = ConfirmBox.display("", "Are you sure you want to exit?");
		if (answer) {
			if (connected) closingConnection();
			System.exit(0);
		}
	}
	
	private void checkFile() {
		String filePath = "";
		try {
			//Reads first line of "MyNotes Path" file.
			File myNotesPath = new File("MyNotes Path.txt");
			BufferedReader br = new BufferedReader(new FileReader(myNotesPath));
			if (!myNotesPath.exists()) {
				myNotesPath.createNewFile();
			}
			filePath = br.readLine();
			
			//Checks if file path exists or not.
			if (filePath != "") {
				fileBrowser.setText(filePath);
				if (filePath.contains("MyNotes.txt")) {
					foundFile = true;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readFile(File browsedFile) {
		try {
			//Checks for "MyNotes Path" file.
			File myNotesPath = new File("MyNotes Path.txt");
			if (!myNotesPath.exists()) {
				myNotesPath.createNewFile();
			}
			
			//Checks the browsed file.
			if (browsedFile.getAbsolutePath().contains("MyNotes.txt")) {
				foundFile = true;
				fileBrowser.setText(browsedFile.getAbsolutePath());
				PrintWriter writeToNotes = new PrintWriter(myNotesPath);
				writeToNotes.write(browsedFile.getAbsolutePath());
				writeToNotes.close();
			}
			else {
				AlertBox.display("", "Please make sure you have selected \"MyNotes.txt\".");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void attemptToConnect(String serverIP, int port) {
		try {
			connectToServer(serverIP, port);
			setupStreams();
			whileConnected();
		} catch (IOException e) {
			AlertBox.display("", "Failed to connect!");
			return;
		}
		
		connectToServer.setText("Disconnect");
		connected = true;
	}
	
	private void connectToServer(String serverIP, int port) throws IOException {
		connection = new Socket(InetAddress.getByName(serverIP), port);
		connected = true;
	}
	
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		AlertBox.display("", "Connected to: " + connection.getInetAddress().getHostName());
	}
	
	private void whileConnected() {
		Timer connectedTask = new Timer();
		connectedTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            	try {
    				note = (Note) input.readObject();
    				if (note.getNote().equals("CLOSED")) {
    					Platform.runLater(new Runnable() {
							@Override
							public void run() {
		    					connectedTask.cancel();
								AlertBox.display("", "Server has ended the connection");
		    					connectToServer.fire();
							}
    					});
    				}
    				addNote(note);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
            }
        }, 0, 1000);
	}
	
	private void closingConnection() {
		if (connection != null && connection.isConnected()) {
			AlertBox.display("", "Closing connection...");
			try {
				output.writeObject(new Note("CLOSED", 0, 0));
				output.flush();
				output.close();
				input.close();
				connection.close();
			} catch (SocketException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendNote(Note note) {
		if (!connected) return;
		try {
			output.writeObject(note);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addNote(Note note) {
		tracker.addNote(note);
	}
	
}
