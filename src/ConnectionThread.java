import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/***
 * ConnectionThread class used to manage a new Thread for each client
 *
 * @author Rémy UM, Osama RAIES HADJ BOUBAKER
 * @author  ENSEA RTS
 */
public class ConnectionThread extends Thread{
    int numClient;
    Socket socket;
    InputStream in;
    State state;

    public ConnectionThread(Socket socket, int numClient, InputStream in){
        super();
        this.numClient = numClient;
        this.socket = socket;
        this.in  = in;
    }

    public enum State {
        CLOSE,
        LISTENING
    }


    private State connectionState(){
        return this.state;
    }

    private void setState(State state){
        this.state = state;
    }

    public String getMSG(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    public void printMSG(String msg) throws UnsupportedEncodingException {
        System.out.println("Client "+ numClient +": " + new String(msg.getBytes(), StandardCharsets.UTF_8));
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
        writer.println("ACK " + address +" : " +clientSocket.getPort());
    }

    public void OpenMSG(){
        System.out.println("Welcome Client " + numClient);
    }

    public void CloseMSG(){
        System.out.print("Close connection to Client " + numClient);
    }



    public void run(){
        try {
            OpenMSG();
            setState(State.LISTENING);
            while (connectionState() == State.LISTENING) {
                String msg = getMSG(in);
                printMSG(msg);
                ackMSG(socket);
                checkCloseMSG(msg);
            }
            TCPMultiServer.reduceNBClient(numClient);
            socket.close();
            CloseMSG();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
