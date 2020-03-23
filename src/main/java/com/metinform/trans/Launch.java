package com.metinform.trans;

import com.metinform.trans.support.ConstantSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author chieftain
 * @date 2020/3/20 17:02
 */
public class Launch extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ConstantSet.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("AnyToDocx");
        primaryStage.setScene(new Scene(root, 600, 440));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
