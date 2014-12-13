/*
 * Created on 15/07/2014 by fisherj
 * 
 * Copyright (c) 2005-2014 Public Transport Victoria (PTV)
 * State Government of Victoria, Australia
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of PTV.
 */

package net.jadefisher.statusmonitor.model;

import java.util.List;
import java.util.Map;

public class HttpRequestDefinition {
	private String url;

	private Map<String, String> parameters;

	private Map<String, String> headers;

	private HttpMethod method;

	private List<Integer> expectedResponseCodes;

	private List<String> expectedResponsePatterns;

	private String expectedResponseContentType;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Integer> getExpectedResponseCodes() {
		return expectedResponseCodes;
	}

	public void setExpectedResponseCodes(List<Integer> expectedResponseCodes) {
		this.expectedResponseCodes = expectedResponseCodes;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public List<String> getExpectedResponsePatterns() {
		return expectedResponsePatterns;
	}

	public void setExpectedResponsePatterns(
			List<String> expectedResponsePatterns) {
		this.expectedResponsePatterns = expectedResponsePatterns;
	}

	public String getExpectedResponseContentType() {
		return expectedResponseContentType;
	}

	public void setExpectedResponseContentType(
			String expectedResponseContentType) {
		this.expectedResponseContentType = expectedResponseContentType;
	}
}
