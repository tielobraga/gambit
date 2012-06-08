package app.android.gambit.remote;


import java.io.IOException;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;


// This class methods will throw UnauthorizedException if auth token passed is invalid,
// and FailedRequestException if internet connection is unavailable.

public class GoogleDocsClient
{
	private static final int UNAUTHORIZED_STATUS_CODE = 401;

	private static final int CONTENT_COMPRESSION_LOWER_LIMIT = 256;

	private static final XmlNamespaceDictionary DICTIONARY;
	static {
		DICTIONARY = new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom")
			.set("app", "http://www.w3.org/2007/app")
			.set("batch", "http://schemas.google.com/gdata/batch")
			.set("docs", "http://schemas.google.com/docs/2007")
			.set("gAcl", "http://schemas.google.com/acl/2007")
			.set("gd", "http://schemas.google.com/g/2005")
			.set("gs", "http://schemas.google.com/spreadsheets/2006")
			.set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
			.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
			.set("xml", "http://www.w3.org/XML/1998/namespace");
	}

	private final String authToken;
	private HttpRequestFactory requestFactory;

	public GoogleDocsClient(String authToken) {
		this.authToken = authToken;
		setUpRequestFactory();
	}

	private void setUpRequestFactory() {
		HttpTransport transport = new NetHttpTransport();
		requestFactory = transport.createRequestFactory(new RequestInitializer());
	}

	private class RequestInitializer implements HttpRequestInitializer
	{
		private static final String GDATA_VERSION = "3.0";

		@Override
		public void initialize(HttpRequest request) throws IOException {
			request.setHeaders(buildHeaders());
			request.addParser(new AtomParser(DICTIONARY));
		}

		private GoogleHeaders buildHeaders() {
			GoogleHeaders headers = new GoogleHeaders();

			headers.setGDataVersion(GDATA_VERSION);
			headers.setGoogleLogin(authToken);

			return headers;
		}
	}

	protected XmlNamespaceDictionary getXmlNamespaceDictionary() {
		return DICTIONARY;
	}

	protected HttpRequest buildGetRequest(GoogleUrl url) {
		try {
			return requestFactory.buildGetRequest(url).setInterceptor(compressionSwitcher);
		}
		catch (IOException e) {
			throw new SyncException(e);
		}
	}

	private final HttpExecuteInterceptor compressionSwitcher = new HttpExecuteInterceptor()
	{
		@Override
		public void intercept(HttpRequest httpRequest) throws IOException {
			if (httpRequest.getContent() != null) {
				if (httpRequest.getContent().getLength() >= CONTENT_COMPRESSION_LOWER_LIMIT) {
					httpRequest.setEnableGZipContent(true);
				}
			}
		}
	};

	protected <T> T processGetRequest(HttpRequest request, Class<T> dataClass) {
		try {
			return processRequest(request).parseAs(dataClass);
		}
		catch (IOException e) {
			throw new FailedRequestException(e);
		}
	}

	protected HttpRequest buildPostRequest(GoogleUrl url, HttpContent content) {
		try {
			return requestFactory.buildPostRequest(url, content).setInterceptor(compressionSwitcher);
		}
		catch (IOException e) {
			throw new SyncException(e);
		}
	}

	protected void processPostRequest(HttpRequest request) {
		processRequest(request);
	}

	protected HttpRequest buildPutRequest(GoogleUrl url, AtomContent content) {
		try {
			HttpRequest request = requestFactory.buildPutRequest(url, content).setInterceptor(compressionSwitcher);
			// Ask server to update regardless the corresponding value has been changed by another
			// client or not
			request.getHeaders().setIfMatch("*");
			return request;
		}
		catch (IOException e) {
			throw new SyncException(e);
		}
	}

	protected void processPutRequest(HttpRequest request) {
		processRequest(request);
	}

	protected HttpRequest buildDeleteRequest(GoogleUrl url) {
		try {
			HttpRequest request = requestFactory.buildDeleteRequest(url).setInterceptor(compressionSwitcher);
			// Ask server to delete regardless the corresponding value has been changed by another
			// client or not
			request.getHeaders().setIfMatch("*");
			return request;
		}
		catch (IOException e) {
			throw new SyncException(e);
		}
	}

	protected void processDeleteRequest(HttpRequest request) {
		processRequest(request);
	}

	private HttpResponse processRequest(HttpRequest request) {
		try {
			return request.execute();
		}
		catch (HttpResponseException e) {
			throw exceptionFromUnsuccessfulStatusCode(e.getStatusCode());
		}
		catch (IOException e) {
			throw new FailedRequestException(e);
		}
	}

	private FailedRequestException exceptionFromUnsuccessfulStatusCode(int statusCode) {
		if (statusCode == UNAUTHORIZED_STATUS_CODE) {
			return new UnauthorizedException();
		}
		else {
			return new FailedRequestException();
		}
	}
}