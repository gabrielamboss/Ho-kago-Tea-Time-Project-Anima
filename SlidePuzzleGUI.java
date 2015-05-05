package puzzle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.sun.awt.AWTUtilities;

class SlidePuzzleGUI extends JPanel {
    private GraphicsPanel    _puzzleGraphics;
    private SlidePuzzleModel _puzzleModel = new SlidePuzzleModel();

    public SlidePuzzleGUI() {
        JPanel controlPanel = new JPanel();
        _puzzleGraphics = new GraphicsPanel();
        this.setLayout(new BorderLayout());
        this.add(_puzzleGraphics, BorderLayout.CENTER);
    }


    class GraphicsPanel extends JPanel implements MouseListener {
        private static final int ROWS = 3;
        private static final int COLS = 3;
        
        private static final int CELL_SIZE = 220;
        private Font _biggerFont;
        
        public GraphicsPanel() {
            _biggerFont = new Font("SansSerif", Font.BOLD, CELL_SIZE/2);
            this.setPreferredSize(
                   new Dimension(CELL_SIZE * COLS, CELL_SIZE*ROWS));
            this.setOpaque(false);
            this.addMouseListener(this);
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int r=0; r<ROWS; r++) {
                for (int c=0; c<COLS; c++) {
                    int x = c * CELL_SIZE;
                    int y = r * CELL_SIZE;
                    String text = _puzzleModel.getFace(r, c);
                    if (text != null) {
                        g.setColor(new Color(49,152,157,75));
                        g.fillRect(x+2, y+2, CELL_SIZE-4, CELL_SIZE-4);
                        g.setColor(new Color(26,43,23,255));
                        g.setFont(_biggerFont);
                        g.drawString(text, x+CELL_SIZE/2-33, y+CELL_SIZE/2+44);
                    }
                }
            }
        }
        
        
        public void mousePressed(MouseEvent e) {
            int col = e.getX()/CELL_SIZE;
            int row = e.getY()/CELL_SIZE;
            
            if (!_puzzleModel.moveTile(row, col)) {
                Toolkit.getDefaultToolkit().beep();
            }
            
            this.repaint();
        }
        
        public void mouseClicked (MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered (MouseEvent e) {}
        public void mouseExited  (MouseEvent e) {}
    }
}
