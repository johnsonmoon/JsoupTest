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

	public static void main(String[] args) {
		output("Input ip-port-name-password:");
		String ip = input();
		String port = input();
		String userName = input();
		String password = input();
		sendGetter = new RequestSendGetter(ip, port, userName, password);
		if (sendGetter.isExist()) {// 存在
			if (sendGetter.logon()) {// 登录成功
				CommonUtils.output("登录成功");
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
					Element serverInfo = baseInfo.get(0).getElementsByTag("a").first();
					String name = serverInfo.text();
					String href = serverInfo.attr("href");
					String nodeName = baseInfo.get(1).text();
					String version = baseInfo.get(2).text();
					CommonUtils.output("----------------------------------------\r\n\r\nAppServer: " + name + " NodeName: "
							+ nodeName + " version: " + version);
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
					CommonUtils.output(baseInfo2.get(1).text() + "-----" + baseInfo2.get(2).text() + "-----"
							+ baseInfo2.get(3).text() + "-----" + baseInfo2.get(4).text());
					String href2 = baseInfo2.get(1).getElementsByTag("a").last().attr("href");
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
					CommonUtils.output("\r\n--------------" + baseInfo3.text());
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
