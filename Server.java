import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

        //*** Application Protocol *****
        String request = in.readLine();

        String[] requestParts = request.split(" ");
        String path = requestParts[1];
        path = "docroot" + path;
        File file = new File(path);
        if (file.exists()){

            out.println("HTTP/1.1 " + 200 + " " + "OK");
            out.println("Content-Type: " + getContentType(path));
            out.println("Content-Length: " + file.length());
            out.println("Connection: close");
            out.println(); // Blank line to separate headers from body
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                out.println(line);
            }
        }
        else{
            out.println("Not a file");
            return;
        }

        in.close();
        out.close();
    }

    public static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".gif")) return "image/gif";
        return "text/plain"; // Default type
    }
    


    public void run() throws IOException{
        boolean running = true;

        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
