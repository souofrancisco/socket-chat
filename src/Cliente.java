import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickname;

    public Cliente(Socket socket, String nickname) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.nickname = nickname;
        } catch (IOException e) {
            closeClient(socket, in, out);
        }
    }

    public void sendMessage() {
        try {
            out.write(nickname);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                out.write(nickname + ": " + messageToSend);
                out.newLine();
                out.flush();

            }
        } catch (IOException e) {
            closeClient(socket, in, out);
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageToReceive = in.readLine();
                    System.out.println(messageToReceive);
                } catch (IOException e) {
                    closeClient(socket, in, out);
                }
            }
        }).start();
    }

    public void closeClient(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (socket != null) {
                socket.close();
            }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o seu nickname: ");
        String nickname = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Cliente cliente = new Cliente(socket, nickname);
        cliente.listenForMessages();
        cliente.sendMessage();


    }
}
