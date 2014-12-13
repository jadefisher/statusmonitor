package net.jadefisher.statusmonitor.dao;

import java.text.SimpleDateFormat;

import net.jadefisher.statusmonitor.model.MonitorLogEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MonitorLogRepositoryImpl implements MonitorLogRepository {
	private static final Log log = LogFactory
			.getLog(MonitorLogRepositoryImpl.class);

	@Override
	public void appendMonitorLog(MonitorLogEntry logEntry) {
		try {
			String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
					.format(logEntry.getCreatedDate());
			log.warn(dateStr + " " + logEntry.getLogType() + " "
					+ logEntry.getServiceId() + " - " + logEntry.getMonitorId()
					+ " - " + logEntry.getMessage());
		} catch (Exception e) {
			log.warn("exception logging monitor log", e);
		}
	}
}
