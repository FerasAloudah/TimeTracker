import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class TrackerWindow implements NativeKeyListener {
	
	private Stage window;
	private TextArea noteWindow;
	private Timeline noteTimer;
	private Timeline windowTimer;
	private TrackerFile myNotes;
	private TrackerAnalyzeNotes notes;
	
	private static final int MAX_TITLE_LENGTH = 1024;
	private static boolean cPressed = false;
	private static boolean turnOff = false;
	
	public TrackerWindow() {
		window = new Stage(StageStyle.UNDECORATED);
		noteWindow = new TextArea();
		
		VBox vbox = new VBox(noteWindow);
		Scene noteScene = new Scene(vbox, 211, 93);
		
		window.setOnCloseRequest(e -> {
			
			window.close();
		});
				
		noteWindow.setStyle("-fx-text-fill: gold; -fx-background-color: #0c1816; -fx-border-color: gold");
		noteWindow.setEditable(false);
		noteWindow.setMouseTransparent(true);
		noteWindow.setFocusTraversable(false);
		window.setX(347);
		window.setY(987);
		window.setScene(noteScene);
		
		//Removing background color and vertical bar.
		window.show();
		Region region = (Region) noteWindow.lookup(".content");
		region.setStyle("-fx-background-color: #0c1816;");
		ScrollBar scrollBarv = (ScrollBar) noteWindow.lookup(".scroll-bar:vertical");
		scrollBarv.setStyle("-fx-opacity: 0;");
		window.hide();
		
		//Reading from MyNotes.
		notes = new TrackerAnalyzeNotes();
		try {
			File myNotesPath = new File("MyNotes Path.txt");
			BufferedReader reader = new BufferedReader(new FileReader(myNotesPath));
			myNotes = new TrackerFile(reader.readLine());
			reader.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void runTracker() {
		//Clear old notes.
		notes.clearNotes();
		updateNotes(notes.getNotes());
        //Declare the timers.
        noteTimer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
        	try {
    			notes.readNote(myNotes.readLastLine());
    			notes.sortArray();
    			updateNotes(notes.getNotes());
    			notes.minusOne();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }));
        windowTimer = new Timeline(new KeyFrame(Duration.millis(250), e -> {
        	//Getting active window's name.
			char[] currentWindowName = windowName();
			//Checking if it's league or not and if C is pressed.
    		if (Native.toString(currentWindowName).equals("League of Legends (TM) Client") && !cPressed && !turnOff) {
                window.show();
                window.setAlwaysOnTop(true);
            }
            else {
                window.hide();
            }
        }));
        
        noteTimer.setCycleCount(Timeline.INDEFINITE);
        windowTimer.setCycleCount(Timeline.INDEFINITE);
        
        noteTimer.play();
        windowTimer.play();
	}
	
	public void updateNotes(String notes) {
		noteWindow.setText(notes);
	}
	
	public void addNote(Note note) {
		notes.addNote(note);
	}
	
	public char[] windowName() {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        return buffer;
	}
	
	public void closeTracker() {
		//Stopping the timer and hiding the frame.
		noteTimer.stop();
		windowTimer.stop();
		window.hide();
	}
	
	public void runListener() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
		logger.setUseParentHandlers(false);
		try {
			if (!GlobalScreen.isNativeHookRegistered())
				GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			AlertBox.display("", "There was a problem registering the native hook.");
			return;
		}
		GlobalScreen.addNativeKeyListener(new TrackerWindow());
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_C)
			cPressed = true;
		else if (e.getKeyCode() == 52)
			turnOff = !turnOff;
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		cPressed = false;
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {}
	
}

