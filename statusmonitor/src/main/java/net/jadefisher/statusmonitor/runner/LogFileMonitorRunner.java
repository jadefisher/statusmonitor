package net.jadefisher.statusmonitor.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.model.LogFileMonitor;
import net.jadefisher.statusmonitor.model.LogType;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogFileMonitorRunner extends MonitorRunner<LogFileMonitor>
		implements TailerListener {
	private static final Log log = LogFactory
			.getLog(LogFileMonitorRunner.class);

	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> future;
	private final List<Pattern> patterns;
	private EventManager eventManager;
	private Tailer tailer;
	private long lastFailure;
	private long lastPass;

	public LogFileMonitorRunner(ScheduledExecutorService executorService,
			LogFileMonitor monitor) {
		this.executorService = executorService;
		this.monitor = monitor;
		this.patterns = new ArrayList<Pattern>();
		for (String pattern : monitor.getPatterns())
			this.patterns.add(Pattern.compile(pattern));
	}

	@Override
	public void startMonitoring(EventManager eventManager) {
		this.eventManager = eventManager;
		log.warn("Checking monitor: " + monitor.getName()
				+ " ----------------------------------------");

		future = this.executorService.schedule(this::setUpMonitoring, 10,
				TimeUnit.SECONDS);
	}

	@Override
	public void stopMonitoring() {
		log.warn("Done checking monitor: " + monitor.getName()
				+ " ----------------------------------------");
		tailer.stop();
		future.cancel(true);
	}

	private void setUpMonitoring() {
		tailer = new Tailer(new File(this.monitor.getLogFile()), this, 500);
		tailer.run();
	}

	@Override
	public void handle(String line) {
		long now = System.currentTimeMillis();

		for (Pattern pattern : patterns) {
			if (pattern.matcher(line).matches()) {
				eventManager.logMonitor(monitor, LogType.FAILED,
						monitor.getLogFile() + " contained pattern match for "
								+ pattern);
				lastFailure = now;
			}
		}

		// We don't want to create 1000s of PASSED logs, but we do want to
		// create them regularly
		if ((now - lastFailure) > monitor.getRequiredStablePeriod()
				&& (now - lastPass) > monitor.getRequiredStablePeriod()) {
			eventManager.logMonitor(monitor, LogType.PASSED, null);
			lastPass = now;
		}
	}

	@Override
	public void init(Tailer tailer) {
	}

	@Override
	public void fileNotFound() {
		this.eventManager.logMonitor(monitor, LogType.ERROR,
				"Couldn't find log file: " + this.monitor.getLogFile());
	}

	@Override
	public void fileRotated() {
	}

	@Override
	public void handle(Exception ex) {
		this.eventManager.logMonitor(monitor, LogType.ERROR,
				"Couldn't read log file: " + this.monitor.getLogFile());
	}
}
