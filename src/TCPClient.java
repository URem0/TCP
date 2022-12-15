import java.io.*;
import java.net.Socket;

/***
 * TCP Client Class connected to localhost, port 8080 by default.
 * Specify as arguments address port to connect to a TCP server on address:port
 *
 * @author Rémy UM, Osama RAIES HADJ BOUBAKER
 * @author  ENSEA RTS
 */
public class TCPClient {
    private static final int portCLIENT = 5490 ;
    private int portDST = 8080;
    private State state;
    private Socket socket;
    private String dst = "localhost";

    public enum State {
        CLOSE,
        RUNNING
    }

    public void openMSG(){
        System.out.println("-------Open Client---------");
    }

    public void closeMSG(){
        System.out.println("-------Close Client---------");
    }

    private void setState(State state){
        this.state = state;
    }

    private State getClientState(){
        return this.state;
    }

    private void setAddressDST(String dst){
        this.dst = dst;
    }

    private void setPortDST(int portDST){
        this.portDST = portDST;
    }

    public TCPClient() throws Exception {
        this.socket = new Socket(dst, portDST);
    }

    /***
     * Collects the input message of the client from the console
     * @return msg the client's msg
     */
    public String clientMSG(){
        System.out.println("Enter your message: ");
        Console c = System.console();
        String msg = c.readLine();
        return msg;
    }

    /***
     * Checks if the client's command is "close" and then close the clientSocket
     * @param msg the client input msg
     */
    public void checkCloseMSG(String msg){
        if (msg.equals("close")){
            setState(State.CLOSE);
        }
    }

    /***
     * Takes the input message and sends it to the socket
     * @param msg the client input msg
     * @throws IOException
     */
    public void sendMSG(String msg) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println(new String(msg.getBytes(), "utf8"));
    }

    /***
     * From the socket receive the acknowledgment message "ACK"
     * @throws IOException
     */
    public void receiveACK()throws IOException {
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        System.out.println(line);
    }

    /***
     * Launch the TCP client
     * @throws Exception
     */
    public void launch() throws Exception {
        openMSG();
        setState(State.RUNNING);

        while (getClientState() == State.RUNNING) {
            String msg = clientMSG();
            sendMSG(msg);
            receiveACK();
            checkCloseMSG(msg);
        }

        socket.close();
        closeMSG();
    }

    public static void main(String[] args) throws Exception {
        TCPClient client = new TCPClient();

        if (args.length != 0 ){
            client.setAddressDST(args[0]);
            client.setPortDST(Integer.parseInt(args[1]));
        }

        client.launch();
    }
}
