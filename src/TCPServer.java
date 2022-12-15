import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/***
 * TCP Server Class on port 8080 by default.
 * Specify as argument a port number to set a new port
 *
 * @author Rémy UM, Osama RAIES HADJ BOUBAKER
 * @author  ENSEA RTS
 */
public class TCPServer {
    private State state;
    public int port = 8080;
    ServerSocket socket;

    public TCPServer() throws IOException {
        this.socket = new ServerSocket(this.port);
    }

    public enum State {
        CLOSE,
        LISTENING
    }

    public void OpenMSG(){
        System.out.println("-------Open Server---------");
    }

    public void CloseMSG(){
        System.out.println("-------Close Server---------");
    }

    private State ServerState(){
        return this.state;
    }

    private void setState(State state){
        this.state = state;
    }

    /***
     * Receives the packet from a client
     * @param clientSocket socket of the server
     * @return the client message as a String
     * @throws IOException
     */
    public String getMSG(Socket clientSocket) throws IOException {
        InputStream in = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    /***
     * Prints the client message into the console
     * @param msg client's message
     */
    public void printMSG(String msg) {
        System.out.println(new String(msg.getBytes(), StandardCharsets.UTF_8));
    }

    /***
     * Checks if the server receive a close message
     * @param msg
     */
    public void checkCloseMSG(String msg){
        if (msg.equals("close")){
            setState(State.CLOSE);
        }
    }

    /***
     * Sends an acknowledgment message to the client
     * @param clientSocket the client's socket
     * @throws IOException
     */
    public void ackMSG(Socket clientSocket) throws IOException {
        InetAddress clientAddress = clientSocket.getInetAddress();
        String address = clientAddress.getHostAddress();
        OutputStream out = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);
        writer.println("ACK " + address);
    }

    public void launch() throws IOException {
        OpenMSG();
        setState(State.LISTENING);
        ServerSocket socket = new ServerSocket(this.port);
        Socket clientSocket = socket.accept();

        while (ServerState() == State.LISTENING) {
            String msg = getMSG(clientSocket);
            printMSG(msg);
            ackMSG(clientSocket);
            checkCloseMSG(msg);
        }

        CloseMSG();
        clientSocket.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.launch();
    }
}