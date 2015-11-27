package com.creativedes.net;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class About extends JFrame {
	
	public About() {
		setTitle("About");
		setSize(240,240);
		
		JPanel panel = new JPanel();
		//GridBagLayout gbag = new GridBagLayout();
		JLabel author = new JLabel("Author: Ageev Evgeny");
		author.setForeground(Color.BLUE);
		GridBagConstraints gbc = new GridBagConstraints();
		//gbc.anchor = GridBagConstraints.SOUTH;
		//gbc.insets = new Insets(5,5,5,5);
		//gbag.setConstraints(author, gbc);
		//panel.add(author, gbc);
		panel.add(author, BorderLayout.CENTER);
		JLabel email = new JLabel("email: makushatnik@yandex.ru");
		email.setForeground(Color.BLUE);
		//gbc.anchor = GridBagConstraints.SOUTH;
		//gbc.insets = new Insets(5,5,5,5);
		//gbag.setConstraints(email, gbc);
		//panel.add(email, gbc);
		panel.add(email, BorderLayout.CENTER);
		//getContentPane().setLayout(new BorderLayout());
		//getContentPane().add(panel, BorderLayout.CENTER);
		JPanel blancP = new JPanel();
		JLabel blancL = new JLabel();
		gbc.weightx = 2;
		blancP.add(blancL, gbc);
		add(blancP, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		About ab = new About();
		ab.show();
	}

}
