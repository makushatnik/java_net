package com.creativedes.net;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Scanner extends JFrame {

	private JTextArea text;
	private JTextField host;
	private JButton scanTButton;
	private JButton scanUButton;
	private JButton whoisButton;
	private JButton clearButton;
	
	private static final int TCP = 0;
	private static final int UDP = 1;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner();
		scan.show();
	}
	
	public Scanner()
	{
		setTitle("Scanner");
		setSize(640,480);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});
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
		JMenuItem fileSave = new JMenuItem("Save..");
		fileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});
		JMenuItem aboutItem = new JMenuItem("About",KeyEvent.VK_A);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutPressed();
			}
		});
		fileMenu.add(fileExit);
		fileMenu.add(fileSave);
		helpMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
		
		JPanel panel = new JPanel();
		JLabel hostLabel = new JLabel("Enter the host:");
		panel.add(hostLabel);
		host = new JTextField(15);
		panel.add(host);
		scanTButton = new JButton("Scan TCP");
		scanTButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionScan(TCP);
			}
		});
		panel.add(scanTButton);
		scanUButton = new JButton("Scan UDP");
		scanUButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionScan(UDP);
			}
		});
		panel.add(scanUButton);
		whoisButton = new JButton("Whois");
		whoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionWhois();
			}
		});
		panel.add(whoisButton);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClear();
			}
		});
		panel.add(clearButton);
		text = new JTextArea(25,50);
		text.setBackground(Color.WHITE);
		text.setMargin(new Insets(3,5,0,0));
		text.setEditable(false);
		JScrollPane scroller = new JScrollPane(text);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(scroller, BorderLayout.CENTER);
		//getContentPane().add(sost, BorderLayout.SOUTH);
	}
	
	public void actionExit()
	{
		System.exit(0);
	}
	
	public void actionScan(int type)
	{
		String server = host.getText().trim();
		if (server.length() == 0) return;
		
		/*InetAddress address;
		boolean found = false;
		try {
			address = InetAddress.getByName(server);
			found = true;
		} catch (UnknownHostException e) {
			
		}
		byte buf[] = server.getBytes();
		try {
			address = InetAddress.getByAddress(buf);
			found = true;
		} catch (UnknownHostException e) {
			
		}
		
		if (!found)
		{
			//text.setFont();
			text.setText("Host not found!\nTry to enter host name again\n");
			return;
		}*/
		String str = text.getText();
		str += "Scanning " + (type == 0 ? "TCP" : "UDP") + " on " + server + " started\n";
		text.setText(str);
		Socket s;
		for (int i = 0; i < 1024; i++)
		{
			s = new Socket();
			try {
				s.connect(new InetSocketAddress(server, i), 100);
				if (s.isConnected()) {
					s.close();
					str += "Открыт порт: " + i + "\n";
					text.setText(str);
				}
			} catch (Exception e) {
				//str += "Error:\n" + e.toString() + "\n";
				//text.setText(str);
			}
		}
		str += "\n----------------------------------------\n";
		text.setText(str);
	}
	
	public void actionWhois()
	{
		String server = host.getText().trim();
		if (server.length() == 0) return;
		server = server + "\r\n";
		String str = text.getText();
		try {
			Socket s = new Socket("whois.iana.org", 43);
			InputStream in = (InputStream) s.getInputStream();
			OutputStream out = (OutputStream) s.getOutputStream();
			byte buf[] = server.getBytes();
			out.write(buf);
			int c;
			String answer = "";
			while ((c = in.read()) != -1)
				answer += (char)c;
			s.close();
			
			str += answer;
			text.setText(str);
		} catch (IOException e) {
			str += "Error:\n" + e.toString();
			text.setText(str);
		}
		str += "\n----------------------------------------\n";
		text.setText(str);
	}
	
	public void actionClear()
	{
		text.setText("");
	}
	
	public void actionSave()
	{
		File file;
		JFileChooser fd = new JFileChooser(new File("."));
		fd.setDialogTitle("Save text as..");
		int act = fd.showSaveDialog(this);
		if (act != JFileChooser.APPROVE_OPTION) return;
		
		file = fd.getSelectedFile();
		if (file.exists()) {
			act = JOptionPane.showConfirmDialog(this, "File exists. Rewrite?");
			if (act != JOptionPane.YES_OPTION) return;
		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			String str = text.getText();
			pw.print(str);
			if (pw.checkError()) throw new IOException("Error while try to write file");
			pw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error occured:\n" + e.toString(),
					"Write file error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void aboutPressed() {
		About ab = new About();
		ab.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ab.dispose();
			}
		});
	}
}

