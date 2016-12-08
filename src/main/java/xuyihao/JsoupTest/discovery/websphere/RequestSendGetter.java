package xuyihao.JsoupTest.discovery.websphere;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import xuyihao.JsoupTest.util.HttpUtil;

import static xuyihao.JsoupTest.util.HttpUtil.createSSLInsecureClient;

/**
 * 
 * @Author Xuyh created at 2016年11月10日 上午9:49:09
 *
 */
public class RequestSendGetter {
	private String ip;
	private String port;
	private String userName;
	private String password;

	private Map<String, String> cookieMap = new HashMap<>();
	private boolean isLogin;

	//是否通过https方式登录，默认true
	private boolean isLoginWithHttps = true;

	public RequestSendGetter(String ip, String port, String userName, String password) {
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * 免密码登录
	 * 
	 * @return
	   */
	public boolean loginWithoutPassword() {
		boolean flag = true;
		//-----------------------------------------------------------------------------------------------------/ibm/console/
		String consoleResponse = executeGet("http://" + ip + ":" + port + "/ibm/console/");
		Document consoleResponseDoc = Jsoup.parse(consoleResponse);
		Element submit = consoleResponseDoc.getElementsByAttributeValue("name", "submit").last();
		Map<String, String> verifyParams = new HashMap<>();
		if (submit != null) {
			verifyParams.put("submit", submit.attr("value"));
			verifyParams.put("username", userName);
		} else {
			return false;
		}
		//-----------------------------------------------------------------------------------------------------/ibm/console/login.do
		String str = executePost("http://" + ip + ":" + port + "/ibm/console/login.do", verifyParams);
		Document checked = Jsoup.parse(str);
		Element forceRadio = checked.getElementById("forceradio");
		Element submit2 = checked.getElementsByAttributeValue("name", "submit").last();
		Element csrfid = checked.getElementsByAttributeValue("name", "csrfid").last();//8.5
		//-----------------------------------------------------------------------------------------------------/ibm/console/login.do
		if (forceRadio != null && submit2 != null) {//会话未关闭(强制重新登录)
			Map<String, String> forceLoginParams = new HashMap<>();
			forceLoginParams.put("action", forceRadio.attr("value"));
			forceLoginParams.put("submit", submit.attr("value"));
			if (csrfid != null) {
				forceLoginParams.put("csrfid", csrfid.attr("value"));
			}
			executePost("http://" + ip + ":" + port + "/ibm/console/login.do", forceLoginParams);
		}
		if (flag) {
			isLogin = true;
			isLoginWithHttps = false;
		}
		return flag;
	}

	/**
	 * 通过用户名密码登录
	 *
	 * @return
	   */
	public boolean loginWithPassword() {
		boolean flag = true;
		//-----------------------------------------------------------------------------------------------------logon.jsp
		executeGetWithSSL("https://" + ip + ":" + port + "/ibm/console/logon.jsp");
		//-----------------------------------------------------------------------------------------------------j_security_check
		Map<String, String> verifyParams = new HashMap<>();
		verifyParams.put("j_username", userName);
		verifyParams.put("j_password", password);
		executePostWithSSL("https://" + ip + ":" + port + "/ibm/console/j_security_check", verifyParams);
		//-----------------------------------------------------------------------------------------------------/ibm/console
		executeGetWithSSL("https://" + ip + ":" + port + "/ibm/console/");
		//-----------------------------------------------------------------------------------------------------/ibm/console/login.do?action=secure
		String str = executeGetWithSSL("https://" + ip + ":" + port + "/ibm/console/login.do?action=secure");
		Document checked = Jsoup.parse(str);
		Element forceRadio = checked.getElementById("forceradio");
		Element submit = checked.getElementsByAttributeValue("name", "submit").last();
		Element csrfid = checked.getElementsByAttributeValue("name", "csrfid").last();//8.5
		//-----------------------------------------------------------------------------------------------------/ibm/console/secure/securelogon.do
		if (forceRadio != null && submit != null) {//会话未关闭(强制重新登录)
			Map<String, String> forceLoginParams = new HashMap<>();
			forceLoginParams.put("action", forceRadio.attr("value"));
			forceLoginParams.put("submit", submit.attr("value"));
			if (csrfid != null) {
				forceLoginParams.put("csrfid", csrfid.attr("value"));
			}
			executePostWithSSL("https://" + ip + ":" + port + "/ibm/console/secure/securelogon.do", forceLoginParams);
		}
		if (flag) {
			isLogin = true;
			isLoginWithHttps = true;
		}
		return flag;
	}

	/**
	 * 登录（改进）
	 *
	 * @return
	   */
	public boolean login() {
		if (ip == null || ip.equals("")) {
			return false;
		}
		if (port == null || port.equals("")) {
			return false;
		}
		if (userName == null || userName.equals("")) {
			return false;
		}
		if (password == null || password.equals("")) {
			return loginWithoutPassword();
		} else {
			return loginWithPassword();
		}
	}

	/**
	 * 从URL获取html响应
	 * 
	 * @param suffixUrl
	 * @return
	 */
	public String getHtmlResp(String suffixUrl) {
		if (isLogin) {
			String fullUrl;
			if (isLoginWithHttps) {
				fullUrl = "https://" + ip + ":" + port + suffixUrl;
				return executeGetWithSSL(fullUrl);
			} else {
				fullUrl = "http://" + ip + ":" + port + suffixUrl;
				return executeGet(fullUrl);
			}
		} else {
			return "";
		}
	}

	/**
	 * 将html文本转换成文档
	 * 
	 * @param html
	 * @return
	 */
	public Document parseHtmlToDoc(String html) {
		Document doc;
		try {
			doc = removeHtmlSpace(html);
		} catch (Exception e) {
			return null;
		}
		return doc;
	}

	/**
	 * 将HTML页面中的 " & n b s p " 空格符去除并转换成Document
	 *
	 * @param str
	 * @return
	   */
	public Document removeHtmlSpace(String str) {
		Document doc = Jsoup.parse(str);
		String result = doc.html().replace("&nbsp;", "");
		return Jsoup.parse(result);
	}

	/**
	 * 判断URL是否可连接
	 * 
	 * @return
	 */
	public boolean isExist() {
		if (password == null || password.equals("")) {
			String urlWithoutHttps = "http://" + ip + ":" + port + "/ibm/console/logon.jsp";
			CloseableHttpClient httpClient;
			HttpClientContext context;
			HttpGet httpGet = new HttpGet(urlWithoutHttps);
			try {
				httpClient = HttpClientBuilder.create().build();
				context = HttpClientContext.create();
				httpClient.execute(httpGet, context);
				getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
			} catch (Exception e) {
				return false;
			}
		} else {
			String url = "https://" + ip + ":" + port + "/ibm/console/logon.jsp";
			CloseableHttpClient httpClient;
			HttpClientContext context;
			HttpGet httpGet = new HttpGet(url);
			try {
				httpClient = HttpUtil.createSSLInsecureClient();
				context = HttpClientContext.create();
				httpClient.execute(httpGet, context);
				getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private String executeGet(String url){
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGet.setConfig(requestConfig);
		CloseableHttpClient httpClient = null;
		String str = "";
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpClientContext context = HttpClientContext.create();
			CloseableHttpResponse response = httpClient.execute(httpGet, context);
			getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
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
		} finally {
			try {
				if (httpClient != null)
					httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	private String executeGetWithSSL(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGet.setConfig(requestConfig);
		CloseableHttpClient httpClient = null;
		String str = "";
		try {
			httpClient = HttpUtil.createSSLInsecureClient();
			HttpClientContext context = HttpClientContext.create();
			CloseableHttpResponse response = httpClient.execute(httpGet, context);
			getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
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
				if (httpClient != null)
					httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	private String executePost(String url, Map<String, String> params){
		String reStr = "";
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpPost.setConfig(requestConfig);
		httpPost.setHeader("Cookie", convertCookieMapToString(cookieMap));
		List<NameValuePair> paramsRe = new ArrayList<>();
		for (String key : params.keySet()) {
			paramsRe.add(new BasicNameValuePair(key, params.get(key)));
		}
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		CloseableHttpResponse response;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(paramsRe));
			HttpClientContext context = HttpClientContext.create();
			response = httpclient.execute(httpPost, context);
			getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
			HttpEntity entity = response.getEntity();
			reStr = EntityUtils.toString(entity, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		return reStr;
	}

	private String executePostWithSSL(String url, Map<String, String> params) {
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
		CloseableHttpClient httpClientRe;
		try {
			httpClientRe = createSSLInsecureClient();
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

	private void getCookiesFromCookieStore(CookieStore cookieStore, Map<String, String> cookieMap) {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
	}

	private String convertCookieMapToString(Map<String, String> map) {
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
