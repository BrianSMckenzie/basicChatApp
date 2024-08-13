import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectedClient {

    private final Socket socket;
    public int id;
    private DataInputStream in;
    private DataOutputStream out;
    public boolean closed;
    private String message;

    ConnectedClient(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public void getMessages(ArrayList<ConnectedClient> connectedClients) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        while (!closed) {

            message = in.readUTF();

            if(message.equals("quit")) {
                System.out.println("User" + id + ": DISCONNECTED");
                close();
            }
            else {
                System.out.println("User" + id + ": " + message);
                sendMessage(connectedClients);
            }
        }
    }

    public void sendMessage(ArrayList<ConnectedClient> connectedClients) throws IOException {
        for(ConnectedClient connectedClient : connectedClients) {
            if(connectedClient.id != id){
                out = new DataOutputStream(connectedClient.socket.getOutputStream());
                out.writeUTF("\nUser" +id + ": " + message);
            }
        }

    }

    private void close() throws IOException {
        socket.close();
        in.close();
        closed = true;
    }


}

