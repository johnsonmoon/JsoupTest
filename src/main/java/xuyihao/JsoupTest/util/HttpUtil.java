package xuyihao.JsoupTest.util;

import org.apache.commons.net.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 
 * @Author Xuyh created at 2016年11月10日 上午9:57:07
 *
 */
public class HttpUtil {
	public static String getHttpResponse(String url) {
		String str = "";
		CloseableHttpClient httpclient = null;
		try {
			if (url.startsWith("https")) {
				httpclient = createSSLInsecureClient();
			} else {
				httpclient = HttpClientBuilder.create().build();
			}
			HttpGet httpget = new HttpGet(url);
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpget.setConfig(requestConfig);

			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);

			int state = response.getStatusLine().getStatusCode();

			if (state == 404) {
				return null;
			}

			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					str = EntityUtils.toString(entity, "UTF-8");

					return str;
				} else {
					return null;
				}

			} finally {
				response.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				if (httpclient != null)
					httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return str;

	}

	public static String getHttpResponse(HttpGet httpGet) {

		String str = "";

		CloseableHttpClient httpclient = null;
		try {

			URI uri = httpGet.getURI();
			if (uri.getScheme().contains("https")) {
				httpclient = createSSLInsecureClient();
			} else {
				httpclient = HttpClientBuilder.create().build();
			}

			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).build();
			httpGet.setConfig(requestConfig);

			CloseableHttpResponse response = httpclient.execute(httpGet);

			int state = response.getStatusLine().getStatusCode();

			if (state == 404) {
				return null;
			}
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					str = EntityUtils.toString(entity, "UTF-8");
				}
			} finally {
				response.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				if (httpclient != null)
					httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return str;

	}

	public static String getDataUseBasicAuth(String path, String userName, String passWord) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(path);
			URLConnection urlConnection = setUsernamePassword(url, userName, passWord);
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();

			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static URLConnection setUsernamePassword(URL url, String userName, String passWord) throws IOException {
		URLConnection urlConnection = url.openConnection();
		String authString = userName + ":" + passWord;
		String authStringEnc = new String(Base64.encodeBase64(authString.getBytes()));
		urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
		return urlConnection;
	}

	public static String getHttpResponse(HttpPost httpPost, List<NameValuePair> params) {

		String reStr = "";
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		CloseableHttpResponse response;

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(params));
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			reStr = EntityUtils.toString(entity, "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		return reStr;
	}

	public static String getSessionId4WebLogic(HttpPost httpPost, List<NameValuePair> params, String sessionName) {
		String sessionId = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpClientContext context;

		try {
			context = HttpClientContext.create();
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpClient.execute(httpPost, context);

			CookieStore cookieStore = context.getCookieStore();
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				String cookieName = cookie.getName();
				String value = cookie.getValue();
				if (cookieName.equalsIgnoreCase(sessionName)) {
					sessionId = value;
					break;
				}

			} // end foreach

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		return sessionId;
	}

	/**
	 * 创建 SSL连接
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();

			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
					new X509HostnameVerifier() {

						public boolean verify(String arg0, SSLSession arg1) {
							return true;
						}

						public void verify(String host, SSLSocket ssl) throws IOException {
						}

						public void verify(String host, X509Certificate cert) throws SSLException {
						}

						public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
						}

					});

			return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();

		} catch (GeneralSecurityException e) {
			throw e;
		}
	}

	public static void main(String[] args) throws IOException, GeneralSecurityException {

		String resp = getHttpResponse("https://10.1.240.161:9043/ibm/console/logon.jsp");

		System.out.println(resp);

		/*
		 * String userName = "wasadmin"; String password = "admin";
		 * 
		 * String jSecChkUrl =
		 * "https://10.1.240.161:9043/ibm/console/j_security_check"; HttpPost
		 * httpPost = new HttpPost(jSecChkUrl); List<NameValuePair> params = new
		 * ArrayList<>(); params.add(new BasicNameValuePair("j_username",
		 * userName)); params.add(new BasicNameValuePair("j_password", password));
		 * 
		 * RequestConfig requestConfig =
		 * RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).
		 * build(); httpPost.setConfig(requestConfig);
		 * 
		 * CloseableHttpClient httpClient = createSSLInsecureClient();
		 * HttpClientContext context;
		 * 
		 * try { context = HttpClientContext.create(); httpPost.setEntity(new
		 * UrlEncodedFormEntity(params)); httpClient.execute(httpPost, context);
		 * 
		 * CookieStore cookieStore = context.getCookieStore(); List<Cookie> cookies
		 * = cookieStore.getCookies(); for (Cookie cookie : cookies) { String
		 * cookieName = cookie.getName(); String value = cookie.getValue(); if
		 * (cookieName.equalsIgnoreCase("LtpaToken")) {
		 * 
		 * }else if(cookieName.equalsIgnoreCase("LtpaToken2")) {
		 * 
		 * }
		 * 
		 * } // end foreach
		 * 
		 * }catch (Exception e){ e.printStackTrace(); }
		 */
	}
}
