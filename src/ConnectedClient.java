import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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
   // private final List<String> usersInRoom;

    ConnectedClient(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
       // usersInRoom = new CopyOnWriteArrayList<>();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void getUserName() throws IOException {
        username = in.readUTF();
    }

    public void setChatRoom() throws IOException {
        //out.writeUTF(Arrays.toString(Server           .roomNums));
        room = in.readInt();
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
                System.out.println("User" + id + " (" + username + ")" + " (" + room + "): " + message);
                sendMessage(connectedClients);
            }
        }
    }

    // this one sends the incoming clients messages to every other client connected to the server
    public void sendMessage(CopyOnWriteArrayList<ConnectedClient> connectedClients) throws IOException {
        List<String> usersInRoom = new ArrayList<>(); // there is a better way to do this but this works for rn
        for (ConnectedClient connectedClient : connectedClients) {
            if (connectedClient.id != id && (connectedClient.room == room && connectedClient.username != null))  {
                DataOutputStream outOthers = new DataOutputStream(connectedClient.socket.getOutputStream());
                if(message.equals("/users")) {
                    usersInRoom.add(connectedClient.username);
                } else if (this.quit) {
                    outOthers.writeUTF(username + " has left the server");
                }
                else {
                    outOthers.writeUTF(username + ": " +  message);
                }
            }
        }
        if (message.equals("/users")) {
            if(!usersInRoom.isEmpty()) {
                out.writeUTF(usersInRoom.toString());
            }
            else {
                out.writeUTF("you are all alone");
            }
        }
    }
    private void close() throws IOException {
        socket.close();
        in.close();
    }
}

