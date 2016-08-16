
package co.edu.eafit.dis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.Font;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JPasswordField;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class InitGameGUI extends JFrame {
    
    int width = 460, height = 300;
    
    JLabel labelGameIcon, labelGameName, 
            labelUser, labelPass, labelLogin;
    
    JTextField textUser;
    
    JPasswordField textPass;
    
    JButton butEnter, butExit;
    
    ImageIcon imageGameIcon;
    
    static Border emptyBorder = BorderFactory.
            createLineBorder(Color.GRAY);
    
    Connection connection;
    
    public InitGameGUI() {
        startGameGUI();
    }
    
    private void startGameGUI() {
        
        this.setTitle("Dots and Boxes");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        this.getContentPane().setBackground(Color.WHITE);
        
        imageGameIcon = new ImageIcon("src/co/edu/eafit/dis/res" + 
                "/dots_and_boxes.png");
        labelGameIcon = new JLabel(imageGameIcon);
        labelGameIcon.setBounds(30, 30, imageGameIcon.getIconWidth(),
                imageGameIcon.getIconHeight());
        
        labelGameName = new JLabel("D O T S  A N D  B O X E S");
        labelGameName.setBounds(30, 220, imageGameIcon.getIconWidth(), 20);
        labelGameName.setHorizontalAlignment(JLabel.CENTER);
        labelGameName.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        labelUser = new JLabel("U S E R N A M E");
        labelUser.setHorizontalAlignment(JLabel.CENTER);
        labelUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelUser.setBounds(230, 30, 200, 20);
        
        textUser = new JTextField();
        textUser.setHorizontalAlignment(JTextField.CENTER);
        textUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textUser.setBorder(emptyBorder);
        textUser.setBounds(230, 60, 200, 30);
        
        labelPass = new JLabel("P A S S W O R D");
        labelPass.setHorizontalAlignment(JLabel.CENTER);
        labelPass.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelPass.setBounds(230,105,200,20);
        
        textPass = new JPasswordField("");
        textPass.setHorizontalAlignment(JTextField.CENTER);
        textPass.setFont(new Font("Serif", Font.PLAIN, 13));
        textPass.setEchoChar('*');
        textPass.setBorder(emptyBorder);
        textPass.setBounds(230, 135, 200, 30);
        
        labelLogin = new JLabel("Create an account");
        labelLogin.setHorizontalAlignment(JLabel.CENTER);
        labelLogin.setFont(new Font("Monospaced", Font.ITALIC, 10));
        labelLogin.setForeground(Color.BLUE);
        labelLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelLogin.setBounds(230, 170, 200, 20);
        
        butEnter = new JButton("E N T E R");
        butEnter.setHorizontalAlignment(JButton.CENTER);
        butEnter.setFont(new Font("Monospaced", Font.BOLD, 11));
        butEnter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butEnter.setFocusPainted(false);
        butEnter.setBounds(230, 205, 100, 35);
        
        butEnter.addActionListener((ActionEvent e) -> {
            
            String user = textUser.getText().trim();
            String pass = textPass.getText().trim();
            
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Incomplete fields!");
                
                if (user.isEmpty()) textUser.requestFocus();
                if (pass.isEmpty()) textPass.requestFocus();
                
            } else {
                if (loginComparison(user, pass)) {
                    this.dispose();
                    
                    setOnlineStatus(user);
                    
                    NewGameGUI newGameGUI = new NewGameGUI();
                    newGameGUI.setUser(user);
                    
                } else {
                    JOptionPane.showMessageDialog(null, 
                            "Username or password incorrect!");
                }
            }
        });
        
        butExit = new JButton("E X I T");
        butExit.setHorizontalAlignment(JButton.CENTER);
        butExit.setFont(new Font("Monospaced", Font.BOLD, 11));
        butExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butExit.setFocusPainted(false);
        butExit.setBounds(330, 205, 100, 35);
        
        butExit.addActionListener((ActionEvent e) -> {
            System.exit(0); // Leave execution.
        });
        
        labelLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterGUI registerGUI = new RegisterGUI();
                registerGUI.setVisible(true);
            }
        });
        
        this.add(labelUser);
        this.add(labelPass);
        this.add(textUser);
        this.add(textPass);
        this.add(labelLogin);
        this.add(butEnter);
        this.add(butExit);
        this.add(labelGameIcon);
        this.add(labelGameName);
        
        this.setVisible(true);
    }
    
    private void setOnlineStatus(String user) {
        try {
            String insertQuery = "UPDATE users SET state = 1 WHERE user = ?";
            
            PreparedStatement prepState = connection
                    .prepareStatement(insertQuery);
            prepState.setString(1, user);
            
            prepState.executeUpdate();
            
        } catch(SQLException e) {}
    }
    
    private boolean loginComparison(String user, String pass) {
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
             
            connection = DriverManager.getConnection
                        ("jdbc:mysql://localhost/dots_and_boxes", 
                                "root", "rootroot");
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery
                ("SELECT * FROM users WHERE user = '" + user + "' "
                        + "AND pass = sha1('" + pass + "')");
            
            if (resultSet.next()) return true;
                    
        } catch(SQLException | ClassNotFoundException e) 
            { System.out.println(e); }
        
        return false;
    }
}