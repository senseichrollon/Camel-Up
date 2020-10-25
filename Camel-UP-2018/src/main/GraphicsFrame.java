package main;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class GraphicsFrame extends JFrame {
	
	private GraphicsPanel panel;
	public static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(); // 1920
	public static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(); // 1080
	
	public GraphicsFrame(String title) {
		super(title);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		panel = new GraphicsPanel();
		add(panel);
		setVisible(true);
	}
}
