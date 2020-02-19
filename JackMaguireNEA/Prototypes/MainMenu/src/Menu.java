import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Menu extends Application {

    Stage menuStage;

    Scene test1;
    Button test11Btn, test12Btn;
    TextField test1F;
    GridPane t1;

    Scene test2;
    Button test21Btn, test22Btn;
    TextField test2F;
    GridPane t2;


    @Override
    public void start(Stage primaryStage) throws Exception {
        menuStage = primaryStage;
        menuStage.setTitle("Menu TEST");

        t1 = new GridPane();
        t1.setPadding(new Insets(20));
        t1.setVgap(5);
        t1.setHgap(5);
        t2 = new GridPane();
        t2.setPadding(new Insets(20));
        t2.setVgap(5);
        t2.setHgap(5);


        test11Btn = new Button("1");
        GridPane.setConstraints(test11Btn, 0, 0);
        test12Btn = new Button("2");
        GridPane.setConstraints(test11Btn, 0, 1);
        test1F = new TextField();
        GridPane.setConstraints(test1F, 1, 0);
        test1F.setText("TF1");

        test21Btn = new Button("1");
        GridPane.setConstraints(test21Btn, 0, 0);
        test22Btn = new Button("2");
        GridPane.setConstraints(test21Btn, 0, 1);
        test2F = new TextField();
        GridPane.setConstraints(test2F, 1, 0);
        test2F.setText("TF2");

        t1.getChildren().addAll(test11Btn, test12Btn, test1F);
        t2.getChildren().addAll(test21Btn, test22Btn, test2F);


        test1 = new Scene(t1, 400, 400);
        test2 = new Scene(t2, 400, 400);


        menuStage.setScene(test1);
        menuStage.show();


        test21Btn.setOnAction(e -> {
            menuStage.setScene(test1);
            test11Btn.setDisable(true);
            test12Btn.setDisable(false);
            test21Btn.setDisable(true);
            test22Btn.setDisable(false);

        });
        test11Btn.setOnAction(e -> {
            menuStage.setScene(test1);
            test11Btn.setDisable(true);
            test12Btn.setDisable(false);
            test21Btn.setDisable(true);
            test22Btn.setDisable(false);

        });


        test22Btn.setOnAction(e -> {
            menuStage.setScene(test2);
            test11Btn.setDisable(false);
            test12Btn.setDisable(true);
            test21Btn.setDisable(false);
            test22Btn.setDisable(true);

        });
        test12Btn.setOnAction(e -> {
            menuStage.setScene(test2);
            test11Btn.setDisable(false);
            test12Btn.setDisable(true);
            test21Btn.setDisable(false);
            test22Btn.setDisable(true);

        });


    }




    public static void main(String[] args) {
        launch(args);
    }
}


// https://stackoverflow.com/questions/49149502/button-color-change-in-javafx