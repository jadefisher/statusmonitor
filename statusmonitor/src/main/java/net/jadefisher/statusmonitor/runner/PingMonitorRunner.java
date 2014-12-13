package net.jadefisher.statusmonitor.runner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.model.LogType;
import net.jadefisher.statusmonitor.model.PingMonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PingMonitorRunner extends MonitorRunner<PingMonitor> {
	private static final Log log = LogFactory.getLog(PingMonitorRunner.class);

	private ScheduledFuture<?> future;
	private EventManager eventManager;
	private ScheduledExecutorService executorService;

	public PingMonitorRunner(ScheduledExecutorService executorService,
			PingMonitor monitor) {
		this.executorService = executorService;
		this.monitor = monitor;
	}

	@Override
	public void startMonitoring(EventManager eventManager) {
		this.eventManager = eventManager;
		log.info("Monitoring " + this.monitor.getTargetHost());
		this.future = executorService.scheduleAtFixedRate(this::runMonitor, 5,
				20, TimeUnit.SECONDS);
	}

	@Override
	public void stopMonitoring() {
		this.future.cancel(true);
	}

	private void runMonitor() {
		try {
			InetAddress.getByName(monitor.getTargetHost()).isReachable(
					monitor.getPingTimeout());
			eventManager.logMonitor(monitor, LogType.PASSED,
					monitor.getTargetHost() + " is reachable");
		} catch (IOException e) {
			eventManager.logMonitor(monitor, LogType.FAILED,
					monitor.getTargetHost() + " is unreachable");
		}
	}
}
