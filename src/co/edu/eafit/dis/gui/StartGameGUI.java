
package co.edu.eafit.dis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartGameGUI extends JFrame {

    int width = 400, height = 600;
    int pointX, pointY; // Coordinates.
    
    int rows = 8, cols = 8;

    JPanel gamePanel = new JPanel();
    
    JLabel dotsArray[][];
    
    public Point actualPoint = null;
    public Point newPoint = null;
    
    boolean state = false;
    
    Graphics graphics;
    
    int flag = 0;

    public StartGameGUI() {
        drawLinesGUI(rows, cols);
    }

    private void drawLinesGUI(int rows, int cols) {
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
                        
                        if (actualPoint != null) {
                            
                            setPointX((int) x.getX()); 
                            setPointY((int) y.getY());
                        
                            newPoint = new Point(getPointX(), getPointY());
                            state = true;
                            
                            /*System.out.println(
                                      "  x1: " + actualPoint.getX() 
                                    + ", y1: " + actualPoint.getY() 
                                    + ", x2: " + newPoint.getX() 
                                    + ", y2: " + newPoint.getY());*/
                            
                            boolean isLineCorrect = validateLine
                                (actualPoint, newPoint);
                            
                            if (isLineCorrect) {
                                //repaint();
                                
                                flag = 1;
                                drawLine(graphics);
                                //repaint();
                            } else {
                                System.out.println("not correct!");
                            }
                            
                            actualPoint = null; // Reload the initial point;
                            newPoint = null;
                            state = false;
                            
                        } else {
                            setPointX((int) x.getX()); 
                            setPointY((int) y.getY());
                            
                            actualPoint = new Point(getPointX(), getPointY());
                            state = false;
                        }
                        
                        //System.out.println("point x " + getPointX());
                        //System.out.println("point y " + getPointY());
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
                
                dots[i][j].setLocation(x, y);
                
                gamePanel.add(dots[i][j]);
            }
        }
        
        return dots;
    }
    
    private void drawLine(Graphics g) {
        
        if(state) {
            int x0 = (int)actualPoint.getX();
            int y0 = (int)actualPoint.getY();
            int x1 = (int)newPoint.getX();
            int y1 = (int)newPoint.getY();
            
            System.out.println("x0: "+x0);
            System.out.println("y0: "+y0);
            System.out.println("x1: "+x1);
            System.out.println("y1: "+y1);
            
            System.out.println("entro pero no dibuja!!!");
            
            g.drawLine(x0, y0, x1, y1);
        }
    }
    
    @Override
    public void paint(Graphics g) {
        
        graphics = g;
        super.paint(graphics); // Overlap paint.
        
        Graphics2D g2d = (Graphics2D) graphics;
        drawLine(g2d);
    }
    
    private boolean validateLine(Point iP, Point fP) {
        // iP: initial point, fP: final point.
        
        int dist = (gamePanel.getWidth() - 8) / (cols);
        boolean lineStatus = false;
        
        if (iP.getX() == fP.getX()) {
            if (iP.getY() > fP.getY()) {
                if (iP.getY() - fP.getY() == dist) {
                    lineStatus = true;
                }
            } else {
                if (iP.getY() < fP.getY()) {
                    if (fP.getY() - iP.getY() == dist) {
                        lineStatus = true;
                    }
                }
            }
        } else {
            if (iP.getY() == fP.getY()) {
                if (iP.getX() > fP.getX()) {
                    if (iP.getX() - fP.getX() == dist) {
                        lineStatus = true;
                    }
                } else {
                    if (iP.getX() < fP.getX()) {
                        if (fP.getX() - iP.getX() == dist) {
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
