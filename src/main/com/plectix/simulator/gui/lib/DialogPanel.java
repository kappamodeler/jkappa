package com.plectix.simulator.gui.lib;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * <p>TODO document DialogPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
public class DialogPanel extends JDialog {
	public boolean accepted = false;
	
	public DialogPanel() {
		super();
		setModal(true);
	}

	public void setContent(JComponent content) {
		JPanel topContainer = new JPanel();
		topContainer.setLayout(new BorderLayout());
		topContainer.add(content, BorderLayout.CENTER);

		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accepted = true;
				setVisible(false);
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accepted = false;
				setVisible(false);
			}
		});
		
		setContentPane(topContainer);
		setSize(300, 200);
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(300, 200));

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				accepted = false;
				setVisible(false);
			}
		});
		
		invalidate();
		validate();
		pack();
	}
	
	public final boolean isAccepted() {
		return accepted;
	}

}
