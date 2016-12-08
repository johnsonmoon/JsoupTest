package xuyihao.JsoupTest.discovery.websphere.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xuyihao.JsoupTest.discovery.websphere.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

/**
 * Created by Xuyh on 2016/12/7.
 */
public class ClusterInformationSearch {
	private RequestSendGetter sendGetter = null;

	public ClusterInformationSearch(RequestSendGetter sendGetter) {
		this.sendGetter = sendGetter;
	}

	public void searchClusterInfomation(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document doc = sendGetter.parseHtmlToDoc(response);

		//Bounding node group name
		Element nodeGroupName = doc.getElementsByAttributeValue("name", "nodeGroupName").last();
		if (nodeGroupName != null) {
			for (Element option : nodeGroupName.getElementsByTag("option")) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("nodeGroupName--->" + option.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//Prefer local
		Element preferLocal = doc.getElementsByAttributeValue("name", "preferLocal").last();
		if (preferLocal != null) {
			CommonUtils.output("preferLocal--->" + (preferLocal.attr("checked").equals("checked") ? "是" : "否"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//Enable failover of transaction log recovery
		Element enableHA = doc.getElementsByAttributeValue("name", "enableHA").last();
		if (enableHA != null) {
			CommonUtils.output("enableHA--->" + (enableHA.attr("checked").equals("checked") ? "是" : "否"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		Elements detailInfos = doc.getElementsByClass("nav-bullet");
		for (Element detailInfo : detailInfos) {
			Element refEle = detailInfo.getElementsByTag("a").first();
			String ref = refEle.attr("href");
			if (ref.contains("forwardName=CEA.features")) {
				getCommunicationsEnabledApplicationsInfo(ref);
				continue;
			}
		}
	}

	private void getCommunicationsEnabledApplicationsInfo(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		//Communications Enabled Applications services
		//Enable communications service
		Element jsr289Enabled = document.getElementsByAttributeValue("name", "jsr289Enabled").last();
		if (jsr289Enabled != null) {
			CommonUtils.output("jsr289Enabled--->" + (jsr289Enabled.attr("checked").equals("checked") ? "是" : "否"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//REST interface
		//Context root
		Element restContextRoot = document.getElementsByAttributeValue("name", "restContextRoot").last();
		if (restContextRoot != null) {
			CommonUtils.output("restContextRoot--->" + restContextRoot.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//Virtual host(seconds)
		Element virtualHost = document.getElementsByAttributeValue("name", "virtualHost").last();
		if (virtualHost != null) {
			for (Element option : virtualHost.getElementsByTag("option")) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("virtualHost--->" + option.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//Maximum hold time
		Element maxRequestHoldTime = document.getElementsByAttributeValue("name", "maxRequestHoldTime").last();
		if (maxRequestHoldTime != null) {
			CommonUtils.output("maxRequestHoldTime--->" + maxRequestHoldTime.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		//Telephony access method
		//Use SIP CTI (ECMA TR/87) gateway for telephony access
		Elements telephonyAccessMethod = document.getElementsByAttributeValue("name", "telephonyAccessMethod");
		if (telephonyAccessMethod != null) {
			for (Element option : telephonyAccessMethod) {
				if (option.attr("checked").equals("checked")) {
					CommonUtils.output("telephonyAccessMethod--->" + telephonyAccessMethod.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}
}
