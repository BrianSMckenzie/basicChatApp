import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public static void main(String[] args) {
        new Client();
    }

    Client() {
        try{
            socket = new Socket("localhost", 8080);
            new Thread(() -> {
                try {
                    getMessages();
                } catch (IOException e) {
                    System.out.println("DISCONNECTED");
                }
            }).start();
            sendMessages();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    private void sendMessages() throws IOException {
        while(true) {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter: ");
            String message = scanner.nextLine();

            if(message.equals("quit")) {
                out.writeUTF(message);
                disconnect();
            }

            out.writeUTF(message);

        }
    }

    private void getMessages() throws IOException {
        while(true) {
            if (in != null) {
                String incomingMessage = in.readUTF();
                System.out.println(incomingMessage);
                System.out.print("Enter: ");
            }
        }
    }

    private void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}
