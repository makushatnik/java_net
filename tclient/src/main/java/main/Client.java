package main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

/**
 * Created by Ageev Evgeny on 24.03.2016.
 */
public class Client extends JFrame {
    private JTextField host;
    private JTextField port;
    private JButton testButton;
    private JTextField downloadDir;
    private JTextField mask;
    private JTable table;
    private JButton chooseButton;
    private JButton listButton;
    private JButton downloadButton;
    private FilesTableModel tableModel;

    public static final String PORT = "7070";
    public static final String HOST = "localhost";
    public static final String LIST = "LIST";
    public static final String EXIT = "END";
    public static final String DOWNLOAD = "DOWNLOAD";

    private Download selected;


    public static void main(String[] args) {
        Client client = new Client();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setVisible(true);
    }

    public Client() {
        setTitle("Scanner");
        setSize(680,480);

        createMenu();

        createUI();

        //readSettings();
    }

    public void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExit = new JMenuItem("Exit",KeyEvent.VK_X);
        fileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        JMenuItem aboutItem = new JMenuItem("About",KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aboutPressed();
            }
        });
        fileMenu.add(fileExit);
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public void createUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        //JPanel panel = new JPanel(new FlowLayout());
        JLabel hostLabel = new JLabel("Enter the host:");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(hostLabel, gbc);
        host = new JTextField(15);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(host, gbc);
        host.setText(HOST);
        JLabel portLabel = new JLabel("port:");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(portLabel, gbc);
        port = new JTextField(5);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel.add(port, gbc);
        port.setText(PORT);
        testButton = new JButton("Test connection");
        testButton.addActionListener(e->actionTest());
        gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(testButton, gbc);

        JLabel ddirLabel = new JLabel("Download directory:");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(ddirLabel, gbc);
        downloadDir = new JTextField(15);
        gbc = new GridBagConstraints();
        //gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(downloadDir, gbc);
        downloadDir.setText("");
        chooseButton = new JButton("...");
        chooseButton.addActionListener(e->actionChoose());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.05;
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(chooseButton, gbc);

        JLabel maskLabel = new JLabel("File mask:");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(maskLabel, gbc);
        mask = new JTextField(5);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 4;
        gbc.gridy = 1;
        panel.add(mask, gbc);

        tableModel = new FilesTableModel();
        //table = new JTable();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e->tableSelectionChanged());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProgressRenderer renderer = new ProgressRenderer(0,100);
        renderer.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, renderer);
        table.setRowHeight((int) renderer.getPreferredSize().getHeight());

        JPanel filesPanel = new JPanel();
        filesPanel.setBorder(BorderFactory.createTitledBorder("Files on server"));
        filesPanel.setLayout(new BorderLayout());
        filesPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        listButton = new JButton("Get list");
        listButton.addActionListener(e->actionList());
        buttonsPanel.add(listButton);
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(e->actionDownload());
        buttonsPanel.add(downloadButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(filesPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void tableSelectionChanged() {
        selected = tableModel.getDownload(table.getSelectedRow());
    }

    public void actionExit() {
        String server = host.getText().trim();
        String portStr = port.getText().trim();
        if (!isCorrect(server, portStr)) return;

        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName(server), Integer.parseInt(portStr));
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            if (s.isConnected()) {
                OutputStream out = s.getOutputStream();
                out.write(EXIT.getBytes());
                out.flush();
            }
            s.close();
        } catch (IOException e) {
            try {
                if (s != null) s.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
        }

        System.exit(0);
    }

    public void aboutPressed() {
        final About ab = new About();
        ab.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ab.dispose();
            }
        });
    }

    public void actionChoose() {
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = c.showOpenDialog(Client.this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            downloadDir.setText(c.getSelectedFile().getAbsolutePath());
            //downloadDir.setText(c.getCurrentDirectory().toString());
        }
    }

    public void actionTest() {
        String server = host.getText().trim();
        String portStr = port.getText().trim();
        if (!isCorrect(server, portStr)) return;

        Socket s;
        s = new Socket();
        boolean closed = true;
        try {
            s.connect(new InetSocketAddress(server, Integer.parseInt(portStr)), 20);
            if (s.isConnected()) {
                s.close();
                closed = false;
            }
        } catch (IOException e) {
            try {
                if (s != null) s.close();
            } catch (IOException ex) {
                System.err.println("Socket not closed");
            }
        }
        if (closed) {
            JOptionPane.showMessageDialog(this, "Connection failed", "Failed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Connection success", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actionList() {
        String server = host.getText().trim();
        String portStr = port.getText().trim();
        if (!isCorrect(server, portStr)) return;

        ListThread listThread = new ListThread(server, portStr);
        listThread.start();
    }

    public void actionDownload() {
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a file in a table.", "File not selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String server = host.getText().trim();
        String portStr = port.getText().trim();
        if (!isCorrect(server, portStr)) return;

        String ddir = downloadDir.getText().trim();
        if (ddir.equals("")) {
            JOptionPane.showMessageDialog(this, "Enter the download directory", "Download dir is empty",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File dirFile = new File(ddir);
        if (!dirFile.exists()) {
            JOptionPane.showMessageDialog(this, "Download dir is not exists", "Dir is not exists",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        else if (!dirFile.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Download dir must be a directory", "It's a file",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String fname = selected.getFname();
        if (fname == null || fname.equals("")) return;
        DownloadThread dTh = new DownloadThread(server, portStr, fname, ddir);
        dTh.start();
    }

    public boolean isCorrect(String hostStr, String portStr) {
        if (hostStr.equals("")) {
            JOptionPane.showMessageDialog(this,"Enter the host name","Host name is empty",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if (portStr.equals("")) {
            JOptionPane.showMessageDialog(this,"Enter the port number","Port number is empty",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    private class ListThread extends Thread {
        private String server;
        private String portStr;

        public ListThread(String s, String p) {
            server = s;
            portStr = p;
        }

        public void run() {
            Socket s = null;
            try {
                s = new Socket(InetAddress.getByName(server), Integer.parseInt(portStr));
            } catch (IOException e) {
                System.err.println("Socket failed");
            }
            try {
                if (s.isConnected()) {
                    System.out.println("Connected");
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),
                            true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    out.println(LIST);
                    out.flush();
                    System.out.println("Query received");

                    String tmp = "";
                    tableModel.clearAll();
                    while ((tmp = in.readLine()) != null) {
                        //String tmp = in.readLine();
                        long fSize = 0;
                        int pos = tmp.indexOf("|");
                        if (pos != -1) {
                            try {
                                fSize = Long.parseLong(tmp.substring(pos+1));
                            } catch (Exception e) {}
                            tmp = tmp.substring(0, pos);
                        }
                        System.out.println("file = " + tmp + ", size = " + fSize);
                        Download dl = new Download(tmp);
                        dl.setSize(fSize);
                        tableModel.addDownload(dl);
                    }
                    System.out.println("List of files loaded");
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (s != null) s.close();
                } catch (IOException ex) {
                    System.err.println("Socket not closed");
                }
            }
        }
    }

    private class DownloadThread extends Thread {
        private String server;
        private String portStr;
        private String fname;
        private String ddir;

        public DownloadThread(String s, String p, String f, String d) {
            server = s;
            portStr = p;
            fname = f;
            ddir = d;
        }

        public void run() {
            FileOutputStream out2 = null;
            try (Socket s = new Socket(InetAddress.getByName(server), Integer.parseInt(portStr))) {
                if (s.isConnected()) {
                    //s.close();
                    OutputStream out = s.getOutputStream();
                    out.write((DOWNLOAD + "_" + fname).getBytes());
                    out.flush();

                    out2 = new FileOutputStream(ddir + "/" + fname);
                    //create
                    Path path = Paths.get(ddir + "/" + fname);
                    if (Files.exists(path)) {
                        System.out.println("File " + fname + " exists. Will be rewrited");
                    } else {
                        Files.createFile(path);
                    }
                    System.out.println("File " + fname + " created.");
                    //load
                    //String tmp = "";
                    byte[] buffer = new byte[1000];
                    InputStream in = s.getInputStream();
                    while (in.available() > 0) {
                        //tmp += (char)in.read();
                        int cnt = in.read(buffer, 0, 1000);
                        out2.write(buffer, 0, cnt);
                    }
                    out2.flush();
                    System.out.println("File " + fname + " loaded.");
                    /*StringTokenizer token = new StringTokenizer(tmp, "\r\n");
                    while (token.hasMoreTokens()) {
                        //table.addRow()
                    }*/
                }
            } catch (IOException e) {
                System.out.println("IOException\n" + e.getMessage());
            } finally {
                try {
                    if (out2 != null) out2.close();
                } catch (IOException e) {
                    System.err.println("Can't close stream.");
                }
            }
        }
    }
}
