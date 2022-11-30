import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer {
    int NbClient;
    public void run() throws IOException {
        ServerSocket socket = new ServerSocket(8080);

        while(true){
            Socket clientSocket = socket.accept();
            NbClient++;
            System.out.println("Number of client: " + NbClient);
            InputStream in = clientSocket.getInputStream();
            ConnectionThread connectionThread = new ConnectionThread(clientSocket, NbClient, in);
            connectionThread.run();
        }
    }

    public static void main(String[] args) throws IOException {
        TCPMultiServer tcpMultiServer = new TCPMultiServer();
        tcpMultiServer.run();
    }
}
