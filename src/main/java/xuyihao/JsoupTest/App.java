package xuyihao.JsoupTest;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuyihao.JsoupTest.function.AppServerInfomationSearch;
import xuyihao.JsoupTest.util.CommonUtils;

/**
 * 
 * @Author Xuyh created at 2016年11月10日 上午10:31:56
 *
 */
public class App {
	private static RequestSendGetter sendGetter = null;

	public static void main(String[] args) {
		sendGetter = new RequestSendGetter("10.1.240.161", "9043", "wasadmin", "admin");
		if (sendGetter.isExist()) {// 存在
			if (sendGetter.logon()) {// 登录成功
				CommonUtils.output("登录成功");
				String response = sendGetter
						.getHtmlResp("/ibm/console/navigatorCmd.do?forwardName=ApplicationServer.content.main");
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
			} else {
				CommonUtils.output("登录不成功");
			}
		} else {
			CommonUtils.output("不存在");
		}
	}
}
