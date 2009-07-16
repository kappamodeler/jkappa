package com.plectix.simulator.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.gui.lib.GridBagPanel;
import com.plectix.simulator.gui.lib.UIProperties;
import com.plectix.simulator.simulator.DefaultSimulatorFactory;
import com.plectix.simulator.simulator.SimulatorCommandLine;

/**
 * <p>TODO document ControlPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
public class ControlPanel extends GridBagPanel implements ActionListener {
	private static final Logger LOGGER = Logger.getLogger(ControlPanel.class);

	private static final PrintStream DEFAULT_OUTPUT_STREAM = System.err;
	
	private static final String SIMULATION_COMBOBOX_ACTION_COMMAND = "SIMULATION_COMBOBOX_ACTION_COMMAND"; 
	
	private static final String COMMAND_LINE_OPTIONS = "--operation-mode 1 --agents-limit 100 --xml-session-name Session.xml --seed 1";
	
	// TODO: Add a slider bar to set the period:
	private long period = 200;
	
	private long simulationJobID = -1;
	
	private JComboBox simulationComboBox = null;
	private JButton startButton = null;
	private JButton stopButton = null;
	private JTextField commandLineOptionsTextField = null;
	private JProgressBar progressBar = null;

	private GraphPanel graphPanel = null;
	
	private List<SimulationSettings> simulationSettingsList = null;
	
	private List<ControlPanelListener> listeners = new ArrayList<ControlPanelListener>();

	private SimulationService simulationService = null;
	
	public ControlPanel() {
		super();

		simulationService = new SimulationService(new DefaultSimulatorFactory());
		// simulationService.shutdown();
	}
	
	public void initialize() {
		GridBagConstraintsEx gc = createNewConstraints().insets(5, 5, 5, 5);
		
		JLabel label = new JLabel(UIProperties.getString("controlpanel.simulation.label"));
		add(label, gc.fillBoth());
		
		simulationComboBox = new JComboBox();
		simulationComboBox.setActionCommand(SIMULATION_COMBOBOX_ACTION_COMMAND);
		simulationComboBox.addActionListener(this);
		add(simulationComboBox, gc.incx().fillNone());

		startButton = new JButton(UIProperties.getString("controlpanel.startbutton.label"));
		stopButton = new JButton(UIProperties.getString("controlpanel.stopbutton.label"));
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("Pressed button: " + startButton.getText());
				startSimulation();
			} 
		});

		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("Pressed button: " + stopButton.getText());
				stopSimulation();
			} 
		});
		stopButton.setEnabled(false);
		
		commandLineOptionsTextField = new JTextField();
		
		addGlue(gc, GridBagConstraints.BOTH);
		
		updatePanel();
	}
	
	public boolean addListener(ControlPanelListener listener) {
		// let's make sure that this listener is up-to-date:
		// LOGGER.info("Bringing listener " + listener + " up-to-date, currentTerminal is: " + currentTerminal.getArea());
		// listener.setCurrentTerminal(currentTerminal);
		boolean ret = listeners.add(listener);
		// notifyListeners(currentTerminal);
		return ret;
	}

	public boolean removeListener(ControlPanelListener listener) {
		return listeners.remove(listener);
	}
	
	private void updatePanel() {
		LOGGER.info("Updating panel");
		removeAll();
		
		GridBagConstraintsEx gc = createNewConstraints().insets(5, 5, 5, 5);

		JLabel label = new JLabel(UIProperties.getString("controlpanel.simulation.label"));
		add(label, gc.fillNone().anchorLeft());
		
		simulationComboBox.removeAllItems();
		if (simulationSettingsList != null) {
			for (SimulationSettings simulationSettings : simulationSettingsList) {
				simulationComboBox.addItem(simulationSettings);
			}
		}
		add(simulationComboBox, gc.incx());

		add(startButton, gc.incx().fillNone());
		add(stopButton, gc.incx().fillNone());
		add(progressBar, gc.incx().fillHorizontal());
		
		label = new JLabel(UIProperties.getString("controlpanel.commandline.label"));
		add(label, gc.col(0).incy().fillNone().anchorLeft());
		add(commandLineOptionsTextField, gc.incx().span(4, 1).fillHorizontal());

		gc.col(0).incy().span(1, 1).insets(0, 5, 0, 0).fillHorizontal();
		
		// finally add a glue:
		gc.col(0).incy().span(1, 1).insets(0, 5, 0, 0);
		addGlue(gc, GridBagConstraints.BOTH);
		
		invalidate();
		validate();
		repaint();
	}
	
	private final void startSimulation() {
		String commandLineOptions = commandLineOptionsTextField.getText();
		if (commandLineOptions == null || commandLineOptions.length() == 0) {
			LOGGER.warn("commandLineOptions is null!!!");
		} else {
			LOGGER.info("Command line options:" + commandLineOptions);
			try {
				LOGGER.info("Calling Simulator");
				SimulatorCommandLine commandLine = new SimulatorCommandLine(commandLineOptions);
				simulationJobID = simulationService.submit(new SimulatorInputData(commandLine.getSimulationArguments(), DEFAULT_OUTPUT_STREAM), null);
				setComponentsEnabled(true);
				
				progressBar.setIndeterminate(true);				
				graphPanel.resetCharts();
				new SimulatorStatusChecker(this, simulationService, simulationJobID, period);
			} catch (ParseException parseException) {
				if (simulationJobID >= 0) {
					simulationService.cancel(simulationJobID, true, true);
					simulationJobID = -1;
				}
				parseException.printStackTrace();
			}
		}
	}
	
	private final void stopSimulation() {
		LOGGER.info("Stopping Simulator");
		if (simulationJobID >= 0) {
			simulationService.cancel(simulationJobID, true, true);						
			setComponentsEnabled(false);
		}
		simulationJobID = -1;
		LOGGER.info("Done with Simulator.");
	}

	private final void setComponentsEnabled(boolean simulationRunning) {
		startButton.setEnabled(!simulationRunning);
		stopButton.setEnabled(simulationRunning);
		simulationComboBox.setEnabled(!simulationRunning);
		commandLineOptionsTextField.setEnabled(!simulationRunning);
		progressBar.setEnabled(simulationRunning);
	}
	
	public final void updateStatus(SimulatorStatusInterface simulatorStatus) {
		final int progress = (int) (100.0 * simulatorStatus.getProgress());
		if (progress != 0) {
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
			if (progress == 100) {
				stopSimulation();
				progressBar.setValue(0);
			}
		}
		
		if (graphPanel != null) {
			graphPanel.addMemoryUsageData();
		}
		
		// get data and plot
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(SIMULATION_COMBOBOX_ACTION_COMMAND)) {
			SimulationSettings selectedSimulation = (SimulationSettings)(((JComboBox)(e.getSource())).getSelectedItem());
			commandLineOptionsTextField.setText(selectedSimulation.getCommandLineOptions() + " " + COMMAND_LINE_OPTIONS);
		}
	}

	public final List<SimulationSettings> getSimulationSettingsList() {
		return simulationSettingsList;
	}

	public final void setSimulationSettingsList(List<SimulationSettings> simulationSettingsList) {
		this.simulationSettingsList = simulationSettingsList;
		/*
		for (SimulationSettings simulationSettings : simulationSettingsList) {
			System.err.println(simulationSettings.getCommandLineOptions());
		}
		*/
	}

	public final void setGraphPanel(GraphPanel graphPanel) {
		this.graphPanel = graphPanel;
	}

}
