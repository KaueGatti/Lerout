package my.company.projetorotisseriejavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.company.projetorotisseriejavafx.Controller.InicioController;
import my.company.projetorotisseriejavafx.DB.DatabaseConnection;
import my.company.projetorotisseriejavafx.Util.IconHelper;

import java.io.IOException;
import java.io.PrintStream;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + "Inicio.fxml"));
            Parent root = loader.load();

            scene = new Scene(root);

            scene.getStylesheets().addAll(
                    getClass().getResource("/styles/main.css").toExternalForm()
            );

            root.applyCss();

            IconHelper.applyIconsTo(root);

            stage.setScene(scene);

            InicioController controller = loader.getController();

            controller.initialize(stage);

            stage.setMaximized(true);

            stage.setResizable(false);

            stage.show();

        } catch (Exception e) {
            System.out.println("Erro start: " + e);
            e.printStackTrace();
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml));
        return fxmlLoader.load();
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        launch(args);
    }

}