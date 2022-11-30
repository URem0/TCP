import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    public String getMSG(Socket clientSocket) throws IOException {
        InputStream in = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        return line;
    }

    public void printMSG(String msg) throws UnsupportedEncodingException {
        System.out.println(new String(msg.getBytes(), "utf8"));
    }

    public void checkCloseMSG(String msg){
        if (msg.equals("close")){
            setState(State.CLOSE);
        }
    }

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