import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private volatile ServerSocket server;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        this.server = new ServerSocket(port);
        System.out.println("Server started at port " + port);
        while (!server.isClosed()) {
            Socket socket = server.accept();
            System.out.println("New connection from " + socket.getRemoteSocketAddress());
            ClientHandler ch = new ClientHandler(socket);
            Thread ClientThread = new Thread(ch);
            ClientThread.start();
        }
    }

    public void stop() throws IOException {
        if (server != null) {
            server.close();
        }
    }
}
