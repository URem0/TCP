import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    private State clientState(){
        return this.state;
    }

    private void setDST(String dst){
        this.dst = dst;
    }

    private void setPortDST(int portDST){
        this.portDST = portDST;
    }

    public TCPClient() throws Exception {
        this.socket = new Socket(dst, portDST);
    }

    public String clientMSG(){
        System.out.println("Enter your message: ");
        Console c = System.console();
        String msg = c.readLine();
        return msg;
    }

    public void checkCloseMSG(String msg){
        if (msg.equals("close")){
            setState(State.CLOSE);
        }
    }

    public void sendMSG(String msg) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println(new String(msg.getBytes(), "utf8"));
    }

    public void receiveACK()throws IOException {
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        System.out.println(line);
    }

    public void launch() throws Exception {
        openMSG();
        setState(State.RUNNING);

        while (clientState() == State.RUNNING) {
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
            client.setDST(args[0]);
            client.setPortDST(Integer.parseInt(args[1]));
        }

        client.launch();
    }
}
