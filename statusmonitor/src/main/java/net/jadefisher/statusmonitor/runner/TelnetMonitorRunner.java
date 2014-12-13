package net.jadefisher.statusmonitor.runner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.model.LogType;
import net.jadefisher.statusmonitor.model.TelnetMonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelnetMonitorRunner extends MonitorRunner<TelnetMonitor> {
	private static final Log log = LogFactory.getLog(TelnetMonitorRunner.class);

	private ScheduledFuture<?> future;
	private EventManager eventManager;
	private ScheduledExecutorService executorService;

	public TelnetMonitorRunner(ScheduledExecutorService executorService,
			TelnetMonitor monitor) {
		this.executorService = executorService;
		this.monitor = monitor;
	}

	@Override
	public void startMonitoring(EventManager eventManager) {
		this.eventManager = eventManager;
		log.info("Monitoring " + this.monitor.getTargetHost() + ":"
				+ this.monitor.getTargetPort());
		this.future = executorService.scheduleAtFixedRate(this::runMonitor, 5,
				20, TimeUnit.SECONDS);
	}

	@Override
	public void stopMonitoring() {
		this.future.cancel(true);
	}

	private void runMonitor() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(this.monitor.getTargetHost(),
					this.monitor.getTargetPort()));
			eventManager.logMonitor(monitor, LogType.PASSED,
					"Connection okay to " + monitor.getTargetHost() + ":"
							+ monitor.getTargetPort());
		} catch (IOException e) {
			eventManager.logMonitor(monitor, LogType.FAILED,
					"Connection refused to " + monitor.getTargetHost() + ":"
							+ monitor.getTargetPort());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				log.warn("Exception closing socket");
			}
		}
	}
}
