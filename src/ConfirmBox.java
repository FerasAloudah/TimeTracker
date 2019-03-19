import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
	
	 static boolean answer;

	    public static boolean display(String title, String message) {
	        Stage window = new Stage();
	        
	        //Block events to other windows
	        window.initModality(Modality.APPLICATION_MODAL);
	        window.setTitle(title);
	        window.setMinWidth(280);
	        Label label = new Label();
	        label.setText(message);

	        //Create two buttons
	        Button yesButton = new Button("Yes");
	        Button noButton = new Button("No");
	        yesButton.setMinWidth(70);
	        noButton.setMinWidth(70);

	        //Clicking will set answer and close window
	        yesButton.setOnAction(e -> {
	            answer = true;
	            window.close();
	        });
	        noButton.setOnAction(e -> {
	            answer = false;
	            window.close();
	        });

	        VBox layout = new VBox(15);
	        HBox buttons = new HBox(10);

	        //Add buttons
	        buttons.getChildren().addAll(yesButton, noButton);
	        buttons.setAlignment(Pos.CENTER);
	        layout.getChildren().addAll(label, buttons);
	        layout.setAlignment(Pos.CENTER);
	        Scene scene = new Scene(layout);
	        window.setScene(scene);
	        window.setResizable(false);
	        window.showAndWait();

	        //Make sure to return answer
	        return answer;
	    }

}
