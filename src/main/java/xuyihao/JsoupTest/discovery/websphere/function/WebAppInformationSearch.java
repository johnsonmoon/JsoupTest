package xuyihao.JsoupTest.discovery.websphere.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xuyihao.JsoupTest.discovery.websphere.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

/**
 * Created by Xuyh at 2016/11/19 下午 01:50.
 */
public class WebAppInformationSearch {
	private RequestSendGetter sendGetter;

	public WebAppInformationSearch(RequestSendGetter sendGetter) {
		this.sendGetter = sendGetter;
	}

	public void getWebAppInformation(String href) {
		String response = sendGetter.getHtmlResp(href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 名称
		Element name = document.getElementById("name");
		if (name != null) {
			CommonUtils.output("name--->" + name.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 应用程序引用验证
		Element validation = document.getElementById("validation");
		if (validation != null) {
			for (Element option : validation.getElementsByTag("option")) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("validation--->" + option.text());
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		Elements attrRefs = document.getElementsByClass("nav-bullet");
		for (Element refRow : attrRefs) {
			String ref = refRow.getElementsByTag("a").last().attr("href");
			if (ref.contains("forwardName=ApplicationStartupProperties")) {
				getApplicationStartupPropertiesInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=appconfigure.modules.toappservers")) {
				getAppconfigureInfo(ref);
				continue;
			}
		}
	}

	/**
	 * 启动行为
	 *
	 * @param href
	 */
	private void getApplicationStartupPropertiesInfo(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 启动顺序
		Element startingWeight = document.getElementById("startingWeight");
		if (startingWeight != null) {
			CommonUtils.output("startingWeight--->" + startingWeight.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 在服务器完成启动之前启动应用程序
		Element backgroundApplication = document.getElementById("backgroundApplication");
		if (backgroundApplication != null) {
			CommonUtils.output("backgroundApplication--->" + backgroundApplication.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 为资源创建 MBean
		Element createMBeansForResources = document.getElementById("createMBeansForResources");
		if (createMBeansForResources != null) {
			CommonUtils.output("createMBeansForResources--->" + createMBeansForResources.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * 管理模块
	 *
	 * @param href
	 */
	private void getAppconfigureInfo(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		Element table = document.getElementsByClass("table-row").last();
		if (table != null) {
			Elements rows = table.getElementsByClass("collection-table-text");
			// URI
			CommonUtils.output(rows.get(2).text());
			// 模块类型
			CommonUtils.output(rows.get(3).text());
			// 服务器(部署在那个服务器上)
			CommonUtils.output(rows.get(4).text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}
}
