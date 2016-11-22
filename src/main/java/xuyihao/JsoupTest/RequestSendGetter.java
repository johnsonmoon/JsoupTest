package xuyihao.JsoupTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xuyihao.JsoupTest.util.HttpUtil;

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

	private String ltpaToken;
	private String ltpaToken2;
	private boolean isLogon;
	private String jSessionId;
	private String allCookie;

	public RequestSendGetter(String ip, String port, String userName, String password) {
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public enum WebSphereVersion {
		six, seven, eight, unknown
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	public boolean logon() {
		String jSecChkUrl = "https://" + ip + ":" + port + "/ibm/console/j_security_check";
		HttpPost httpPost = new HttpPost(jSecChkUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("j_username", userName));
		params.add(new BasicNameValuePair("j_password", password));
		String initCookie = "JSESSIONID=" + jSessionId;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpPost.setHeader("Cookie", initCookie);
		httpPost.setConfig(requestConfig);
		try {
			CloseableHttpClient httpClient = HttpUtil.createSSLInsecureClient();
			HttpClientContext context;
			context = HttpClientContext.create();
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpClient.execute(httpPost, context);

			CookieStore cookieStore = context.getCookieStore();
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				String cookieName = cookie.getName();
				String value = cookie.getValue();
				if (cookieName.equalsIgnoreCase("LtpaToken")) {
					ltpaToken = value;
				} else if (cookieName.equalsIgnoreCase("LtpaToken2")) {
					ltpaToken2 = value;
				}
			}

			if (ltpaToken != null && !ltpaToken.equals("")) {
				initCookie += ";LtpaToken=" + ltpaToken;
			}
			if (ltpaToken2 != null && !ltpaToken2.equals("")) {
				initCookie += ";ltpaToken2=" + ltpaToken2;
			}
			HttpGet httpGet = new HttpGet("https://" + ip + ":" + port + "/ibm/console/login.do?action=secure");
			httpGet.setHeader("Cookie", initCookie);
			httpGet.setConfig(requestConfig);
			HttpUtil.getHttpResponse(httpGet);
			allCookie = initCookie;
			isLogon = true;
		} catch (Exception e) {
			e.printStackTrace();
			isLogon = false;
		}
		return isLogon;
	}

	/**
	 * 从URL获取html响应
	 * 
	 * @param suffixUrl
	 * @return
	 */
	public String getHtmlResp(String suffixUrl) {
		if (isLogon) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).build();
			String fullUrl = "https://" + ip + ":" + port + suffixUrl;
			HttpGet httpGet = new HttpGet(fullUrl);
			httpGet.setConfig(requestConfig);
			httpGet.setHeader("Cookie", allCookie);
			return HttpUtil.getHttpResponse(httpGet);
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
			doc = Jsoup.parse(html);
		} catch (Exception e) {
			return null;
		}
		return doc;
	}

	/**
	 * 判断URL是否可连接
	 * 
	 * @return
	 */
	public boolean isExist() {
		String url = "https://" + ip + ":" + port + "/ibm/console/logon.jsp";
		HttpUtil.getHttpResponse(url);
		CloseableHttpClient httpClient;
		HttpClientContext context;
		HttpGet httpGet = new HttpGet(url);
		try {
			httpClient = HttpUtil.createSSLInsecureClient();
			context = HttpClientContext.create();
			httpClient.execute(httpGet, context);

			CookieStore cookieStore = context.getCookieStore();
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				String cookieName = cookie.getName();
				String value = cookie.getValue();
				if (cookieName.equalsIgnoreCase("JSESSIONID")) {
					jSessionId = value;
					break;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
