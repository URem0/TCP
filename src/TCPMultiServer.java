import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPMultiServer {
    static int nbClient;
    static ConnectionThread connectionThread;
    static HashMap<Integer, Boolean> connectedList = new HashMap<Integer, Boolean>();


    public void OpenMSG(){
        System.out.println("-------Open Server---------");
    }

    public void CloseMSG(){
        System.out.println("-------Close Server---------");
    }

    public void increaseNBClient(){
        nbClient++;
    }

    public static void reduceNBClient(int numClient){
        connectedList.put(numClient,false);
        nbClient--;
    }

    public void addClientConnectedList(int num){
        if (!connectedList.containsKey(num)){
            connectedList.put(num, false);
        }
    }

    private static int searchFirstAvailable() {
        for (Map.Entry<Integer, Boolean> entry : connectedList.entrySet()){
            Boolean isTaken = entry.getValue();
            if (isTaken == false){
                return entry.getKey();
            }
        }
        return 0;
    }

    public void run() throws IOException {
        ServerSocket socket = new ServerSocket(8080);
        OpenMSG();
        while(true){
            Socket clientSocket = socket.accept();
            increaseNBClient();
            System.out.println("Number of clients: " + nbClient);

            addClientConnectedList(nbClient);

            InputStream in = clientSocket.getInputStream();


            int numberAvailable = searchFirstAvailable();
            connectionThread = new ConnectionThread(clientSocket,numberAvailable ,in);
            connectionThread.start();
            connectedList.put(numberAvailable,true);
        }
    }



    public static void main(String[] args) throws IOException {
        TCPMultiServer tcpMultiServer = new TCPMultiServer();
        tcpMultiServer.run();
    }
}