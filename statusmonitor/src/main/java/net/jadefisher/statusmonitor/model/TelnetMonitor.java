package net.jadefisher.statusmonitor.model;

public class TelnetMonitor extends Monitor {
	private String targetHost;

	private int targetPort;

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

}
