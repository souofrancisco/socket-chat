import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> chs = new CopyOnWriteArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickname;
    private volatile boolean isClosed = false;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.nickname = in.readLine(); //sempre que um cliente entrar lê o seu nickname;
            chs.add(this);
            broadcastMessage("SERVER: " + nickname + " has connected to the server!");
        } catch (IOException e) {
            closeServer(socket, in, out);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        sendMessage();
        while (socket.isConnected()) {
            try {
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeServer(socket, in, out);
            }
        }
    }

    public void sendMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Brodcast a message from the server: ");
                    String message = scanner.nextLine();
                    for (ClientHandler ch : chs) {
                        ch.out.write("SERVER: " + message);
                        ch.out.newLine();
                        ch.out.flush();
                    }
                } catch (IOException e) {
                    closeServer(socket, in, out);
                }
            }
        }).start();
    }

    public void broadcastMessage(String messageToClient) {
        for (ClientHandler ch : chs) {
            try {
                if (!ch.nickname.equals(nickname) ) {
                    ch.out.write(messageToClient);
                    ch.out.newLine();
                    ch.out.flush();
                }
            } catch (IOException e) {
                closeServer(socket, in, out);
            }
        }
    }

    public void removeClientHandler() {
        chs.removeIf(ch -> ch == this);
        broadcastMessage("SERVER:" + nickname + " desconnected from the server!");
    }

    public void closeServer(Socket socket, BufferedReader in, BufferedWriter out) {
        if (isClosed) return; // Se já foi fechado, não faz nada
        isClosed = true;

        removeClientHandler();
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
