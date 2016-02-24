import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;


/**
 *
 */
public class Window extends Application {

    private final TextArea infoArea = new TextArea();
    private final TextArea realTimeArea = new TextArea();

    protected void appendToInfoArea(String txt) {
        Platform.runLater(() -> infoArea.appendText("\n"+txt));
    }

    protected void replaceRealTimeArea(final String txt) {
        Platform.runLater(() -> realTimeArea.setText(txt));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new Log(this);

        primaryStage.setTitle("ffmpeg converter to iPad Mini 2");
        VBox root = new VBox();

        infoArea.setWrapText(true);
        infoArea.setMinHeight(400);
        realTimeArea.setWrapText(true);

        root.getChildren().addAll(infoArea, realTimeArea);

        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();

        infoArea.appendText("important log events will show here");
        realTimeArea.appendText("real time ffmpeg info will show here");

        try {
            Task task = new Converter();
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
