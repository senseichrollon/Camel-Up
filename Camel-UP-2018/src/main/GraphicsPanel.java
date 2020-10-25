package main;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JComponent;
import javax.swing.JPanel;

import graphics.engine.ScreenManager;

public class GraphicsPanel extends JPanel {


	public static GraphicsPanel GLOBAL_INSTANCE;
    private Clip clip;

	
	public GraphicsPanel() {
		setLayout(null);
		
		GLOBAL_INSTANCE = this;

		ScreenManager.init();
		
		MouseAdapter mouseIn = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				update(e, "mp");
				repaint();
			}
			
			public void mouseReleased(MouseEvent e) {
				update(e, "mr");
				repaint();

			}
			
			public void mouseMoved(MouseEvent e) {
					update(e, "mm");
					repaint();
			}
		};
		
		addMouseListener(mouseIn);
		addMouseMotionListener(mouseIn);	
		repaint();
//		playMusic();
	}
	
	
	public void update(MouseEvent evt, String code) {
		ScreenManager.getCurrentScreen().update(evt,code);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		ScreenManager.getCurrentScreen().render(g);
	}
	
	public void playMusic() {
		try {
		    AudioInputStream stream;
		    AudioFormat format;
		    DataLine.Info info; 
		    stream = AudioSystem.getAudioInputStream(new File("223761544711293.wav"));
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format); 
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(stream);
		    clip.loop(Clip.LOOP_CONTINUOUSLY);
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean containsComponent(JComponent component) {
		for(Component c : getComponents()) {
			if(c == component) {
				return true;
			}
		}
		return false;
	}
}
