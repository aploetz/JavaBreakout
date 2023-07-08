package javabreakout;

import javax.swing.JFrame;

public class BreakoutGame {

	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		BreakoutPanel panel = new BreakoutPanel();
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//window.setResizable(false);
		window.setTitle("Java Breakout");
		window.add(panel);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		panel.start();
	}

}
