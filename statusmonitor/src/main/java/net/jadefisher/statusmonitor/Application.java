package net.jadefisher.statusmonitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	@Value("${statusmonitor.http.connections.maxTotal}")
	private int maxConnections;

	@Value("${statusmonitor.http.connections.defaultMaxPerRoute}")
	private int defaultMaxPerRoute;

	@Value("${statusmonitor.scheduleThreadPoolSize}")
	private int scheduleThreadPoolSize;

	@Bean
	public PoolingHttpClientConnectionManager connectionManager() {
		PoolingHttpClientConnectionManager cmgr = new PoolingHttpClientConnectionManager();
		cmgr.setMaxTotal(maxConnections);
		cmgr.setDefaultMaxPerRoute(defaultMaxPerRoute);
		return cmgr;
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		return Executors.newScheduledThreadPool(scheduleThreadPoolSize);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
