package com.metinform.trans.console;

import com.metinform.trans.support.ConstantSet;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console {
    private TextArea console;

    public Console(TextArea console) {
        this.console = console;
    }

    public void appendText(String valueOf, boolean error) {
        synchronized (ConstantSet.CONSOLEAPPENDLOCK){
            if(error) {
                console.setStyle("-fx-text-fill: red");
            }
            Platform.runLater(() -> console.appendText(valueOf.concat("\r\n")));
        }
    }

    public void appendText(String valueOf) {
        this.appendText(valueOf, false);
    }

    public void appendError(String valueOf) {
        this.appendText(valueOf, true);
    }

    public void appendInfo(String valueOf) {
        this.appendText(valueOf, false);
    }
}