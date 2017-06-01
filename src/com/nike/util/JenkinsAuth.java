/*
 * JenkinsAuth untility class does the Jenkins authentication that is needed to trigger the build on Jenkins.
 * 	-> authentication for Jenkins and 
 * 	-> does the build trigger by using the Web Service of Jenkins
 * This class is used by this application web service when there is a call/request for signing of any given ipa.
 * 
 *
 * 
 */
package com.nike.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.listeners.PushProfilesValidityCheckListener;

/**
 * Simple class to launch a jenkins build on run@Cloud platform, should also work on every jenkins instance (not tested)
 *
 */
public class JenkinsAuth {
	
	static Logger logger =  LogManager.getLogger(JenkinsAuth.class.getName());

	public static void triggerJenkinsBuild(String projectName, String IS_PUSH,
			String APP_REQUEST_TYPE, String VERSION, String APP_TITLE,
			ServletContext servletContext, String BundleID) {


		// Create your httpclient
		DefaultHttpClient client = new DefaultHttpClient();

		// Then provide the right credentials
		client.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(PushProfilesValidityCheckListener.JENKINS_USER_NAME, PushProfilesValidityCheckListener.JENKINS_PWD));

		// Generate BASIC scheme object and stick it to the execution context
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);

		// Add as the first (because of the zero) request interceptor
		// It will first intercept the request and preemptively initialize the
		// authentication scheme if there is not
		client.addRequestInterceptor(new PreemptiveAuth(), 0);

		String clientURL = TableConstants.ROOT_URL_WITH_JENKINS_PORT_NUMBER
				+ TableConstants.PROJECT_URL_PART + "Public/UpdateJobResult";
		String finalFolderName = projectName + "&APPNAME=" + APP_TITLE;
		// You get request that will start the build
		String getUrl = PushProfilesValidityCheckListener.JENKINS_URL + "/job/" + PushProfilesValidityCheckListener.JENKINS_JOB_NAME
				+ "/buildWithParameters?token=" + PushProfilesValidityCheckListener.JENKINS_TOKEN 
				+ "&FOLDERNAME=" + finalFolderName
				+ "&CLIENT_URL=" + clientURL
				+ "&APP_REQUEST_TYPE=" + URLEncoder.encode(APP_REQUEST_TYPE)
				+ "&VERSION=" + URLEncoder.encode(VERSION)
				+ "&IS_PUSH=" + URLEncoder.encode(IS_PUSH)
				+ "&BUNDLE_ID=" + URLEncoder.encode(BundleID);
		getUrl = getUrl.replaceAll(" ", "%20");
		
		//			getUrl = URLEncoder.encode(getUrl, "UTF-8");
					if (logger.isDebugEnabled()) {
						logger.debug(getUrl);
					} else {
						logger.debug(getUrl);
					}
					HttpGet get = new HttpGet(getUrl);
		
					try {
						// Execute your request with the given context
						HttpResponse response = client.execute(get, context);
						HttpEntity entity = response.getEntity();
						EntityUtils.consume(entity);
					} catch (IOException e) {
						logger.error("IOException in making a HTTP call to Jenkins "
								+ ErrorUtils.getStackTrace(e));
					}
	}
	/**
	 * Preemptive authentication interceptor
	 *
	 */
	static class PreemptiveAuth implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
		 * org.apache.http.protocol.HttpContext)
		 */
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			// Get the AuthState
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

		}

	}
}
