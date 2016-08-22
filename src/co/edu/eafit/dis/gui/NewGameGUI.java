
package co.edu.eafit.dis.gui;

import static co.edu.eafit.dis.gui.InitGameGUI.emptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class NewGameGUI extends JFrame {
    
    int width = 220, height = 225;
    
    JLabel labelNewGame, labelRows, labelCols, labelAddPlayer;
    
    JTextField textRows, textCols, textAddPlayer;
    
    Connection connection;
    
    String user;
    
    JButton butEnter;
    
    public NewGameGUI() {
        createNewGame();
        
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection
                        ("jdbc:mysql://10.131.137.164:3306/dots_and_boxes", 
                                "root", "");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection error: " + e);
        }
    }
    
    private void createNewGame() {
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setOfflineStatus(user);
            }
        });
        
        this.setTitle("Start New Game");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        this.getContentPane().setBackground(Color.WHITE);
        
        labelNewGame = new JLabel("N E W  G A M E");
        labelNewGame.setBounds(0, 30, 220, 20);
        labelNewGame.setHorizontalAlignment(JLabel.CENTER);
        labelNewGame.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
//        labelRows = new JLabel("R O W S");
//        labelRows.setBounds(30, 65, 70, 20);
//        labelRows.setHorizontalAlignment(JLabel.CENTER);
//        labelRows.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        
//        textRows = new JTextField();
//        textRows.setBounds(30, 90, 70, 30);
//        textRows.setHorizontalAlignment(JTextField.CENTER);
//        textRows.setBorder(emptyBorder);
//        textRows.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        textRows.setToolTipText("Values between 5 and 8");
//        
//        labelCols = new JLabel("C O L S");
//        labelCols.setBounds(120, 65, 70, 20);
//        labelCols.setHorizontalAlignment(JLabel.CENTER);
//        labelCols.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        
//        textCols = new JTextField();
//        textCols.setBounds(120, 90, 70, 30);
//        textCols.setHorizontalAlignment(JTextField.CENTER);
//        textCols.setBorder(emptyBorder);
//        textCols.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        textCols.setToolTipText("Values between 5 and 8");
        
        labelAddPlayer = new JLabel("A D D  P L A Y E R");
        labelAddPlayer.setBounds(0, 65, 220, 20);
        labelAddPlayer.setHorizontalAlignment(JLabel.CENTER);
        labelAddPlayer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        textAddPlayer = new JTextField();
        textAddPlayer.setBounds(30, 95, 160, 30);
        textAddPlayer.setHorizontalAlignment(JTextField.CENTER);
        textAddPlayer.setBorder(emptyBorder);
        textAddPlayer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textAddPlayer.setToolTipText("Username of your opponent!");
        
        butEnter = new JButton("N E W  G A M E");
        butEnter.setHorizontalAlignment(JButton.CENTER);
        butEnter.setFont(new Font("Monospaced", Font.BOLD, 11));
        butEnter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butEnter.setFocusPainted(false);
        butEnter.setBounds(30, 140, 160, 35);
        
        butEnter.addActionListener((ActionEvent e) -> {
            
            if (textAddPlayer.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Incomplete field!");
                
                textAddPlayer.requestFocus();
                
            } else if (userExists(textAddPlayer.getText()) && 
                    !textAddPlayer.getText().equals(user)) {
                
                String player = textAddPlayer.getText();

                try {
                    
                    int gameStatus1[] = thereIsAnotherGame(user, player);
                    int gameStatus2[] = thereIsAnotherGame(player, user);
                    
                    if (gameStatus1[0] == 1) {
                        StartGameGUI startGameGUI = 
                            new StartGameGUI(5, 5);
                        startGameGUI.setUser(user);
                        startGameGUI.setPlayer(player);
                        startGameGUI.setVisible(true);
                        startGameGUI.setGameID(gameStatus1[1]);
                        
                        this.dispose();
                    } else if (gameStatus2[0] == 1) {    
                        StartGameGUI startGameGUI = 
                            new StartGameGUI(5, 5);
                        startGameGUI.setUser(user);
                        startGameGUI.setPlayer(player);
                        startGameGUI.setVisible(true);
                        startGameGUI.setGameID(gameStatus2[1]);
                        
                        this.dispose();
                        
                    } else {

                        String insertQuery = "INSERT INTO games (user, player, state) "
                            + "VALUES (?, ?, ?)";

                        PreparedStatement prepState = connection
                                .prepareStatement(insertQuery);

                        prepState.setString(1, user);
                        prepState.setString(2, player);
                        prepState.setInt(3, 1);

                        prepState.execute();

                        StartGameGUI startGameGUI = 
                                new StartGameGUI(5, 5);
                        startGameGUI.setUser(user);
                        startGameGUI.setPlayer(player);
                        startGameGUI.setVisible(true);

                        String selectQuery = "SELECT * FROM games WHERE "
                                + "user = '" + user + "' AND "
                                + "player = '" + player + "' "
                                + "ORDER BY idgame DESC";

                        Statement statement = connection.createStatement(); 
                        ResultSet result = statement.executeQuery (selectQuery);

                        int flagResult = 0;

                        while (result.next()) {
                            if (flagResult == 0) {
                                startGameGUI.setGameID
                                    (result.getInt(1));
                                flagResult++;
                            }
                        }

                        this.dispose();
                    }

                } catch (SQLException ex) {
                    System.out.println(""+ex);
                }
                
            } else {
                
                JOptionPane.showMessageDialog(null, 
                        "  The user you entered is"
                    + "\nincorrect, or is not online.");
                
                textAddPlayer.setText(""); 
                textAddPlayer.requestFocus();
            }
            
        });
        
        this.add(labelNewGame);
//        this.add(labelRows);
//        this.add(textRows);
//        this.add(labelCols);
//        this.add(textCols);
        this.add(labelAddPlayer);
        this.add(textAddPlayer);
        this.add(butEnter);
        
        this.setVisible(true);
    }
    
    private int[] thereIsAnotherGame(String user, String player) {
        
        int gameStatus[] = new int[2];
        
        try {
            
            String selectQuery = "SELECT * FROM games WHERE "
                    + "user = '" + user + "' AND "
                    + "player = '" + player + "' AND state = 1";
            
            Statement statement = connection.createStatement(); 
            ResultSet result = statement.executeQuery (selectQuery);
            
            int thereIsAGameFlag = 0;
            
            while (result.next()) {
                if (thereIsAGameFlag == 0) {
                    gameStatus[0] = 1;
                    gameStatus[1] = result.getInt(1);
                    thereIsAGameFlag++;
                }
            }
            
        } catch(SQLException e) {}
        
        return gameStatus;
    }
    
    private boolean userExists(String player) {
        try {
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery
                ("SELECT * FROM users WHERE user = '" + player + "' AND state = 1");
            
            if (resultSet.next()) return true; 
                    
        } catch(SQLException e) {}
        
        return false;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    private void setOfflineStatus(String user) {
        try {
            String insertQuery = "UPDATE users SET state = 0 WHERE user = ?";
            
            PreparedStatement prepState = connection
                    .prepareStatement(insertQuery);
            prepState.setString(1, user);
            
            prepState.executeUpdate();
            
        } catch(SQLException e) {}
    }
}
