package com.plectix.simulator.gui.panel;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.plectix.simulator.gui.lib.GradientPanelUI;


/**
 * <p>TODO document MainPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
@SuppressWarnings("serial")
public class MainPanel extends com.plectix.simulator.gui.lib.MainPanel {
	private ControlPanel controlPanel = null; 
	private GraphPanel graphPanel = null; 
	
	public MainPanel() {
		super();
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	
		// setDebug(true);
	}
	
	@Override
	public void initialize() {
		GridBagConstraintsEx gc = createNewConstraints();
		
		add(controlPanel, gc.insets(5, 5, 5, 5).fillHorizontal());
		add(graphPanel, gc.insets(0, 0, 0, 0).fillBoth().incy());
		
		controlPanel.addListener(graphPanel);
		setUI(new GradientPanelUI(new Color(219, 219, 219), new Color(187, 187, 187)));
		controlPanel.setOpaque(true);
		controlPanel.setBackground(new Color(209, 215, 226));
		controlPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, new Color(64, 64, 64)), new EmptyBorder(0, 0, 0, 2)));
	}

	public final void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	public final void setGraphPanel(GraphPanel graphPanel) {
		this.graphPanel = graphPanel;
	}

}
