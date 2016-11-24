package xuyihao.JsoupTest;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuyihao.JsoupTest.function.AppServerInfomationSearch;
import xuyihao.JsoupTest.function.MQServerInformationSearch;
import xuyihao.JsoupTest.function.WebAppInformationSearch;
import xuyihao.JsoupTest.function.WebServerInformationSearch;
import xuyihao.JsoupTest.util.CommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @Author Xuyh created at 2016年11月10日 上午10:31:56
 *
 */
public class App {
	private static RequestSendGetter sendGetter = null;
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static String version;//was的版本，在初始页面获取，不同版本号的was在页面布局上会有细微的差别

	public static void main(String[] args) {
		output("Input ip-port-name-password:");
		String ip = input();
		String port = input();
		String userName = input();
		String password = input();
		sendGetter = new RequestSendGetter(ip, port, userName, password);
		if (sendGetter.isExist()) {// 存在
			if (sendGetter.login()) {// 登录成功
				CommonUtils.output("登录成功");
				//欢迎页面
				String welcomePage = sendGetter.getHtmlResp("/ibm/console");
				Document welcomPageDoc = sendGetter.parseHtmlToDoc(welcomePage);
				Element detailPageElement = welcomPageDoc.getElementsByAttributeValue("name", "detail").last();
				String detailHref = detailPageElement.attr("src");
				String detailPage = sendGetter.getHtmlResp(detailHref);
				Document detailPageDoc = sendGetter.parseHtmlToDoc(detailPage);
				Element versionElement = detailPageDoc.getElementsByClass("portlet-table-body").last();
				if(versionElement != null){
					version = versionElement.text();
				}
				//导航页面
				String navigationPage = sendGetter.getHtmlResp("/ibm/console/nsc.do");
				Document navigationPageDoc = sendGetter.parseHtmlToDoc(navigationPage);
				String hrefAppServer = "";
				String hrefWebServer = "";
				String hrefMQServer = "";
				String hrefWebApp = "";
				Elements table = navigationPageDoc.getElementsByClass("navigation-bullet");
				for (Element row : table) {
					String ref = row.getElementsByTag("a").attr("href");
					if (ref.contains("forwardName=ApplicationServer")) {
						hrefAppServer = ref;
						continue;
					}
					if (ref.contains("forwardName=WebServer")) {
						hrefWebServer = ref;
						continue;
					}
					if (ref.contains("forwardName=SIBMQServer")) {
						hrefMQServer = ref;
						continue;
					}
					if (ref.contains("forwardName=ApplicationDeployment")) {
						hrefWebApp = ref;
						continue;
					}
				}
				// ------------------------App server
				String response = sendGetter.getHtmlResp(hrefAppServer);
				Document doc1 = sendGetter.parseHtmlToDoc(response);
				Elements elements = doc1.getElementsByClass("table-row");
				int size = elements.size();
				for (int i = 0; i < size; i++) {// 一个个查找Application server 的信息
					Elements baseInfo = elements.get(i).getElementsByClass("collection-table-text");
					if(baseInfo.size() < 1){
						continue;
					}
					String name;
					String href;
					String nodeName;
					String appServerversion;
					if(version.contains("8.")){//8.0/8.5...
						Element serverInfo = baseInfo.get(0).getElementsByTag("a").first();
						name = serverInfo.text();
						href = serverInfo.attr("href");
						nodeName = baseInfo.get(1).text();
						appServerversion = baseInfo.get(3).text();
					}else {//6.0/6.1...
						Element serverInfo = baseInfo.get(0).getElementsByTag("a").first();
						name = serverInfo.text();
						href = serverInfo.attr("href");
						nodeName = baseInfo.get(1).text();
						appServerversion = baseInfo.get(2).text();
					}
					CommonUtils.output("----------------------------------------\r\n\r\nAppServer: " + name + " NodeName: "
							+ nodeName + " version: " + appServerversion);
					AppServerInfomationSearch appServerInfomationSearch = new AppServerInfomationSearch(sendGetter);
					appServerInfomationSearch.searchAppServerInfomation(href);
				}
				// -------------------------Web server
				CommonUtils.output("\r\n\r\n");
				String webServerResponse = sendGetter.getHtmlResp(hrefWebServer);
				Document doc2 = sendGetter.parseHtmlToDoc(webServerResponse);
				Elements tableWebServer = doc2.getElementsByClass("table-row");
				int size2 = tableWebServer.size();
				for (int j = 0; j < size2; j++) {// 一个个查找Web server 的信息
					Elements baseInfo2 = tableWebServer.get(j).getElementsByClass("collection-table-text");
					if(baseInfo2.size() < 1){
						continue;
					}
					String webServerName;
					String webServerType;
					String webServerNodeName;
					String webServerVersion;
					String href2;
					if(version.contains("8.")){//8.0/8.5...
						webServerName = baseInfo2.get(1).text();
						webServerType = baseInfo2.get(2).text();
						webServerNodeName = baseInfo2.get(3).text();
						webServerVersion = baseInfo2.get(4).text();
						href2 = baseInfo2.get(1).getElementsByTag("a").last().attr("href");
						CommonUtils.output(webServerName + "-----" + webServerType + "-----" + webServerNodeName + "-----" + webServerVersion);
					}else {//6.0/6.1...
						webServerName = baseInfo2.get(1).text();
						webServerType = baseInfo2.get(2).text();
						webServerNodeName = baseInfo2.get(3).text();
						webServerVersion = baseInfo2.get(5).text();
						href2 = baseInfo2.get(1).getElementsByTag("a").last().attr("href");
						CommonUtils.output(webServerName + "-----" + webServerType + "-----" + webServerNodeName + "-----" + webServerVersion);
					}
					WebServerInformationSearch webServerInformationSearch = new WebServerInformationSearch(sendGetter);
					webServerInformationSearch.searchWebServerInfomation(href2);
				}
				// -------------------------MQ server
				CommonUtils.output("\r\n\r\n");
				String MQServerResponse = sendGetter.getHtmlResp(hrefMQServer);
				Document doc3 = sendGetter.parseHtmlToDoc(MQServerResponse);
				Elements tableMQServer = doc3.getElementsByClass("table-row");
				int size3 = tableMQServer.size();
				for (int j = 0; j < size3; j++) {// 一个个查找MQ server 的信息
					Elements baseInfo3 = tableMQServer.get(j).getElementsByClass("collection-table-text");
					if(baseInfo3.size() < 1){
						continue;
					}
					CommonUtils.output("\r\n--------------" + baseInfo3.get(1).text());
					String href3 = baseInfo3.get(1).getElementsByTag("a").last().attr("href");
					MQServerInformationSearch mqServerInformationSearch = new MQServerInformationSearch(sendGetter);
					mqServerInformationSearch.searchMQServerInformation(href3);
				}
				// ---------------------------Web App
				CommonUtils.output("\r\n\r\n");
				String WebAppResponse = sendGetter.getHtmlResp(hrefWebApp);
				Document doc4 = sendGetter.parseHtmlToDoc(WebAppResponse);
				Elements tableWebApp = doc4.getElementsByClass("table-row");
				for (int k = 0; k < tableWebApp.size(); k++) {
					Element webAppRow = tableWebApp.get(k);
					Element singleWebApp = webAppRow.getElementsByAttributeValue("headers", "name").last();
					CommonUtils.output("\r\n------------" + singleWebApp.text());
					String href4 = singleWebApp.getElementsByTag("a").last().attr("href");
					WebAppInformationSearch webAppInformationSearch = new WebAppInformationSearch(sendGetter);
					webAppInformationSearch.getWebAppInformation("/ibm/console/" + href4);
				}
			} else {
				CommonUtils.output("登录不成功");
			}
		} else {
			CommonUtils.output("不存在");
		}
	}

	public static String input() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void output(String message) {
		System.out.println(message);
	}
}
