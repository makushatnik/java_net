package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Ageev Evgeny on 24.03.2016.
 */
public class Server {
    private static final int PORT = 7070;
    public static String CLIENT_DIR;
    public static volatile int active = 1;//1 - running, 0 - shutdown
    //private static final ServerSocket server;

    private Server() {
        /*try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Error constructing server\n" + e);
            System.exit(1);
        }*/
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("USAGE: java Server <client_dir>");
            System.exit(1);
        }
        String cdir = args[0];
        if (cdir == null || cdir.equals("")) {
            System.out.println("Client directory can't be empty");
            System.exit(1);
        }
        Path path = Paths.get(cdir);
        if (!Files.exists(path)) {
            System.out.println("Client directory not exists!");
            System.exit(1);
        }
        if (!Files.isDirectory(path)) {
            System.out.println("Client directory can't be a file!");
            System.exit(1);
        }
        CLIENT_DIR = cdir;
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server started");
        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    new ServerOneThread(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }

    public void stopServer() throws RemoteException {
        System.out.println("Terminating server...");
        int count = Thread.activeCount();
        active = 0;
        System.out.println("Count of active threads - " + count);
        Thread terminator = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                System.out.println("Server terminated");
                System.exit(0);
            }
        });
        terminator.start();
    }
}

class ServerOneThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerOneThread(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                true);

        start();
    }

    public void listDir() {
        System.out.println("Prepare dir list...");
        File path = new File(Server.CLIENT_DIR);
        //Path path = Paths.get(Server.CLIENT_DIR);
        String[] list = path.list();
        try {
            //Stream list = Files.list(path);
            Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
            for (String dirItem : list) {
                Path fPath = Paths.get(path + "/" + dirItem);
                if (Files.isDirectory(fPath)) continue;
                long fileSize = Files.size(Paths.get(path + "/" + dirItem));
                out.write(dirItem + "|" + fileSize + "\r\n");
                System.out.println("Added " + dirItem + ", size = " + fileSize);
            }
        } catch (Exception e) {

        }
        out.flush();
        System.out.println("Sending dir list...");
    }

    public void downloadFile(String fname) {
        Path path = Paths.get(Server.CLIENT_DIR + "/" + fname);
        if (!Files.exists(path)) {
            System.out.println("File " + fname + " is not exists!");
            return;
        }

        try (FileInputStream in = new FileInputStream(Server.CLIENT_DIR + "/" + fname)) {
            byte[] buffer = new byte[1000];

            while (in.available() > 0) {
                //String tmp = reader.readLine();
                int count = in.read(buffer);
                out.write(buffer.toString());
                //out.write(buffer, 0, count);
            }
            out.flush();
            System.out.println("File " + fname + " is writen");
        } catch (FileNotFoundException e) {
            System.out.println("File " + fname + " not exists!\n" + e);
        } catch (IOException e) {
            System.err.println("IOException!\n" + e);
        }
    }

    public void run() {
        try {
            while (true) {
                if (!isActive()) out.println("SHUTDOWN");
                String str = in.readLine();
                if (str == null || str.equals("")) continue;
                if (str.equals("END")) break;
                else if (str.equals("LIST")) listDir();
                else if (str.startsWith("DOWNLOAD")) {
                    String filename = "";
                    int pos = str.indexOf("_");
                    if (pos != -1) filename = str.substring(pos+1);
                    downloadFile(filename);
                }
            }
            System.out.println("closing...");
        } catch (IOException e) {
            System.err.println("IOException\n" + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
        }
    }

    private boolean isActive() {
        if (Server.active == 0) return false;
        return true;
    }
}

class DirFilter implements FilenameFilter {
    private Pattern pattern;
    public DirFilter(String regex) {
        pattern = Pattern.compile(regex);
    }
    public boolean accept(File dir, String name) {
        return pattern.matcher(name).matches();
    }
}
