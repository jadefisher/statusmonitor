package net.jadefisher.statusmonitor.model;

import java.util.List;

public class LogFileMonitor extends Monitor {
	private String logFile;

	private List<String> patterns;

	private int requiredStablePeriod;

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}

	public int getRequiredStablePeriod() {
		return requiredStablePeriod;
	}

	public void setRequiredStablePeriod(int requiredStablePeriod) {
		this.requiredStablePeriod = requiredStablePeriod;
	}
}
