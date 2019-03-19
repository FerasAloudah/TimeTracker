import javafx.application.Application;
import javafx.stage.Stage;

public class TestServer extends Application {
	
	public static void main(String[] args) {
		launch(args);
		
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Server test = new Server();
		test.startRunning(5555, 5);		
	}

}
