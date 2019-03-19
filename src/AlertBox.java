import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

	public static void display(String title, String message) {
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(300);
		
		Label alertMessage = new Label();
		alertMessage.setText(message);
		
		Button closeButton = new Button();
		closeButton.setText("Close");
		closeButton.setMinWidth(70);
		closeButton.setOnAction(e -> window.close());
		
		VBox alertLayout = new VBox(10);
		alertLayout.getChildren().addAll(alertMessage, closeButton);
		alertLayout.setAlignment(Pos.CENTER);
		
		Scene alertScene = new Scene(alertLayout);
		window.setScene(alertScene);
		window.setResizable(false);
		try {
			window.showAndWait();
		} catch (IllegalStateException e) {
			window.show();
		}
	}
	
}
