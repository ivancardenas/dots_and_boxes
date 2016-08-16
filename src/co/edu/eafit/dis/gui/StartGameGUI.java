
package co.edu.eafit.dis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StartGameGUI extends JFrame {
    
    JPanel gamePanel = new JPanel();

    int width = 400, height = 600;
    int pointX = 0, pointY = 0;
    
    int cols = 0;
    
    String user;
    
    Point actualPoint = null;
    Point finalPoint = null;
    
    JLabel dotsArray[][], namesArray[][];
    
    Connection connection;
    
    int gameID;
    
    public StartGameGUI(int rows, int cols) {
        
        drawLinesGUI(rows, cols);
        
        Timer timer = new Timer(5000, 
                (ActionEvent e) -> {
            repaint();
        });
        
        timer.start();
        
        this.cols = cols;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            connection = DriverManager.getConnection
                ("jdbc:mysql://localhost/dots_and_boxes", "root", "rootroot");
            
        } catch (ClassNotFoundException | SQLException e) {}
    }

    private void drawLinesGUI(int rows, int cols) {
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setOfflineStatus(user);
                exitGame(gameID);
            }
        });
        
        this.setTitle("Dots and Boxes");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        this.getContentPane().setBackground(Color.WHITE);

        gamePanel.setBounds(30, 30, this.getWidth() - 60,
                this.getWidth() - 60);
        gamePanel.setBackground(Color.WHITE);
        gamePanel.setLayout(null);

        dotsArray = paintDots(rows, cols);
        
        namesArray = paintNames(rows, cols);
        
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j <= cols; j++) {
                
                Point x = dotsArray[i][j].getLocation();
                Point y = dotsArray[i][j].getLocation();
                
                dotsArray[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        
                        int dis = (gamePanel.getWidth() - 8) / (cols);
                        
                        if (actualPoint != null) {
                            
                            setPointX(((int) x.getX()) / dis);
                            setPointY(((int) y.getY()) / dis);
                        
                            finalPoint = new Point(getPointX(), getPointY());
                            
                            boolean isLineCorrect = validateLine
                                (actualPoint, finalPoint);
                            
                            if (isLineCorrect) {
                                
                                insertPointsDB((int)actualPoint.getX(),
                                        (int)actualPoint.getY(),
                                        (int)finalPoint.getX(),
                                        (int)finalPoint.getY());
                                
                                repaint();
                            } else {
                                JOptionPane.showMessageDialog(
                                        null, "Invalid movement!");
                            }
                            
                            actualPoint = null; // Reload the initial point;
                            finalPoint = null;  // Reload the final point;
                            
                        } else {
                            setPointX(((int) x.getX()) / dis);
                            setPointY(((int) y.getY()) / dis);
                            
                            actualPoint = new Point(getPointX(), getPointY());
                        }
                    }
                });
            }
        }
        
        this.add(gamePanel);

        this.setVisible(true);
    }

    private JLabel[][] paintDots(int rows, int cols) {

        JLabel dots[][] = new JLabel[rows + 1][cols + 1];

        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j <= cols; j++) {

                dots[i][j] = new JLabel("•");
                dots[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));
                dots[i][j].setSize(10, 10);
                
                int x = (gamePanel.getWidth() - 8) / (cols) * j;
                int y = (gamePanel.getWidth() - 8) / (rows) * i;
                
                dots[i][j].setLocation(x,y);
                
                gamePanel.add(dots[i][j]);
            }
        }
        
        return dots;
    }
    
    private void insertPointsDB(int x0, int y0, int x1, int y1) {
        
        String insertQuery = "INSERT INTO POINTS (x0, y0, x1, y1, idgame)"
                + " VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement prepState = connection
                    .prepareStatement(insertQuery);
            prepState.setInt(1, x0);
            prepState.setInt(2, y0);
            prepState.setInt(3, x1);
            prepState.setInt(4, y1);
            prepState.setInt(5, gameID); // idgame insertion.
            
            prepState.execute();
            
        } catch(SQLException e) {}
    }
    
    private JLabel [][] paintNames(int rows, int cols) {
        JLabel names[][] = new JLabel[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                names[i][j] = new JLabel("S");
                names[i][j].setSize(15, 15);
                
                names[i][j].setFont(new Font("Monospace", 
                        Font.PLAIN, 15));
                
                int x = (gamePanel.getWidth() - 8) / (cols) * j;
                int y = (gamePanel.getWidth() - 8) / (rows) * i;
                
                names[i][j].setLocation(x + 33, y + 33);
                
                gamePanel.add(names[i][j]);
                
                names[i][j].setVisible(false);
            }
        }
        
        return names;
    }
    
    @Override
    public void paint(Graphics g) {
        
        super.paint(g); // Overlap paint.
        
        int dis = (gamePanel.getWidth() - 8) / (cols);
        
        int x0 = 0, y0 = 0, x1 = 0, y1 = 0;

        try {

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery (
                    "SELECT * FROM POINTS WHERE idgame = '" + gameID + "' ");

            while (result.next()) {

                x0 = result.getInt(2);
                y0 = result.getInt(3);
                x1 = result.getInt(4);
                y1 = result.getInt(5);

                g.drawLine(x0 * dis + 33, y0 * dis + 57, 
                        x1 * dis + 33, y1 * dis + 57);
            }


        } catch(SQLException e) {}

        paintSquare();
    }
    
    private void paintSquare() {
        
        int x0 = 0, y0 = 0, x1 = 0, y1 = 0;
        
        try {
            
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT * FROM POINTS WHERE idgame =  '" + gameID + "' ");
            
            while (result.next()) {
                
                x0 = result.getInt(2);
                y0 = result.getInt(3);
                x1 = result.getInt(4);
                y1 = result.getInt(5);
                
                if (thereIsALine(x0, y0, x0, y0 + 1) || 
                    thereIsALine(x0, y0 + 1, x0, y0)) {
                    if (thereIsALine(x0, y0 + 1, x1, y1 + 1) || 
                            thereIsALine(x1, y1 + 1, x0, y0 + 1)) {
                        if (thereIsALine(x1, y1 + 1, x1, y1) || 
                                thereIsALine(x1, y1, x1, y1 + 1)) {

                            if (x1 > x0) namesArray[y1][x1 - 1]
                                    .setVisible(true);
                            else if (x0 > x1) namesArray[y1][x1]
                                    .setVisible(true);
                        }
                    }
                }
                if (thereIsALine(x0, y0, x0, y0 - 1) || 
                        thereIsALine(x0, y0 - 1, x0, y0)) {
                    if (thereIsALine(x0, y0 - 1, x1, y1 - 1) || 
                            thereIsALine(x1, y1 - 1, x0, y0 - 1)) {
                        if (thereIsALine(x1, y1 - 1, x1, y1) || 
                                thereIsALine(x1, y1, x1, y1 - 1)) {

                            if (x1 > x0) namesArray[y1 - 1][x1 - 1]
                                    .setVisible(true);
                            else if (x0 > x1) namesArray[y1 - 1][x1]
                                    .setVisible(true);
                        }
                    }
                }
                if (thereIsALine(x0, y0, x0 + 1, y0) || 
                        thereIsALine(x0 + 1, y0, x0, y0)) {
                    if (thereIsALine(x0 + 1, y0, x1 + 1, y1) || 
                            thereIsALine(x1 + 1, y1, x0 + 1, y0)) {
                        if (thereIsALine(x1 + 1, y1, x1, y1) || 
                                thereIsALine(x1, y1, x1 + 1, y1)) {

                            if (y1 > y0) namesArray[y1 - 1][x1]
                                    .setVisible(true);
                            else if (y0 > y1) namesArray[y1][x1]
                                    .setVisible(true);
                        }
                    }
                }
                if (thereIsALine(x0, y0, x0 - 1, y0) || 
                        thereIsALine(x0 - 1, y0, x0, y0)) {
                    if (thereIsALine(x0 - 1, y0, x1 - 1, y1) || 
                            thereIsALine(x1 - 1, y1, x0 - 1, y0)) {
                        if (thereIsALine(x1 - 1, y1, x1, y1) || 
                                thereIsALine(x1, y1, x1 - 1, y1)) {

                            if (y1 > y0) namesArray[y1 - 1][x1 - 1]
                                    .setVisible(true);
                            else if (y0 > y1) namesArray[y1][x1 - 1]
                                    .setVisible(true);
                        }
                    }
                }
            }
            
        } catch(SQLException e) {}
    }
    
    private boolean thereIsALine(int x0, int y0, int x1, int y1) {
        
        int nx0 = 0, ny0 = 0, nx1 = 0, ny1 = 0;
        
        try {
            
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT * FROM POINTS WHERE idgame = '" + gameID + "' ");
            
            while (result.next()) {
                
                nx0 = result.getInt(2);
                ny0 = result.getInt(3);
                nx1 = result.getInt(4);
                ny1 = result.getInt(5);
                
                if (x0 == nx0 && y0 == ny0) {
                    if (x1 == nx1 && y1 == ny1) {
                        return true;
                    }
                }
            }
            
        } catch(SQLException e) {}
        
        return false;
    }
    
    private boolean validateLine(Point iP, Point fP) {
        // iP: initial point, fP: final point.
        
        boolean lineStatus = false;
        
        if (iP.getX() == fP.getX()) {
            if (iP.getY() > fP.getY()) {
                if (iP.getY() - fP.getY() == 1) {
                    lineStatus = true;
                }
            } else {
                if (iP.getY() < fP.getY()) {
                    if (fP.getY() - iP.getY() == 1) {
                        lineStatus = true;
                    }
                }
            }
        } else {
            if (iP.getY() == fP.getY()) {
                if (iP.getX() > fP.getX()) {
                    if (iP.getX() - fP.getX() == 1) {
                        lineStatus = true;
                    }
                } else {
                    if (iP.getX() < fP.getX()) {
                        if (fP.getX() - iP.getX() == 1) {
                            lineStatus = true;
                        }
                    }
                }
            }
        }
        
        return lineStatus;
    }
    
    private void exitGame(int gameID) {
        
        try {
            String insertQuery = "UPDATE games SET state = 0 WHERE idgame = ?";
            
            PreparedStatement prepState = connection
                    .prepareStatement(insertQuery);
            prepState.setInt(1, gameID);
            
            prepState.executeUpdate();
            
        } catch(SQLException e) {}
    }
    
    private void setOfflineStatus(String user) {
        
        System.out.println("update");
        try {
            String insertQuery = "UPDATE users SET state = 0 WHERE user = ?";
            
            System.out.println("update: " + user);
            
            PreparedStatement prepState = connection
                    .prepareStatement(insertQuery);
            prepState.setString(1, user);
            
            prepState.executeUpdate();
            
        } catch(SQLException e) {}
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    
    private int getPointX() {
        return pointX;
    }
    
    private int getPointY() {
        return pointY;
    }
    
    private void setPointX(int pointX) {
        this.pointX = pointX;
    }
    
    private void setPointY(int pointY) {
        this.pointY = pointY;
    }
}
