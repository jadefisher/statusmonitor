package net.jadefisher.statusmonitor.event;

import net.jadefisher.statusmonitor.dao.MonitorLogRepository;
import net.jadefisher.statusmonitor.model.LogType;
import net.jadefisher.statusmonitor.model.Monitor;
import net.jadefisher.statusmonitor.model.MonitorLogEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventManagerImpl implements EventManager {

	@Autowired
	private MonitorLogRepository monitorLogRepository;

	@Override
	public void logMonitor(Monitor monitor, LogType type, String message) {
		MonitorLogEntry logEntry = new MonitorLogEntry(monitor, message, type);

		monitorLogRepository.appendMonitorLog(logEntry);
	}
}
