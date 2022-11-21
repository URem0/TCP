import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private State state;
    public int port = 8080;
    byte[] buf;
    int bufSize = 1024;

    public TCPServer() {
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

    private void setPort(int port){
        this.port = port;
    }

    public void printMSG(InputStream msg) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(msg));
        String line = reader.readLine();
        System.out.println(line);
        checkstate(line);
    }
    public void checkstate(String msg){
        if (msg.equals("close")){
            setState(State.CLOSE);
        }
    }

    public void launch() throws IOException {
        setState(State.LISTENING);
        ServerSocket socket = new ServerSocket(this.port);
        Socket clientSocket = socket.accept();

        while (ServerState() == State.LISTENING) {
            InputStream in = clientSocket.getInputStream();
            printMSG(in);
            OutputStream out = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("ACK");
        }
        clientSocket.close();
        socket.close();

    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.launch();
    }
}