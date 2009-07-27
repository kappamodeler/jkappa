package com.plectix.simulator.gui.lib;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Utility class for showing dialogs to the user
 * 
 * @autho ecemis
 */
public class PromptDialogs {
	
	private JFrame frame;
	private FileDialog fileDialog;
	private static PromptDialogs instance;

	public PromptDialogs() {
		// This constructor is called by Spring so it's not private.
		// Thus this class is not a true singleton (not that it matters).
		instance = this;
	}
	
	public static PromptDialogs getInstance() {
		return instance;
	}
	
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public File promptLoad(final String extension) {
		return showFileDialog("Load File", FileDialog.LOAD, new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		}, null);
	}

	public File promptSave(String filename) {
		return showFileDialog("Save File", FileDialog.SAVE, null, filename);
	}

	public void promptInfo(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void promptError(String message, Exception e) {
		promptError("<html>" + message + ":<br>" + e.getLocalizedMessage());
		e.printStackTrace();
	}

	public void promptError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public boolean promptConfirm(String title, String message) {
		int result = JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.OK_CANCEL_OPTION);
		return result == JOptionPane.OK_OPTION;
	}
	
	private final File showFileDialog(String title, int mode, FilenameFilter filter, String filename) {
		
		if (fileDialog == null)
			fileDialog = new FileDialog(frame);
		fileDialog.setTitle(title);
		fileDialog.setMode(mode);
		fileDialog.setFilenameFilter(filter);
		if (filename != null)
			fileDialog.setFile(filename);

		fileDialog.setVisible(true);
			
		if (fileDialog.getFile() == null)
			return null;
		else
			return new File(fileDialog.getDirectory(), fileDialog.getFile());
		
	}

}
