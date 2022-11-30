import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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

    public void OpenMSG(){
        System.out.println("Hello Client: " + numClient);
    }

    public void CloseMSG(){
        System.out.println("GoodBye Client: " + numClient);
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
            socket.close();
            CloseMSG();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
