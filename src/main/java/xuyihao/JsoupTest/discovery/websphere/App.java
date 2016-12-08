package xuyihao.JsoupTest.discovery.websphere;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuyihao.JsoupTest.discovery.websphere.function.*;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
				CommonUtils.output("登录成功\r\n\r\n");
				Map<String, String> hrefMap = getNavigationHrefs();

				//cluster
				getWasClusterInfo(hrefMap.get("hrefCluster"));
				//appServer
				getAppServerInfo(hrefMap.get("hrefAppServer"));
				//webServer
				getWebServerInfo(hrefMap.get("hrefWebServer"));
				//MQServer
				getMQServerInfo(hrefMap.get("hrefMQServer"));
				//webApp
				getWebAppInfo(hrefMap.get("hrefWebApp"));
			} else {
				CommonUtils.output("登录不成功");
			}
		} else {
			CommonUtils.output("不存在");
		}
	}

	/**
	 * Cluster
	 *
	 * @param hrefCluster
	   */
	public static void getWasClusterInfo(String hrefCluster) {
		String clusterResponse = sendGetter.getHtmlResp(hrefCluster);
		Document document = sendGetter.parseHtmlToDoc(clusterResponse);

		int nameNumber = -1;
		Elements headTable = document.getElementsByClass("column-head-name");
		for (int i = 0; i < headTable.size(); i++) {
			String name = headTable.get(i).text();
			if ((nameNumber == -1) && (name.contains("Name") || name.contains("名称"))) {
				nameNumber = i;
				break;
			}
		}

		Elements clusterTable = document.getElementsByClass("table-row");
		for (Element row : clusterTable) {
			Elements baseInfo = row.getElementsByClass("collection-table-text");
			if (baseInfo.size() < 1) {
				continue;
			}

			String clusterName = "";
			String clusterHref = "";
			if (nameNumber != -1) {
				Element nameInfo = baseInfo.get(nameNumber).getElementsByTag("a").first();
				if (nameInfo != null) {
					clusterName = nameInfo.text();
					clusterHref = nameInfo.attr("href");
				}
			} else {
				Element nameInfo = baseInfo.get(1).getElementsByTag("a").first();
				if (nameInfo != null) {
					clusterName = nameInfo.text();
					clusterHref = nameInfo.attr("href");
				}
			}

			CommonUtils.output("----------------------------------------\r\n\r\nCluster: " + " Name: "
					+ clusterName);
			ClusterInformationSearch clusterInfomationSearch = new ClusterInformationSearch(sendGetter);
			clusterInfomationSearch.searchClusterInfomation(clusterHref);
		}
	}

	/**
	 * App server
	 *
	 * @param hrefAppServer
	   */
	public static void getAppServerInfo(String hrefAppServer) {
		String response = sendGetter.getHtmlResp(hrefAppServer);
		Document doc1 = sendGetter.parseHtmlToDoc(response);

		int appServerNameNumber = -1;
		int appServerNodeNameNumber = -1;
		int appServerVersionNumber = -1;
		int appServerClusterNameNumber = -1;

		Elements tableHead = doc1.getElementsByClass("column-head-name");
		for (int i = 0; i < tableHead.size(); i++) {
			String name = tableHead.get(i).text();
			if ((appServerNameNumber == -1) && (name.contains("Name") || name.contains("名称"))) {
				appServerNameNumber = i;
				continue;
			}
			if ((appServerNodeNameNumber == -1) && (name.contains("Node") || name.contains("节点"))) {
				appServerNodeNameNumber = i;
				continue;
			}
			if ((appServerVersionNumber == -1) && (name.contains("Version") || name.contains("版本"))) {
				appServerVersionNumber = i;
				continue;
			}
			if ((appServerClusterNameNumber == -1) && (name.contains("Cluster Name") || name.contains("集群"))) {
				appServerClusterNameNumber = i;
				continue;
			}
		}

		Elements elements = doc1.getElementsByClass("table-row");
		for (Element row : elements) {// 一个个查找Application server 的信息
			Elements baseInfo = row.getElementsByClass("collection-table-text");
			if (baseInfo.size() < 1) {
				continue;
			}
			String name;
			String href;
			String nodeName;
			String appServerversion;
			String clusterName;
			if (appServerNameNumber != -1) {
				Element serverInfo = baseInfo.get(appServerNameNumber).getElementsByTag("a").first();
				name = serverInfo.text();
				href = serverInfo.attr("href");
			} else {
				Element serverInfo = baseInfo.get(0).getElementsByTag("a").first();
				name = serverInfo.text();
				href = serverInfo.attr("href");
			}
			if (appServerNodeNameNumber != -1) {
				nodeName = baseInfo.get(appServerNodeNameNumber).text();
			} else {
				nodeName = baseInfo.get(1).text();
			}
			if (appServerVersionNumber != -1) {
				appServerversion = baseInfo.get(appServerVersionNumber).text();
			} else {
				appServerversion = baseInfo.get(3).text();
			}
			if (appServerClusterNameNumber != -1) {
				clusterName = baseInfo.get(appServerClusterNameNumber).text();
			} else {
				//clusterName = baseInfo.get(5).text();
				clusterName = "";
			}
			CommonUtils.output("----------------------------------------\r\n\r\nAppServer: " + name + " NodeName: "
					+ nodeName + " version: " + appServerversion + " cluster: " + clusterName);
			AppServerInfomationSearch appServerInfomationSearch = new AppServerInfomationSearch(sendGetter);
			appServerInfomationSearch.searchAppServerInfomation(href);
		}
	}

	/**
	 * Web server
	 *
	 * @param hrefWebServer
	   */
	public static void getWebServerInfo(String hrefWebServer) {
		CommonUtils.output("\r\n\r\n");
		String webServerResponse = sendGetter.getHtmlResp(hrefWebServer);
		Document doc2 = sendGetter.parseHtmlToDoc(webServerResponse);

		int webServerNameNumber = -1;
		int webServerNodeNameNumber = -1;
		int webServerVersionNumber = -1;
		Elements webServerHeadTable = doc2.getElementsByClass("column-head-name");
		for (int i = 0; i < webServerHeadTable.size(); i++) {
			String name = webServerHeadTable.get(i).text();
			if ((webServerNameNumber == -1) && (name.contains("Name") || name.contains("名称"))) {
				webServerNameNumber = i;
				continue;
			}
			if ((webServerNodeNameNumber == -1) && (name.contains("Node") || name.contains("节点"))) {
				webServerNodeNameNumber = i;
				continue;
			}
			if ((webServerVersionNumber == -1) && (name.contains("Version") || name.contains("版本"))) {
				webServerVersionNumber = i;
				continue;
			}
		}

		Elements tableWebServer = doc2.getElementsByClass("table-row");
		int size2 = tableWebServer.size();
		for (int j = 0; j < size2; j++) {// 一个个查找Web server 的信息
			Elements baseInfo2 = tableWebServer.get(j).getElementsByClass("collection-table-text");
			if (baseInfo2.size() < 1) {
				continue;
			}
			String webServerName;
			String webServerNodeName;
			String webServerVersion;
			String href2;

			if (webServerNameNumber != -1) {
				webServerName = baseInfo2.get(webServerNameNumber).text();
				href2 = baseInfo2.get(webServerNameNumber).getElementsByTag("a").last().attr("href");
			} else {
				webServerName = baseInfo2.get(1).text();
				href2 = baseInfo2.get(1).getElementsByTag("a").last().attr("href");
			}
			if (webServerNodeNameNumber != -1) {
				webServerNodeName = baseInfo2.get(webServerNodeNameNumber).text();
			} else {
				webServerNodeName = baseInfo2.get(3).text();
			}
			if (webServerVersionNumber != -1) {
				webServerVersion = baseInfo2.get(webServerVersionNumber).text();
			} else {
				webServerVersion = baseInfo2.get(4).text();
			}

			CommonUtils.output(webServerName + "-----" + webServerNodeName + "-----" + webServerVersion);
			WebServerInformationSearch webServerInformationSearch = new WebServerInformationSearch(sendGetter);
			webServerInformationSearch.searchWebServerInfomation(href2);
		}
	}

	/**
	 * MQ server
	 *
	 * @param hrefMQServer
	   */
	public static void getMQServerInfo(String hrefMQServer) {
		CommonUtils.output("\r\n\r\n");
		String MQServerResponse = sendGetter.getHtmlResp(hrefMQServer);
		Document doc3 = sendGetter.parseHtmlToDoc(MQServerResponse);

		int mqServerNameNumber = -1;
		Elements mqServerHeadTable = doc3.getElementsByClass("column-head-name");
		for (int i = 0; i < mqServerHeadTable.size(); i++) {
			String name = mqServerHeadTable.get(i).text();
			if ((mqServerNameNumber == -1) && (name.contains("Name") || name.contains("名称"))) {
				mqServerNameNumber = i;
				break;
			}
		}

		Elements tableMQServer = doc3.getElementsByClass("table-row");
		for (Element row : tableMQServer) {
			if (mqServerNameNumber != -1) {
				Elements baseInfo3 = row.getElementsByClass("collection-table-text");
				if (baseInfo3.size() < 1) {
					continue;
				}
				CommonUtils.output("\r\n--------------" + baseInfo3.get(mqServerNameNumber).text());
				String href3 = baseInfo3.get(mqServerNameNumber).getElementsByTag("a").last().attr("href");
				MQServerInformationSearch mqServerInformationSearch = new MQServerInformationSearch(sendGetter);
				mqServerInformationSearch.searchMQServerInformation(href3);
			} else {
				Elements baseInfo3 = row.getElementsByClass("collection-table-text");
				if (baseInfo3.size() < 1) {
					continue;
				}
				CommonUtils.output("\r\n--------------" + baseInfo3.get(1).text());
				String href3 = baseInfo3.get(1).getElementsByTag("a").last().attr("href");
				MQServerInformationSearch mqServerInformationSearch = new MQServerInformationSearch(sendGetter);
				mqServerInformationSearch.searchMQServerInformation(href3);
			}
		}
	}

	/**
	 * Web App
	 *
	 * @param hrefWebApp
	   */
	public static void getWebAppInfo(String hrefWebApp) {
		CommonUtils.output("\r\n\r\n");
		String WebAppResponse = sendGetter.getHtmlResp(hrefWebApp);
		Document doc4 = sendGetter.parseHtmlToDoc(WebAppResponse);

		int webAppNameNumber = -1;
		Elements webAppTableHeader = doc4.getElementsByClass("column-head-name");
		for (int i = 0; i < webAppTableHeader.size(); i++) {
			String name = webAppTableHeader.get(i).text();
			if ((webAppNameNumber == -1) && (name.contains("Name") || name.contains("名称"))) {
				webAppNameNumber = i;
				break;
			}
		}

		Elements tableWebApp = doc4.getElementsByClass("table-row");
		for (Element webAppRow : tableWebApp) {
			if (webAppNameNumber != -1) {
				Element singleWebApp = webAppRow.getElementsByClass("collection-table-text").get(webAppNameNumber);
				CommonUtils.output("\r\n------------" + singleWebApp.text());
				String href4 = singleWebApp.getElementsByTag("a").last().attr("href");
				WebAppInformationSearch webAppInformationSearch = new WebAppInformationSearch(sendGetter);
				webAppInformationSearch.getWebAppInformation("/ibm/console/" + href4);
			} else {
				Element singleWebApp = webAppRow.getElementsByAttributeValue("headers", "name").last();
				CommonUtils.output("\r\n------------" + singleWebApp.text());
				String href4 = singleWebApp.getElementsByTag("a").last().attr("href");
				WebAppInformationSearch webAppInformationSearch = new WebAppInformationSearch(sendGetter);
				webAppInformationSearch.getWebAppInformation("/ibm/console/" + href4);
			}

		}
	}

	public static void getWebsphereVersion() {
		//欢迎页面
		String welcomePage = sendGetter.getHtmlResp("/ibm/console");
		Document welcomPageDoc = sendGetter.parseHtmlToDoc(welcomePage);
		Elements welcomElements = welcomPageDoc.getElementsByAttributeValue("name", "detail");
		if (welcomElements != null) {
			Element detailPageElement = welcomElements.last();
			String detailHref = detailPageElement.attr("src");
			String detailPage = sendGetter.getHtmlResp(detailHref);
			Document detailPageDoc = sendGetter.parseHtmlToDoc(detailPage);
			Element versionElement = detailPageDoc.getElementsByClass("portlet-table-body").last();
			if (versionElement != null) {
				version = versionElement.text();
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	public static Map<String, String> getNavigationHrefs() {
		Map<String, String> hrefMap = new HashMap<>();
		//导航页面
		String navigationPage = sendGetter.getHtmlResp("/ibm/console/nsc.do");
		Document navigationPageDoc = sendGetter.parseHtmlToDoc(navigationPage);
		Elements table = navigationPageDoc.getElementsByClass("navigation-bullet");
		for (Element row : table) {
			String ref = row.getElementsByTag("a").attr("href");
			if (ref.contains("forwardName=ServerCluster")) {
				hrefMap.put("hrefCluster", ref);
				continue;
			}
			if (ref.contains("forwardName=ApplicationServer")) {
				hrefMap.put("hrefAppServer", ref);
				continue;
			}
			if (ref.contains("forwardName=WebServer")) {
				hrefMap.put("hrefWebServer", ref);
				continue;
			}
			if (ref.contains("forwardName=SIBMQServer")) {
				hrefMap.put("hrefMQServer", ref);
				continue;
			}
			if (ref.contains("forwardName=ApplicationDeployment")) {
				hrefMap.put("hrefWebApp", ref);
				continue;
			}
		}
		return hrefMap;
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
