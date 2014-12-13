package net.jadefisher.statusmonitor.runner;

import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.model.Monitor;

public abstract class MonitorRunner<T extends Monitor> {

	protected T monitor;

	public abstract void startMonitoring(EventManager eventManager);

	public abstract void stopMonitoring();
}
