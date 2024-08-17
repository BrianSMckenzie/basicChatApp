import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    public static int[] roomNums = {1,2,3,4,5};
    public ConcurrentHashMap<Integer, Set<Integer>> rooms = new ConcurrentHashMap<>();
    // implement this eventually
    public CopyOnWriteArrayList<ConnectedClient> connectedClients = new CopyOnWriteArrayList<>();

    private ServerSocket server;
    private Socket socket;

    public static void main(String[] args) {
       new Server();
    }

    Server(){
        try{        
            server = new ServerSocket(1234);
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
                    System.out.println("Client connected: " + client.id);
                    connectedClients.add(client);

                    client.getUserName();

                    // user choose chat room (need to add way for user to see how many users connected to each room.)
                    client.setChatRoom();

                    System.out.println("user" + client.id + " (" + client.username + ")"+ " (" + client.room + ")" + " has joined the server");

                    // this method loops until client quits server
                    client.getMessages(connectedClients);

                    // remove client from connected list if they quit
                    connectedClients.remove(client);

                    // checks if last removed user was last one on server
                    checkUsers();
    
                }   catch (IOException e) {
                    System.out.println("error: " + e.getMessage());
                    System.out.println("accept connection shit the bed or something idk (most likely client closed without proper quit)");
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
