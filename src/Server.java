import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private ServerSocket server;
    public CopyOnWriteArrayList<ConnectedClient> connectedClients = new CopyOnWriteArrayList<>();
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

    @SuppressWarnings("InfiniteLoopStatement")
    private void acceptConnection() throws IOException {
        while(true) {
            socket = server.accept();
            new Thread(() -> {
                try {
                    ConnectedClient client = new ConnectedClient(socket, connectedClients.size());
                    connectedClients.add(client);

                    System.out.println("user" + client.id + " has joined the server");

                    // this method loops until client quits server
                            client.getMessages(connectedClients);

                    // remove client from connected list if they quit
                    connectedClients.remove(client);

                    // checks if last removed user was last one on server
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
