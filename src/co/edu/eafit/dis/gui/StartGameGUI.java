
package co.edu.eafit.dis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Color;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartGameGUI extends JFrame {
    
    ArrayList<Integer> points = new ArrayList<>();
    
    JPanel gamePanel = new JPanel();

    int width = 400, height = 600;
    int pointX = 0, pointY = 0;
    int rows = 5, cols = 5;
    
    Point actualPoint = null;
    Point finalPoint = null;
    
    JLabel dotsArray[][];
    
    public StartGameGUI() {
        drawLinesGUI(rows, cols);
    }

    private void drawLinesGUI(int rows, int cols) {
        this.setTitle("Dots and Boxes");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(2);
        this.getContentPane().setBackground(Color.WHITE);

        gamePanel.setBounds(30, 30, this.getWidth() - 60,
                this.getWidth() - 60);
        gamePanel.setBackground(Color.WHITE);
        gamePanel.setLayout(null);

        dotsArray = paintDots(rows, cols);
        
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
                                
                                points.add((int)actualPoint.getX());
                                points.add((int)actualPoint.getY());
                                points.add((int)finalPoint.getX());
                                points.add((int)finalPoint.getY());
                                
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
    
    @Override
    public void paint(Graphics g) {
        
        super.paint(g); // Overlap paint.
        
        int dis = (gamePanel.getWidth() - 8) / (cols);
        
        if(!points.isEmpty()) {
            
            int x0 = 0, y0 = 0, x1 = 0, y1 = 0;
            
            for (int i = 0; i < points.size(); i = i+4) {
                x0 = dis * points.get(i) + 33;
                y0 = dis * points.get(i+1) + 57;
                x1 = dis * points.get(i+2) + 33;
                y1 = dis * points.get(i+3) + 57;
                
                g.drawLine(x0, y0, x1, y1);
            }   
        }
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
