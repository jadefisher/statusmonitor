package net.jadefisher.statusmonitor.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.jadefisher.statusmonitor.event.EventManager;
import net.jadefisher.statusmonitor.exception.AssertionFailedException;
import net.jadefisher.statusmonitor.model.EndPointMonitor;
import net.jadefisher.statusmonitor.model.HttpRequestDefinition;
import net.jadefisher.statusmonitor.model.LogType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class EndPointMonitorRunner extends MonitorRunner<EndPointMonitor> {
	private static final Log log = LogFactory
			.getLog(EndPointMonitorRunner.class);

	private ScheduledFuture<?> future;
	private EventManager eventManager;
	private PoolingHttpClientConnectionManager cmgr;
	private ScheduledExecutorService executorService;
	private CloseableHttpClient httpclient;
	private BasicCookieStore cookieStore;

	public EndPointMonitorRunner(PoolingHttpClientConnectionManager cmgr,
			ScheduledExecutorService executorService, EndPointMonitor monitor) {
		this.cmgr = cmgr;
		this.executorService = executorService;
		this.monitor = monitor;
	}

	@Override
	public void startMonitoring(EventManager eventManager) {
		this.eventManager = eventManager;
		cookieStore = new BasicCookieStore();
		httpclient = HttpClients.custom().setConnectionManager(cmgr)
				.setDefaultCookieStore(cookieStore).build();

		this.future = executorService.scheduleAtFixedRate(this::runMonitor, 5,
				20, TimeUnit.SECONDS);
	}

	private void runMonitor() {
		log.info("Checking monitor: " + this.monitor.getName()
				+ " ----------------------------------------");

		cookieStore.clear();
		Map<String, String> headers = new HashMap<String, String>();

		if (this.monitor.getFormAuthentication() != null) {
			executeAndMonitorRequest(httpclient, this.monitor
					.getFormAuthentication().getLogonRequest(), headers);
			log.debug("authenticated with form authentication");
		} else if (this.monitor.getBasicAuthentication() != null) {
			String authString = this.monitor.getBasicAuthentication()
					.getUsername()
					+ ":"
					+ this.monitor.getBasicAuthentication().getPassword();
			String encoding = Base64.encodeBase64String(authString.getBytes());
			headers.put("Authorization", "Basic " + encoding);
		}

		for (HttpRequestDefinition req : this.monitor.getRequests()) {
			executeAndMonitorRequest(httpclient, req, headers);
		}

		if (this.monitor.getFormAuthentication() != null
				&& this.monitor.getFormAuthentication().getLogoffRequest() != null) {
			executeAndMonitorRequest(httpclient, this.monitor
					.getFormAuthentication().getLogoffRequest(), headers);
			log.debug("logged off form authentication");
		}

		log.info("finished checking " + this.monitor.getName()
				+ " ----------------------------------------");
	}

	@Override
	public void stopMonitoring() {
		log.info("stopping monitoring " + this.monitor.getName());
		future.cancel(true);
		try {
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void executeAndMonitorRequest(CloseableHttpClient client,
			HttpRequestDefinition def, Map<String, String> headers) {
		try {
			log.debug("running request: " + def.getUrl());
			executeRequest(client, def, headers);

			eventManager.logMonitor(monitor, LogType.PASSED, null);
		} catch (AssertionFailedException e) {
			eventManager.logMonitor(monitor, LogType.FAILED, e.getMessage());
		} catch (URISyntaxException | IOException e) {
			eventManager.logMonitor(monitor, LogType.ERROR, e.getMessage());
			e.printStackTrace();
		} catch (RuntimeException e) {
			eventManager.logMonitor(monitor, LogType.ERROR, e.getMessage());
			e.printStackTrace();
		}
	}

	private static void executeRequest(CloseableHttpClient client,
			HttpRequestDefinition def, Map<String, String> headers)
			throws URISyntaxException, IOException, AssertionFailedException {

		HttpUriRequest request = createRequest(def);
		log.debug("request: " + request);
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				request.addHeader(header.getKey(), header.getValue());
			}
		}
		if (def.getHeaders() != null) {
			for (Map.Entry<String, String> header : def.getHeaders().entrySet()) {
				request.addHeader(header.getKey(), header.getValue());
			}
		}

		CloseableHttpResponse response = client.execute(request);

		try {
			HttpEntity entity = response.getEntity();
			int responseCode = response.getStatusLine().getStatusCode();
			if (def.getExpectedResponseCodes() != null
					&& !def.getExpectedResponseCodes().contains(responseCode)) {
				throw new AssertionFailedException(
						"Response code check failed. Received " + responseCode
								+ " but expected one of "
								+ def.getExpectedResponseCodes());
			}

			log.debug("request get: " + response.getStatusLine()
					+ " with response: " + entity);

			if (def.getExpectedResponseContentType() != null
					&& !entity.getContentType().getValue()
							.contains(def.getExpectedResponseContentType())) {
				throw new AssertionFailedException(
						"Response content type check failed. Received "
								+ entity.getContentType().getValue()
								+ " but expected "
								+ def.getExpectedResponseContentType());
			}

			if (def.getExpectedResponsePatterns() != null) {
				InputStreamReader serverInput = new InputStreamReader(
						entity.getContent());
				BufferedReader reader = new BufferedReader(serverInput);
				StringBuilder responseBuffer = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuffer.append(line);
				}
				String responseText = responseBuffer.toString();

				for (String patternStr : def.getExpectedResponsePatterns()) {
					if (!responseText.matches(patternStr)) {
						throw new AssertionFailedException(
								"Response content check failed. Received "
										+ responseText
										+ " but which didn't match "
										+ patternStr);
					}
				}
			}
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
	}

	private static HttpUriRequest createRequest(HttpRequestDefinition def)
			throws URISyntaxException {
		RequestBuilder builder = null;
		switch (def.getMethod()) {
		case POST:
			builder = RequestBuilder.post();
			break;
		case GET:
			builder = RequestBuilder.get();
			break;
		case PUT:
			builder = RequestBuilder.put();
			break;
		case DELETE:
			builder = RequestBuilder.delete();
			break;
		case HEAD:
			builder = RequestBuilder.head();
			break;
		default:
			log.error("Don't know method: " + def.getMethod());
		}

		builder.setUri(new URI(def.getUrl()));
		if (def.getParameters() != null) {
			for (Map.Entry<String, String> param : def.getParameters()
					.entrySet()) {
				builder.addParameter(param.getKey(), param.getValue());
			}
		}
		return builder.build();
	}
}
