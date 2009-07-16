package com.plectix.simulator.gui.lib;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


/**
 * Currently this consists of a split
 * pane with the tree of scenarios on the left, and the selected panel
 * (if any) on the right.
 * 
 * @author ecemis
 */
public class SplitPanel extends MainPanel {

	private JPanel treePanel;
	private JPanel tablePanel;
	
	public SplitPanel() {
		super();
	}

	public void setTablePanel(JPanel tablePanel) {
		this.tablePanel = tablePanel;
	}


	public void setTreePanel(JPanel treePanel) {
		this.treePanel = treePanel;
	}

	/**
	 * This method is called automatically by the Spring Framework
	 * after all beans have been created.  Here we set up our sub-
	 * panels: the tree panel on the left and the table panel on 
	 * the right, with a split pane to contain them.
	 */
	@Override
	public void initialize() {
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, tablePanel);
		split.setDividerLocation(250);
		split.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		add(split, createNewConstraints().fillBoth());
	}
	
}
