import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Button send;
    public ListView<String> yourFiles;
    public ListView<String> serverFiles;
    public TextField text;
    private List<File> clientFileList = new ArrayList<>();
    private List<File> serverFileList = new ArrayList<>();
    public static Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fillYourFiles();
        yourFiles.setOnMouseClicked(a -> {
            if (a.getClickCount() == 2) {
                String fileName = yourFiles.getSelectionModel().getSelectedItem();
                File currentFile = findFileByNameOnClient(fileName);
                if (currentFile != null) {
                    try {
                        File serverFile = findFileByNameOnServer(fileName);
                        if (serverFile != null) {
                            showAlertWindow("File is already uploaded on server");
                            return;
                        }
                        os.writeUTF("./upload");
                        os.writeUTF(fileName);
                        os.writeLong(currentFile.length());
                        FileInputStream fis = new FileInputStream(currentFile);
                        byte[] buffer = new byte[1024];
                        while (fis.available() > 0) {
                            int bytesRead = fis.read(buffer);
                            os.write(buffer, 0, bytesRead);
                        }
                        os.flush();
                        String response = is.readUTF();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fillServerFiles();
                }

            }
        });


        serverFiles.setOnMouseClicked(a -> {
            byte[] buffer = new byte[1024];
            if (a.getClickCount() == 2) {
                String fileName = serverFiles.getSelectionModel().getSelectedItem();
                File currentFile = findFileByNameOnClient(fileName);
                if (currentFile != null) {
                    showAlertWindow("File is already downloaded");
                    return;
                }
                try {
                    os.writeUTF("./download");
                    os.writeUTF(fileName);
                    long fileLength = is.readLong();
                    File file = new File("./client/src/resources/" + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    for (long i = 0; i < (fileLength / 1024 == 0 ? 1 : fileLength / 1024); i++) {
                        int bytesRead = is.read(buffer);
                        fos.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fillYourFiles();
            }
        });
    }

    private void showAlertWindow(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private File findFileByNameOnClient(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    private File findFileByNameOnServer(String fileName) {
        for (File file : serverFileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public void fillYourFiles() {
        yourFiles.getItems().clear();
        String clientPath = "./client/src/resources/";
        File dir = new File(clientPath);
        if (!dir.exists()) {
            throw new RuntimeException("directory resource not exists on client");
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            clientFileList.add(file);
            yourFiles.getItems().add(file.getName());
        }
    }

    public void fillServerFiles() {
        serverFiles.getItems().clear();
        String serverPath = "./server/src/resources/";
        File dir = new File(serverPath);
        if (!dir.exists()) {
            throw new RuntimeException("directory resource does not exists on server");
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            serverFileList.add(file);
            serverFiles.getItems().add(file.getName());
        }
    }

    public void connect(ActionEvent actionEvent) {
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1000);
            fillServerFiles();
        } catch (IOException | InterruptedException e) {
            showAlertWindow("Server not found");
        }
    }
}