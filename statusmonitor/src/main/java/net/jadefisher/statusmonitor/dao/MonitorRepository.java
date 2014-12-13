package net.jadefisher.statusmonitor.dao;

import java.util.List;

import net.jadefisher.statusmonitor.model.Monitor;

public interface MonitorRepository {

	List<Monitor> getMonitors();
}
