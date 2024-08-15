import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public static void main(String[] args) {
        new Client();
    }

    Client() {
        try{
            socket = new Socket("localhost", 8080);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);
            setUsername();
            chooseChatRoom();
            new Thread(() -> {
                try {
                    getMessages();
                } catch (IOException e) {
                    System.out.println("DISCONNECTED");
                }
            }).start();
            sendMessages();
        }catch(IOException e){
            System.out.println("error: " + e.getMessage());
        }
    }

    private void chooseChatRoom() throws IOException {
        while(true){
            System.out.print("Choose a chat room ");
            System.out.println(in.readUTF());
            int temp = scanner.nextInt();
            System.out.println("Do you want to join room" + temp + " (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                out.writeInt(temp);
                break;
            }
        }
    }

    private void setUsername() throws IOException {
        while(true) {
            System.out.print("Please enter your username: ");
            String temp = scanner.nextLine();
            System.out.print("Do you want " + temp + " as your username? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                out.writeUTF(temp);
                break;
            }
        }
    }

    // sends messages to the server
    @SuppressWarnings("InfiniteLoopStatement")
    private void sendMessages() throws IOException {
        while(true) {

            String message = scanner.nextLine();

            if(message.equals("quit")) {
                out.writeUTF(message);
                disconnect();
            }

            out.writeUTF(message);

        }
    }


    // prints messages from other users in the server
    @SuppressWarnings("InfiniteLoopStatement")
    private void getMessages() throws IOException {
        while(true) {
            if (in != null) {
                String incomingMessage = in.readUTF();
                System.out.println(incomingMessage);
            }
        }
    }

    private void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}
