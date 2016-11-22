package xuyihao.JsoupTest;

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
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.HttpUtil;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		String url = "https://" + ip + ":" + port + "/ibm/console/logon.jsp";
		HttpUtil.getHttpResponse(url);
		CloseableHttpClient httpClient;
		HttpClientContext context;
		HttpGet httpGet = new HttpGet(url);
		try {
			httpClient = createSSLInsecureClient();
			context = HttpClientContext.create();
			httpClient.execute(httpGet, context);
			getCookiesFromCookieStore(context.getCookieStore(), cookieMap);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//-----------------------------------------------------------------------------------------------------j_security_check
		String jSecChkUrl = "https://" + ip + ":" + port + "/ibm/console/j_security_check";
		HttpPost httpPost = new HttpPost(jSecChkUrl);
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("j_username", userName));
		params.add(new BasicNameValuePair("j_password", userPassword));
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
		httpPost.setHeader("Cookie", convertCookieMapToString(cookieMap));
		httpPost.setConfig(requestConfig);
		try {
			CloseableHttpClient httpClient2 = createSSLInsecureClient();
			HttpClientContext context2;
			context2 = HttpClientContext.create();
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpClient2.execute(httpPost, context2);
			getCookiesFromCookieStore(context2.getCookieStore(), cookieMap);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		//-----------------------------------------------------------------------------------------------------/ibm/console
		HttpGet httpGet3 = new HttpGet("https://" + ip + ":" + port + "/ibm/console/");
		httpGet3.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfig3 = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGet3.setConfig(requestConfig3);
		try{
			CloseableHttpClient httpClient3 = createSSLInsecureClient();
			HttpClientContext context3 = HttpClientContext.create();
			httpClient3.execute(httpGet3, context3);
			getCookiesFromCookieStore(context3.getCookieStore(), cookieMap);
		}catch (Exception e3){
			e3.printStackTrace();
		}

		//-----------------------------------------------------------------------------------------------------/ibm/console/login.do?action=secure
		//这个请求发送之后可能会有强制登出上一个会话界面返回
		HttpGet httpGet4 = new HttpGet("https://" + ip + ":" + port + "/ibm/console/login.do?action=secure");
		httpGet4.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfig4 = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGet4.setConfig(requestConfig4);
		CloseableHttpClient httpClient4 = null;
		String str = "";
		try{
			httpClient4 = createSSLInsecureClient();
			HttpClientContext context4 = HttpClientContext.create();
			CloseableHttpResponse response = httpClient4.execute(httpGet4, context4);
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
		Document checked = Jsoup.parse(str);
		Element forceRadio = checked.getElementById("forceradio");
		Element submit = checked.getElementsByAttributeValue("name", "submit").last();
		Element csrfid = checked.getElementsByAttributeValue("name", "csrfid").last();//8.5
		String forceRadioValue = forceRadio.attr("value");
		String submitValue = submit.attr("value");
		String csfidValue = null;
		if(csrfid != null){
			csfidValue = csrfid.attr("value");
		}
		if(forceRadio != null && submit != null){//如果会话未关闭
			String reLoginURL = "https://" + ip + ":" + port + "/ibm/console/j_security_check";
			HttpPost reLogin = new HttpPost(jSecChkUrl);
			List<NameValuePair> paramsRe = new ArrayList<>();
			paramsRe.add(new BasicNameValuePair("action", forceRadioValue));
			paramsRe.add(new BasicNameValuePair("submit", submitValue));
			if(csfidValue != null){
				paramsRe.add(new BasicNameValuePair("csrfid", csfidValue));
			}
			RequestConfig requestConfigRe = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			reLogin.setHeader("Cookie", convertCookieMapToString(cookieMap));
			reLogin.setConfig(requestConfigRe);
			try {
				CloseableHttpClient httpClientRe = createSSLInsecureClient();
				HttpClientContext contextRe;
				contextRe = HttpClientContext.create();
				httpPost.setEntity(new UrlEncodedFormEntity(paramsRe));
				httpClientRe.execute(httpPost, contextRe);
				getCookiesFromCookieStore(contextRe.getCookieStore(), cookieMap);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		//----test if successful
		HttpGet httpGetTest = new HttpGet("https://" + ip + ":" + port + "/ibm/console/nsc.do");
		httpGetTest.setHeader("Cookie", convertCookieMapToString(cookieMap));
		RequestConfig requestConfigTest = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpGetTest.setConfig(requestConfigTest);
		String response = HttpUtil.getHttpResponse(httpGetTest);
		CommonUtils.output(response);
		CommonUtils.output(convertCookieMapToString(cookieMap));
	}

	private static void getCookiesFromCookieStore(CookieStore cookieStore, HashMap<String, String> cookieMap){
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
	}

	private static String convertCookieMapToString(HashMap<String, String> map){
		String cookie = "";
		for(String key : map.keySet()){
			cookie += (key + "=" + map.get(key) + "; ");
		}
		cookie = cookie.substring(0, cookie.length()-2);
		return cookie;
	}
}
