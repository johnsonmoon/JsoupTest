package xuyihao.JsoupTest.discovery.websphere.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xuyihao.JsoupTest.discovery.websphere.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

/**
 * Created by Xuyh at 2016/11/17 下午 05:12.
 */
public class WebServerInformationSearch {
	private RequestSendGetter sendGetter = null;

	public WebServerInformationSearch(RequestSendGetter requestSendGetter) {
		sendGetter = requestSendGetter;
	}

	public void searchWebServerInfomation(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document doc = sendGetter.parseHtmlToDoc(response);

		// Web 服务器名称
		Element serverName = doc.getElementById("serverName");
		if (serverName != null) {
			CommonUtils.output("serverName--->" + serverName.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 类型
		Element webServerType = doc.getElementsByAttributeValue("name", "webServerType").last();
		if (webServerType != null) {
			for (Element option : webServerType.getElementsByTag("option")) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("webServerType--->" + option.text());
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 主机名
		Element hostname = doc.getElementById("hostname");
		if (hostname != null) {
			CommonUtils.output("hostname--->" + hostname.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 端口
		Element port = doc.getElementById("port");
		if (port != null) {
			CommonUtils.output("port--->" + port.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// Web 服务器安装位置
		Element installPath = doc.getElementsByAttributeValue("name", "installPath").last();
		if (installPath != null) {
			CommonUtils.output("installPath--->" + installPath.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 配置文件名
		Element configFileName = doc.getElementsByAttributeValue("name", "configFileName").last();
		if (configFileName != null) {
			CommonUtils.output("configFileName--->" + configFileName.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 服务名称
		Element serviceName = doc.getElementsByAttributeValue("name", "serviceName").last();
		if (serviceName != null) {
			CommonUtils.output("serviceName--->" + serviceName.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 平台类型
		Element platformType = doc.getElementById("platformType");
		if (platformType != null) {
			CommonUtils.output("platformType--->" + platformType.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}
}
