
package co.edu.eafit.dis.gui;

import java.awt.event.ActionEvent;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.Font;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RegisterGUI extends JFrame {
    
    int width = 240, height = 350;
    
    JLabel labelRegister, labelName, labelUser, labelPass;
    
    JTextField textName, textUser;
    
    JPasswordField textPass;
    
    JButton butEnter, butExit;
    
    Connection connection;
    
    public RegisterGUI() {
        createUserGUI();
    }
    
    private void createUserGUI() {
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(2);
        this.getContentPane().setBackground(Color.WHITE);
        
        labelRegister = new JLabel("REGISTRATION");
        labelRegister.setHorizontalAlignment(JLabel.CENTER);
        labelRegister.setFont(new Font("Monospaced", Font.PLAIN, 18));
        labelRegister.setBounds(0, 25, 240, 20);
        
        labelName = new JLabel("Y O U R  N A M E");
        labelName.setHorizontalAlignment(JLabel.CENTER);
        labelName.setFont(new Font("Monospaced", Font.PLAIN, 13));
        labelName.setBounds(0, 60, 240, 20);
        
        textName = new JTextField();
        textName.setHorizontalAlignment(JTextField.CENTER);
        textName.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textName.setBorder(InitGameGUI.emptyBorder);
        textName.setBounds(30, 90, 180, 30);
         
        labelUser = new JLabel("U S E R N A M E");
        labelUser.setHorizontalAlignment(JLabel.CENTER);
        labelUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelUser.setBounds(0, 130, 240, 20);
        
        textUser = new JTextField();
        textUser.setHorizontalAlignment(JTextField.CENTER);
        textUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textUser.setBorder(InitGameGUI.emptyBorder);
        textUser.setBounds(30, 160, 180, 30);
        
        labelPass = new JLabel("P A S S W O R D");
        labelPass.setHorizontalAlignment(JLabel.CENTER);
        labelPass.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelPass.setBounds(0, 200, 240, 20);
        
        textPass = new JPasswordField();
        textPass.setHorizontalAlignment(JTextField.CENTER);
        textPass.setFont(new Font("Serif", Font.PLAIN, 12));
        textPass.setEchoChar('*');
        textPass.setBorder(InitGameGUI.emptyBorder);
        textPass.setBounds(30, 230, 180, 30);
        
        butEnter = new JButton("ENTER");
        butEnter.setHorizontalAlignment(JButton.CENTER);
        butEnter.setFont(new Font("Monospaced", Font.BOLD, 11));
        butEnter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butEnter.setFocusPainted(false);
        butEnter.setBounds(30, 275, 85, 35);
        
        butEnter.addActionListener((ActionEvent e) -> {
            
            String name = textName.getText().trim();
            String user = textUser.getText().trim();
            String pass = textPass.getText().trim();
            
            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Incomplete fields!");
                
                if (pass.isEmpty()) textPass.requestFocus();
                if (user.isEmpty()) textUser.requestFocus();
                if (name.isEmpty()) textName.requestFocus();
                
            } else {
                if (!userExist(user)) {
                    if (insertRegistration(name, user, pass)) {
                        JOptionPane.showMessageDialog(null, "Successful "
                                + "registration!");
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, 
                                "Something went wrong!");
                        // The query couldn't be executed.
                    }
                } else {
                    JOptionPane.showMessageDialog(null, 
                            "The user is already in use!");
                    textUser.setText("");
                    textUser.requestFocus();
                }
            }
        });
        
        butExit = new JButton("CANCEL");
        butExit.setHorizontalAlignment(JButton.CENTER);
        butExit.setFont(new Font("Monospaced", Font.BOLD, 11));
        butExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butExit.setFocusPainted(false);
        butExit.setBounds(125, 275, 85, 35);
        
        butExit.addActionListener((ActionEvent e) -> {
            this.dispose(); // Close this window.
        });
        
        this.add(labelRegister);
        this.add(labelName);
        this.add(textName);
        this.add(labelUser);
        this.add(textUser);
        this.add(labelPass);
        this.add(textPass);
        this.add(butEnter);
        this.add(butExit);
        
        this.setVisible(true);
    }
    
    private boolean insertRegistration(String name, 
            String user, String pass) {
        
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            
            connection = DriverManager.getConnection
                        ("jdbc:mysql://localhost/dots_and_boxes", 
                                "root", "rootroot");
            
            String insertQuery = "INSERT INTO users (name, user, pass) "
                    + "VALUES(?, ?, sha1(?))";
            PreparedStatement insertStatement = connection.
                    prepareStatement(insertQuery);
            insertStatement.setString(1, name);
            insertStatement.setString(2, user);
            insertStatement.setString(3, pass);
            
            insertStatement.execute();
            
            connection.close();
            
        } catch(SQLException | ClassNotFoundException e) {
            System.out.println("Connection error: " + e); 
                return false; // Return connection status.
        }
        
        return true;
    }
    
    private boolean userExist(String user) {
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
             
            connection = DriverManager.getConnection
                        ("jdbc:mysql://localhost/dots_and_boxes", 
                                "root", "rootroot");
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery
                ("SELECT * FROM users WHERE user = '" + user + "' ");
            
            if (resultSet.next()) { 
                connection.close(); return true; 
            } else { connection.close(); }
                    
        } catch(SQLException | ClassNotFoundException e) 
            { System.out.println(e); }
        
        return false;
    }
}