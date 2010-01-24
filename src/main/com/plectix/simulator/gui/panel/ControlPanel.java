package com.plectix.simulator.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.gui.lib.GridBagPanel;
import com.plectix.simulator.gui.lib.UIProperties;
import com.plectix.simulator.simulator.DefaultSimulatorFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.streaming.LiveData;

/**
 * <p>TODO document ControlPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
@SuppressWarnings("serial")
class ControlPanel extends GridBagPanel implements ActionListener {
	private static final Logger LOGGER = Logger.getLogger(ControlPanel.class);

	private static final PrintStream DEFAULT_OUTPUT_STREAM = System.err;
	
	private static final int MINIMUM_LIVE_DATA_INTERVAL = 50;

	private static final int MAXIMUM_LIVE_DATA_INTERVAL = 30050;
	
	private static final String SIMULATION_COMBOBOX_ACTION_COMMAND = "SIMULATION_COMBOBOX_ACTION_COMMAND"; 
	
	private static final String COMMAND_LINE_OPTIONS = "--operation-mode 1 --agents-limit 100 --xml-session-name Session.xml --seed 1";
	
	private long simulationJobID = -1;
	
	private JComboBox simulationComboBox = null;
	private JButton startButton = null;
	private JButton stopButton = null;
	private JTextField commandLineOptionsTextField = null;
	private JProgressBar progressBar = null;

	private GraphPanel graphPanel = null;
	
	private List<SimulationSettings> simulationSettingsList = null;
	
	private List<ControlPanelListener> listeners = new ArrayList<ControlPanelListener>();

	private Timer timer = null;
	
	private SimulationService simulationService = null;

	private JLabel graphUpdatePeriodLabel = null;
	private JSlider graphUpdatePeriodSlider = null;
	private int graphUpdatePeriod = 200;
	
	private JLabel liveDataIntervalLabel = null;
	private JSlider liveDataIntervalSlider = null;
	private int liveDataInterval = 200;

	private int liveDataPoints = SimulationArguments.DEFAULT_LIVE_DATA_POINTS;
	
	private ControlPanel() {
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

		label = new JLabel(UIProperties.getString("controlpanel.livedatainterval.label"));
		add(label, gc.col(0).incy().span(1, 1).fillNone().anchorLeft());
		liveDataIntervalLabel = new JLabel(liveDataInterval + " msecs");
		add(liveDataIntervalLabel, gc.incx().anchorRight());
		liveDataIntervalSlider = new JSlider(MINIMUM_LIVE_DATA_INTERVAL, MAXIMUM_LIVE_DATA_INTERVAL, liveDataInterval);
		liveDataIntervalSlider.setExtent(MINIMUM_LIVE_DATA_INTERVAL);
		liveDataIntervalSlider.setMinorTickSpacing(10*MINIMUM_LIVE_DATA_INTERVAL);
		liveDataIntervalSlider.setMajorTickSpacing(100*MINIMUM_LIVE_DATA_INTERVAL);
		// liveDataIntervalSlider.setSnapToTicks(true);
		liveDataIntervalSlider.setPaintTicks(true);
		liveDataIntervalSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				liveDataInterval = liveDataIntervalSlider.getValue();
				liveDataIntervalLabel.setText(liveDataInterval + " msecs");
			} } );
		add(liveDataIntervalSlider, gc.incx().span(3, 1).fillHorizontal());

		label = new JLabel(UIProperties.getString("controlpanel.graphupdateperiod.label"));
		add(label, gc.col(0).incy().span(1, 1).fillNone().anchorLeft());
		graphUpdatePeriodLabel = new JLabel(graphUpdatePeriod + " msecs");
		add(graphUpdatePeriodLabel, gc.incx().anchorRight());
		graphUpdatePeriodSlider = new JSlider(MINIMUM_LIVE_DATA_INTERVAL, MAXIMUM_LIVE_DATA_INTERVAL, graphUpdatePeriod);
		graphUpdatePeriodSlider.setExtent(MINIMUM_LIVE_DATA_INTERVAL);
		graphUpdatePeriodSlider.setMinorTickSpacing(10*MINIMUM_LIVE_DATA_INTERVAL);
		graphUpdatePeriodSlider.setMajorTickSpacing(100*MINIMUM_LIVE_DATA_INTERVAL);
		// graphUpdatePeriodSlider.setSnapToTicks(true);
		graphUpdatePeriodSlider.setPaintTicks(true);
		graphUpdatePeriodSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				graphUpdatePeriod = graphUpdatePeriodSlider.getValue();
				graphUpdatePeriodLabel.setText(graphUpdatePeriod + " msecs");
			} } );
		add(graphUpdatePeriodSlider, gc.incx().span(3, 1).fillHorizontal());
		
		gc.col(0).incy().span(1, 1).insets(0, 5, 0, 0).fillHorizontal();
		
		// finally add a glue:
		gc.col(0).incy().span(1, 1).insets(0, 5, 0, 0);
		addGlue(gc, GridBagConstraints.BOTH);
		
		invalidate();
		validate();
		repaint();
	}
	
	private final void startSimulation() {
		String commandLineOptions = commandLineOptionsTextField.getText() 
									+ " --live-data-interval " + liveDataInterval 
									+ " --live-data-points " + liveDataPoints;
		
		if (commandLineOptions == null || commandLineOptions.length() == 0) {
			LOGGER.warn("commandLineOptions is null!!!");
		} else {
			LOGGER.info("Command line options:" + commandLineOptions);
			try {
				SimulatorCommandLine commandLine = new SimulatorCommandLine(commandLineOptions);
				graphPanel.clearConsole();
				LOGGER.info("Calling Simulator");
				Runtime.getRuntime().gc();
				simulationJobID = simulationService.submit(new SimulatorInputData(commandLine.getSimulationArguments(), new ConsolePrintStream(graphPanel.getTextArea())), null);
				setComponentsEnabled(true);
				
				progressBar.setIndeterminate(true);				
				graphPanel.resetCharts();

				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						checkStatus();
					}

				}, 0, graphUpdatePeriod);
				
			} catch (Exception exception) {
				if (simulationJobID >= 0) {
					simulationService.cancel(simulationJobID, true, true);
					simulationJobID = -1;
				}
				exception.printStackTrace();
			}
		}
	}
	
	private final void stopSimulation() {
		LOGGER.info("Stopping Simulator");
		if (simulationJobID >= 0) {
			simulationService.cancel(simulationJobID, true, true);						
			setComponentsEnabled(false);
			Runtime.getRuntime().gc();
		}
		simulationJobID = -1;
		LOGGER.info("Done with Simulator.");
	}

	private final void setComponentsEnabled(boolean simulationRunning) {
		startButton.setEnabled(!simulationRunning);
		stopButton.setEnabled(simulationRunning);
		simulationComboBox.setEnabled(!simulationRunning);
		commandLineOptionsTextField.setEnabled(!simulationRunning);
		liveDataIntervalSlider.setEnabled(!simulationRunning);
		graphUpdatePeriodSlider.setEnabled(!simulationRunning);
		progressBar.setEnabled(simulationRunning);
	}

	private void checkStatus() {
		SimulatorStatusInterface simulatorStatus = simulationService.getSimulatorStatus(simulationJobID);
		LiveData liveData = simulationService.getSimulatorLiveData(simulationJobID);
		
		// LOGGER.info("Simulator Status: " + simulatorStatus.getStatusMessage());
		
		if (simulatorStatus == null) {
			timer.cancel();
			timer.purge();

			progressBar.setIndeterminate(false);
			progressBar.setValue(0);
		} else {
			if (simulatorStatus.getProgress() == 1.0) {
				timer.cancel();
				timer.purge();
			}
			// System.err.println("Progress: " + simulatorStatus.getProgress() + " " + simulatorStatus.getStatusMessage());
			
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
				graphPanel.updateMemoryUsageChart();
			}
		}
		
		if (liveData != null) {
			graphPanel.updateLiveDataChart(liveData);
		}
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
