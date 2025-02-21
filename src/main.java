import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        int port = 1234;

        Server server = new Server(port);
        server.start();
    }
}
