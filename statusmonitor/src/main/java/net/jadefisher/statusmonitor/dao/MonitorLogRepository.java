package net.jadefisher.statusmonitor.dao;

import net.jadefisher.statusmonitor.model.MonitorLogEntry;

public interface MonitorLogRepository {
	void appendMonitorLog(MonitorLogEntry logEntry);
}
