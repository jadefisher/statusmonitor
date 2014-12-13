package net.jadefisher.statusmonitor.event;

import net.jadefisher.statusmonitor.model.LogType;
import net.jadefisher.statusmonitor.model.Monitor;

public interface EventManager {

	void logMonitor(Monitor monitor, LogType type, String message);
}
