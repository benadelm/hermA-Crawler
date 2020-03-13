/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import herma.crawler.config.Configuration;

public class HttpClientSetup {
	
	private static final String SOCKET_TIMEOUT_KEY = "socketTimeout";
	private static final int DEFAULT_SOCKET_TIMEOUT = 120000; // TODO großzügig
	
	public static HttpClientBuilder setupHttpClient(final Configuration config) {
		final HttpClientBuilder builder = initHttpClientBuilder();
		setupHttpClient(config, builder);
		return builder;
	}
	
	public static HttpClientBuilder initHttpClientBuilder() {
		final HttpClientBuilder builder = HttpClientBuilder.create();
		
		builder.disableCookieManagement();
		builder.disableRedirectHandling();
		
		builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
		builder.setSSLContext(setupSSLContext());
		
		return builder;
	}
	
	public static void setupHttpClient(final Configuration config, final HttpClientBuilder builder) {
		final int socketTimeoutMilliseconds;
		try {
			socketTimeoutMilliseconds = config.getInt(SOCKET_TIMEOUT_KEY, DEFAULT_SOCKET_TIMEOUT);
		} catch (final ParseException e) {
			return;
		}
		builder.setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(socketTimeoutMilliseconds).build());
	}
	
	private static SSLContext setupSSLContext() {
		try {
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null);
			
			return SSLContextBuilder.create()
					.loadTrustMaterial(trustStore, CredulousTrustStrategy.INSTANCE)
					.build();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class CredulousTrustStrategy implements TrustStrategy {
		
		public static final CredulousTrustStrategy INSTANCE = new CredulousTrustStrategy();
		
		@Override
		public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			return true;
		}
		
	}
	
}
