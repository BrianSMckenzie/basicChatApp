import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private ServerSocket server;
    public ArrayList<ConnectedClient> connectedClients = new ArrayList<>();
    private Socket socket;

    public static void main(String[] args) {
       new Server();
    }

    Server(){
        try{
            server = new ServerSocket(8080);
            acceptConnection();

        }catch(IOException e){
            System.out.println("error: " + e.getMessage());
        }
    }

    private void acceptConnection() throws IOException {
        while(true) {
            socket = server.accept();
            new Thread(() -> {
                try {
                    System.out.println("accepted connection");
                    ConnectedClient client = new ConnectedClient(socket, connectedClients.size());
                    connectedClients.add(client);
                    client.getMessages(connectedClients);
                    connectedClients.remove(client);
                    checkUsers();
                }   catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private void checkUsers() throws IOException {
        if (connectedClients.isEmpty()) {
            System.out.println("no clients connected");
            Scanner scanner = new Scanner(System.in);
                System.out.print("Enter 1 to close the server: ");
            int choice = scanner.nextInt();
            if (choice == 1) {
                server.close();
                scanner.close();
                System.exit(1);
            }
        }
    }
}
