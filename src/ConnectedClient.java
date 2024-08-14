import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectedClient {

    private final Socket socket;
    public int id;
    private DataInputStream in;
    private DataOutputStream out;
    public boolean quit;
    private String message;
    public String username;

    ConnectedClient(Socket socket, int id)  {
        this.socket = socket;
        this.id = id;
    }

    public void getUserName() throws IOException {
        in = new DataInputStream(socket.getInputStream());
        username = in.readUTF();
    }


    // this method sends client messages to the server itself
    public void getMessages(CopyOnWriteArrayList<ConnectedClient> connectedClients) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        while (!quit) {

            message = in.readUTF();

            if(message.equals("quit")) {
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
            if (connectedClient.id != id) {

                out = new DataOutputStream(connectedClient.socket.getOutputStream());

                if(this.quit)
                    out.writeUTF("\n" + username + " has left the server");
                else
                    out.writeUTF("\n" + username + ": " + message);
            }
        }
    }

    private void close() throws IOException {
        socket.close();
        in.close();
    }


}

