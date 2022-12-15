import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/***
 * TCP MultiServer Class on port 8080 by default.
 * Specify as argument a port number to set a new port.
 * This Server accepts multiple clients using multi-threading
 *
 * @author Rémy UM, Osama RAIES HADJ BOUBAKER
 * @author  ENSEA RTS
 */
public class TCPMultiServer {
    static int nbClient;
    static ConnectionThread connectionThread;
    static HashMap<Integer, Boolean> connectedList = new HashMap<Integer, Boolean>();
    private State state;

    public enum State {
        LISTENING
    }

    private State ServerState(){
        return this.state;
    }

    private void setState(State state){
        this.state = state;
    }

    public void OpenMSG(){
        System.out.println("-------Open Server---------");
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

    /***
     * Search for the first already registered client number available
     *
     *
     * @return the number of the first already registered client number available
     */
    private static int searchFirstAvailable() {
        for (Map.Entry<Integer, Boolean> entry : connectedList.entrySet()){
            Boolean isTaken = entry.getValue();
            if (!isTaken){
                return entry.getKey();
            }
        }
        return 0;
    }

    /***
     * For each new client a Thread is created
     * The number of client is increased and the new client is added to the Client Connected List
     * @throws IOException
     */
    public void run() throws IOException {
        ServerSocket socket = new ServerSocket(8080);
        OpenMSG();
        setState(State.LISTENING);
        while(ServerState() == State.LISTENING){
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