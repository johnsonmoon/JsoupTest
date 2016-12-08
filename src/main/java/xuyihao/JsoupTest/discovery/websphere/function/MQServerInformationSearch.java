package xuyihao.JsoupTest.discovery.websphere.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xuyihao.JsoupTest.discovery.websphere.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

/**
 * Created by Xuyh at 2016/11/18 下午 03:36.
 */
public class MQServerInformationSearch {
	private RequestSendGetter sendGetter;

	public MQServerInformationSearch(RequestSendGetter sendGetter) {
		this.sendGetter = sendGetter;
	}

	public void searchMQServerInformation(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document doc = sendGetter.parseHtmlToDoc(response);
		// UUID
		Element uuid = doc.getElementById("uuid");
		if (uuid != null) {
			CommonUtils.output("uuid--->" + uuid.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 服务器类型
		Elements MQServerType = doc.getElementsByAttributeValue("name", "type");
		if (MQServerType == null) {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		} else {
			for (Element type : MQServerType) {
				if (type.attr("checked").equals("checked")) {
					CommonUtils.output("MQServerType--->" + type.attr("value"));
				}
			}
		}
		// 使用绑定传输方式
		Element useBindingsMode = doc.getElementById("useBindingsMode");
		if (useBindingsMode != null) {
			CommonUtils.output("useBindingsMode--->" + useBindingsMode.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// WebSphere MQ 端口
		Element port = doc.getElementById("port");
		if (port != null) {
			CommonUtils.output("port--->" + port.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 传输链名称
		Element transportChainNameSelect = doc.getElementById("transportChainNameSelect");
		if (transportChainNameSelect != null) {
			Elements options = transportChainNameSelect.getElementsByTag("option");
			for (Element option : options) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("transportChainNameSelect--->" + option.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// WebSphere MQ 通道
		Element channel = doc.getElementById("channel");
		if (channel != null) {
			CommonUtils.output("channel--->" + channel.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 消息传递认证别名
		Element secAuthAlias = doc.getElementById("secAuthAlias");
		if (secAuthAlias != null) {
			Elements options2 = secAuthAlias.getElementsByTag("option");
			for (Element option2 : options2) {
				if (option2.attr("selected").equals("selected")) {
					CommonUtils.output("secAuthAlias--->" + option2.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 在消息中接收到的信任用户标识
		Element trustUserIdentifiers = doc.getElementById("trustUserIdentifiers");
		if (trustUserIdentifiers != null) {
			CommonUtils.output("trustUserIdentifiers--->" + trustUserIdentifiers.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 自动发现资源
		Element autoDiscovResources = doc.getElementById("autoDiscovResources");
		if (autoDiscovResources != null) {
			CommonUtils.output("autoDiscovResources--->" + autoDiscovResources.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 资源发现认证别名
		Element discovAuthAlias = doc.getElementById("discovAuthAlias");
		if (discovAuthAlias != null) {
			Elements options3 = discovAuthAlias.getElementsByTag("option");
			for (Element option3 : options3) {
				if (option3.attr("selected").equals("selected")) {
					CommonUtils.output("discovAuthAlias--->" + option3.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 应答队列
		Element replyToQueue = doc.getElementById("replyToQueue");
		if (replyToQueue != null) {
			CommonUtils.output("replyToQueue--->" + replyToQueue.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}
}
