package com.plectix.simulator.gui.lib;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private MainPanel mainPanel;
	
	public MainWindow() {
		super(UIProperties.getString("window.title"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1000, 700);
		final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
	}
	
	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	/**
	 * This method is called automatically by the Spring Framework
	 * after all beans have been created.  Here we set up our main
	 * panel and make ourselves visible.
	 */
	public void initialize() {
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitingEventQueue(this));
		setContentPane(mainPanel);
		setVisible(true);
	}
}
