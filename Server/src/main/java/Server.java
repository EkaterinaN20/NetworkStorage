import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    ExecutorService executor;
    Socket socket;

    public Server() {
        start();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(8189)) {
            //server.setSoTimeout(2000);
            executor = Executors.newFixedThreadPool(4);
            while (!isInterrupted()) {
                socket = server.accept();
                System.out.println("socket accepted");
                executor.execute(new ConnectionHandler(socket));
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}