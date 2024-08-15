import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectedClient {

    public boolean quit;
    public String username;
    public int room;
    public int id;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String message;
    private List<String> usersInRoom;

    ConnectedClient(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        usersInRoom = new CopyOnWriteArrayList<>();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void getUserName() throws IOException {
       // out.writeUTF(Arrays.toString(Server.roomNums));
        username = in.readUTF();
    }

    public void setChatRoom() throws IOException {
        //out.writeUTF(Arrays.toString(Server.roomNums));
        int room = in.readInt();
        if(room > Server.roomNums.length || room < 1) {
            quit = true;
            System.out.println("Invalid room fuck off");
            close();
        }
    }

    // this method sends client messages to the server itself
    public void getMessages(CopyOnWriteArrayList<ConnectedClient> connectedClients) throws IOException {
        while (!quit) {

            message = in.readUTF();

            if(message.equals("/quit")) {
                System.out.println("User" + id + " (" + username + ")" + " HAS DISCONNECTED");
                quit = true;
                sendMessage(connectedClients);
                this.close();
            }
            else {
                System.out.println("User" + id + " (" + username + "): " + message);
                sendMessage(connectedClients);
            }
        }
    }

    // this one sends the incoming clients messages to every other client connected to the server
    public void sendMessage(CopyOnWriteArrayList<ConnectedClient> connectedClients) throws IOException {
        for (ConnectedClient connectedClient : connectedClients) {
            if (connectedClient.id != id && connectedClient.room == this.room && connectedClient.username != null)  {
                DataOutputStream outOthers = new DataOutputStream(connectedClient.socket.getOutputStream());
                if(message.equals("/users")) {
                    this.usersInRoom.add(connectedClient.username);
                } else if (this.quit) {
                    outOthers.writeUTF("\n" + username + " has left the server");
                }
                else {
                    outOthers.writeUTF("\n" + username + ": " +  message);
                }
            }
        }
        if (message.equals("/users")) {
            out.writeUTF(this.usersInRoom.toString());
        }
    }
    private void close() throws IOException {
        socket.close();
        in.close();
    }
}

