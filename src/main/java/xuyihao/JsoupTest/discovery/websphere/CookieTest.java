package xuyihao.JsoupTest.discovery.websphere;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xuyihao.JsoupTest.util.CommonUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xuyihao.JsoupTest.util.HttpUtil.createSSLInsecureClient;

/**
 * 
 * @Author Xuyh created at 2016年11月22日 下午3:30:10
 *
 */
public class CookieTest {
	private static HashMap<String, String> cookieMap = new HashMap<>();

	public static void main(String[] args) {
		CommonUtils.output("Input ip and port and userName and userPassword:");
		String ip = CommonUtils.input();
		String port = CommonUtils.input();
		String userName = CommonUtils.input();
		String userPassword = CommonUtils.input();
		//-----------------------------------------------------------------------------------------------------logon.jsp
		String responseLogin = executeLoginGet("https://" + ip + ":" + port + "/ibm/console/logon.jsp");
		CommonUtils.output(
				"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------logon.jsp\r\n\r\n"
						+ responseLogin);
		//-----------------------------------------------------------------------------------------------------j_security_check
		Map<String, String> verifyParams = new HashMap<>();
		verifyParams.put("j_username", userName);
		verifyParams.put("j_password", userPassword);
		String responseSecurityCheck = executeLoginPost("https://" + ip + ":" + port + "/ibm/console/j_security_check",
				verifyParams);
		CommonUtils.output(
				"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------j_security_check\r\n\r\n"
						+ responseSecurityCheck);
		//-----------------------------------------------------------------------------------------------------/ibm/console
		String responseConsole = executeLoginGet("https://" + ip + ":" + port + "/ibm/console/");
		CommonUtils.output(
				"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------/ibm/console\r\n\r\n"
						+ responseConsole);
		//-----------------------------------------------------------------------------------------------------/ibm/console/login.do?action=secure
		//-----------------------------------------------------------------------------------------------------/ibm/console/secure/securelogon.do
		//这个请求发送之后可能会有强制登出上一个会话界面返回
		String str = executeLoginGet("https://" + ip + ":" + port + "/ibm/console/login.do?action=secure");
		CommonUtils.output(
				"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------/ibm/console/login.do?action=secure\r\n\r\n"
						+ str);
		Document checked = Jsoup.parse(str);
		Element forceRadio = checked.getElementById("forceradio");
		Element submit = checked.getElementsByAttributeValue("name", "submit").last();
		Element csrfid = checked.getElementsByAttributeValue("name", "csrfid").last();//8.5
		if (forceRadio != null && submit != null) {//会话未关闭
			Map<String, String> forceLoginParams = new HashMap<>();
			forceLoginParams.put("action", forceRadio.attr("value"));
			forceLoginParams.put("submit", submit.attr("value"));
			if (csrfid != null) {
				forceLoginParams.put("csrfid", csrfid.attr("value"));
			}
			String responseForceLogin = executeLoginPost("https://" + ip + ":" + port + "/ibm/console/secure/securelogon.do",
					forceLoginParams);
			CommonUtils.output(
					"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------/ibm/console/secure/securelogon.do\r\n\r\n"
							+ responseForceLogin);
		}
		//------------------------------------------------------------------------------------------------------test if successful
		String responseTest = executeLoginGet("https://" + ip + ":" + port + "/ibm/console/nsc.do");
		CommonUtils.output(
				"\r\n\r\n\r\n-----------------------------------------------------------------------------------------------------test if successful\r\n\r\n"
						+ responseTest);
	}

	private static String executeLoginGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfig4 = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGet.setConfig(requestConfig4);
		CloseableHttpClient httpClient4 = null;
		String str = "";
		try {
			httpClient4 = createSSLInsecureClient();
			HttpClientContext context4 = HttpClientContext.create();
			CloseableHttpResponse response = httpClient4.execute(httpGet, context4);
			getCookiesFromCookieStore(context4.getCookieStore(), cookieMap);
			int state = response.getStatusLine().getStatusCode();
			if (state == 404) {
				str = "";
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
			try {
				if (httpClient4 != null)
					httpClient4.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	private static String executeLoginPost(String url, Map<String, String> params) {
		String re = "";
		HttpPost forceLogin = new HttpPost(url);
		List<NameValuePair> paramsRe = new ArrayList<>();
		for (String key : params.keySet()) {
			paramsRe.add(new BasicNameValuePair(key, params.get(key)));
		}
		RequestConfig requestConfigRe = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		forceLogin.setHeader("Cookie", convertCookieMapToString(cookieMap));
		forceLogin.setConfig(requestConfigRe);
		CloseableHttpResponse response;
		try {
			CloseableHttpClient httpClientRe = createSSLInsecureClient();
			HttpClientContext contextRe;
			contextRe = HttpClientContext.create();
			forceLogin.setEntity(new UrlEncodedFormEntity(paramsRe));
			response = httpClientRe.execute(forceLogin, contextRe);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				re = EntityUtils.toString(entity, "utf-8");
			}
			getCookiesFromCookieStore(contextRe.getCookieStore(), cookieMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	private static void getCookiesFromCookieStore(CookieStore cookieStore, Map<String, String> cookieMap) {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
	}

	private static String convertCookieMapToString(Map<String, String> map) {
		String cookie = "";
		for (String key : map.keySet()) {
			cookie += (key + "=" + map.get(key) + "; ");
		}
		if (map.size() > 0) {
			cookie = cookie.substring(0, cookie.length() - 2);
		}
		return cookie;
	}
}
