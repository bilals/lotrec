package lotrec.guifx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lotrec.Lotrec;

import java.io.InputStream;

public class LauncherFX extends Application {

    private Stage splashStage;
    private MainFrameFX mainFrame;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Show splash screen
        showSplash();

        // Initialize in background
        Thread initThread = new Thread(() -> {
            Lotrec.initialize(Lotrec.GUI_RUN_MODE);

            Platform.runLater(() -> {
                mainFrame = new MainFrameFX(primaryStage);
                primaryStage.show();
                if (splashStage != null) {
                    splashStage.close();
                }
            });
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    private void showSplash() {
        splashStage = new Stage(StageStyle.UNDECORATED);

        VBox splashLayout = new VBox(10);
        splashLayout.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #336699; -fx-border-width: 2;");

        // Try to load splash image
        try {
            InputStream is = getClass().getResourceAsStream("/lotrec/resources/lotrec.PNG");
            if (is != null) {
                ImageView imageView = new ImageView(new Image(is));
                imageView.setFitWidth(400);
                imageView.setPreserveRatio(true);
                splashLayout.getChildren().add(imageView);
            }
        } catch (Exception e) {
            // Image not found - continue without it
        }

        Label titleLabel = new Label("LoTREC — Tableaux Theorem Prover");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #336699;");
        splashLayout.getChildren().add(titleLabel);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
        splashLayout.getChildren().add(loadingLabel);

        Scene splashScene = new Scene(splashLayout);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();
    }
}
