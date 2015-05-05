package puzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

class SlidePuzzle {
 public static void main(String[] args) {
	 (new Thread(new Main())).start();
     JFrame window = new JFrame("Slide Puzzle");
     window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     window.setContentPane(new SlidePuzzleGUI());
     window.setUndecorated(true);
     window.pack();
     window.show();
     window.setResizable(false);
     window.setBackground((new Color(0,0,0,0)));
     window.setVisible(true);
     Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
     window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2-23);
     window.setAlwaysOnTop(true);
 }
}
