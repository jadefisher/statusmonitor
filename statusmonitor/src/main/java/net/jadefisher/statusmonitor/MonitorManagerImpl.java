package net.jadefisher.statusmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.jadefisher.statusmonitor.dao.MonitorRepository;
import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.model.EndPointMonitor;
import net.jadefisher.statusmonitor.model.LogFileMonitor;
import net.jadefisher.statusmonitor.model.Monitor;
import net.jadefisher.statusmonitor.model.PingMonitor;
import net.jadefisher.statusmonitor.model.TelnetMonitor;
import net.jadefisher.statusmonitor.runner.EndPointMonitorRunner;
import net.jadefisher.statusmonitor.runner.LogFileMonitorRunner;
import net.jadefisher.statusmonitor.runner.MonitorRunner;
import net.jadefisher.statusmonitor.runner.PingMonitorRunner;
import net.jadefisher.statusmonitor.runner.TelnetMonitorRunner;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitorManagerImpl implements MonitorManager {
	@Autowired
	private MonitorRepository monitorRepository;

	@Autowired
	private EventManager eventManager;

	@Autowired
	private PoolingHttpClientConnectionManager cmgr;

	@Autowired
	private ScheduledExecutorService scheduledExecutorService;

	private List<MonitorRunner<?>> runners;

	@PostConstruct
	@Override
	public void initialiseMonitoring() {
		System.out.println("hello");
		runners = new ArrayList<MonitorRunner<?>>();
		List<Monitor> monitors = monitorRepository.getMonitors();

		for (Monitor monitor : monitors) {
			MonitorRunner<?> runner = null;
			if (monitor instanceof EndPointMonitor) {
				runner = new EndPointMonitorRunner(cmgr,
						scheduledExecutorService, (EndPointMonitor) monitor);
			} else if (monitor instanceof LogFileMonitor) {
				runner = new LogFileMonitorRunner(scheduledExecutorService,
						(LogFileMonitor) monitor);
			} else if (monitor instanceof TelnetMonitor) {
				runner = new TelnetMonitorRunner(scheduledExecutorService,
						(TelnetMonitor) monitor);
			} else if (monitor instanceof PingMonitor) {
				runner = new PingMonitorRunner(scheduledExecutorService,
						(PingMonitor) monitor);
			}

			if (runner != null) {
				runners.add(runner);
				runner.startMonitoring(eventManager);
			}
		}
	}

	@PreDestroy
	public void shutdown() {
		for (MonitorRunner<?> runner : runners) {
			runner.stopMonitoring();
		}
		this.scheduledExecutorService.shutdown();
	}

}
