import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    @FXML
    public TextArea log;
    Server server;

    public void start(ActionEvent actionEvent) {
        if (server != null && server.isAlive()) {
            log.appendText("Already running" + "\n");
        } else {
            server = new Server();
            log.appendText("Server started" + "\n");
        }
    }

    public void stop(ActionEvent actionEvent) {
        stopServer();
    }

    public void stopServer() {
        if (server == null || !server.isAlive()) {
            log.appendText("Nothing to stop" + "\n");
        } else {
            server.interrupt();
            log.appendText("Server stopped" + "\n");
        }
    }
}