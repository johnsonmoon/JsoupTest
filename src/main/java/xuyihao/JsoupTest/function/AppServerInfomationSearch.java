package xuyihao.JsoupTest.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuyihao.JsoupTest.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;

/**
 * 
 * @Author Xuyh created at 2016年11月15日 下午1:35:28
 *
 */
public class AppServerInfomationSearch {
	private RequestSendGetter sendGetter = null;

	public AppServerInfomationSearch(RequestSendGetter sendGetter) {
		this.sendGetter = sendGetter;
	}

	public void searchAppServerInfomation(String href) {
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document doc = sendGetter.parseHtmlToDoc(response);

		// ----------------------------------基础信息

		Element developmentMode = doc.getElementById("developmentMode");
		// 以开发方式运行
		if (developmentMode.attr("checked").equals("checked")) {
			CommonUtils.output("developmentMode");
		}
		// 并行启动
		Element parallelStartEnabled = doc.getElementById("parallelStartEnabled");
		if (parallelStartEnabled.attr("checked").equals("checked")) {
			CommonUtils.output("parallelStartEnabled");
		}
		Element internalClassesAccessMode = doc.getElementById("internalClassesAccessMode");
		Elements options = internalClassesAccessMode.getElementsByTag("option");
		// 访问内部服务器类
		for (int i = 0; i < options.size(); i++) {
			Element option = options.get(i);
			if (option.attr("selected").equals("selected")) {
				CommonUtils.output(option.attr("value") + " || " + option.text());
			}
		}
		// 特定于服务器的应用程序设置
		// 类装入器策略
		Element applicationClassLoaderPolicy = doc.getElementById("applicationClassLoaderPolicy");
		Elements options2 = applicationClassLoaderPolicy.getElementsByTag("option");
		for (int j = 0; j < options2.size(); j++) {
			Element option2 = options2.get(j);
			if (options2.attr("selected").equals("selected")) {
				CommonUtils.output(option2.attr("value") + " || " + option2.text());
			}
		}
		// 类装入方式
		Element applicationClassLoadingMode = doc.getElementById("applicationClassLoadingMode");
		Elements options3 = applicationClassLoadingMode.getElementsByTag("option");
		for (int k = 0; k < options3.size(); k++) {
			Element option3 = options3.get(k);
			if (option3.attr("selected").equals("selected")) {
				CommonUtils.output(option3.attr("value") + " || " + option3.text());
			}
		}

		// ------------------------------------------具体信息

		Elements detailInfos = doc.getElementsByClass("nav-bullet");
		CommonUtils.output(String.valueOf(detailInfos.size()));
		for (int i2 = 0; i2 < detailInfos.size(); i2++) {
			Element detailInfo = detailInfos.get(i2);
			switch (detailInfo.text().trim()) {
			case "会话管理":
				getSessionManagementInfo(detailInfo);
				break;
			case "SIP容器":
				getSIPContainerinfo(detailInfo);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 会话管理信息
	 * 
	 * @param element
	 */
	private void getSessionManagementInfo(Element element) {
		CommonUtils.output("\r\n会话管理信息");

		Element refEle = element.getElementsByTag("a").first();
		String href = refEle.attr("href");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 会话跟踪机制
		// 启用 SSL 标识跟踪
		Element enableSSLTracking = document.getElementById("enableSSLTracking");
		if (enableSSLTracking.attr("checked").equals("checked")) {
			CommonUtils.output(enableSSLTracking.attr("name") + "  yes");
		} else {
			CommonUtils.output(enableSSLTracking.attr("name") + "  no");
		}
		// 启用 cookie
		Element enableCookies = document.getElementById("enableCookies");
		if (enableCookies.attr("checked").equals("checked")) {
			CommonUtils.output(enableCookies.attr("name") + "  yes");
		} else {
			CommonUtils.output(enableCookies.attr("name") + "  no");
		}
		// 启用 URL 重写
		Element enableUrlRewriting = document.getElementById("enableUrlRewriting");
		if (enableUrlRewriting.attr("checked").equals("checked")) {
			CommonUtils.output(enableUrlRewriting.attr("name") + "  yes");
		} else {
			CommonUtils.output(enableUrlRewriting.attr("name" + "  no"));
		}
		// 启用协议切换重写
		Element enableProtocolSwitchRewriting = document.getElementById("enableProtocolSwitchRewriting");
		if (enableProtocolSwitchRewriting.attr("checked").equals("checked")) {
			CommonUtils.output(enableProtocolSwitchRewriting.attr("name") + "  yes");
		} else {
			CommonUtils.output(enableProtocolSwitchRewriting.attr("name") + "  no");
		}

		// 内存中最大会话量
		Element maxInMemorySessionCount = document.getElementById("maxInMemorySessionCount");
		CommonUtils.output("maxInMemorySessionCount" + maxInMemorySessionCount.attr("value"));
		// 会话允许溢出
		Element allowOverflow = document.getElementById("allowOverflow");
		if (allowOverflow.attr("checked").equals("checked")) {
			CommonUtils.output(allowOverflow.attr("name" + "  yes"));
		} else {
			CommonUtils.output(allowOverflow.attr("name") + "  no");
		}

		// 会话超时
		Element vnone = document.getElementById("vnone");
		Element invalidationTimeout = document.getElementById("invalidationTimeout");
		if (vnone.attr("checked").equals("checked")) {
			CommonUtils.output("无超时");
		} else {
			CommonUtils.output(invalidationTimeout.attr("value") + invalidationTimeout.text());
		}

		// 安全性集成
		Element enableSecurityIntegration = document.getElementById("enableSecurityIntegration");
		if (enableSecurityIntegration.attr("checked").equals("checked")) {
			CommonUtils.output(enableSecurityIntegration.attr("安全性集成") + "on");
		} else {
			CommonUtils.output(enableSecurityIntegration.attr("安全性集成") + "off");
		}

		// 序列化会话访问
		// 允许序列访问
		Element allowSerializedSessionAccess = document.getElementById("allowSerializedSessionAccess");
		if (allowSerializedSessionAccess.attr("checked").equals("checked")) {
			CommonUtils.output("允许序列访问");
		} else {
			CommonUtils.output("不允许序列访问");
		}
		// 最长等待时间
		Element maxWaitTime = document.getElementById("maxWaitTime");
		CommonUtils.output(maxWaitTime.attr("value") + maxWaitTime.text());
		// 允许访问超时
		Element accessSessionOnTimeout = document.getElementById("accessSessionOnTimeout");
		if (accessSessionOnTimeout.attr("checked").equals("checked")) {
			CommonUtils.output("允许访问超时");
		} else {
			CommonUtils.output("不允许访问超时");
		}

		CommonUtils.output("\r\n");
	}

	/**
	 * SIP容器
	 * 
	 * @param element
	 */
	private void getSIPContainerinfo(Element element) {
		CommonUtils.output("\r\nSIP容器信息");

		Element refEle = element.getElementsByTag("a").first();
		String href = refEle.attr("href");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		
		//常规属性
		//最大应用程序会话数
		Element maxAppSessions = document.getElementById("maxAppSessions");
		CommonUtils.output(maxAppSessions.attr("value"));
		
		//每平均时间段最大消息数
		Element maxMessagesPerSecond = document.getElementById("maxMessagesPerSecond");
		CommonUtils.output(maxMessagesPerSecond.attr("value"));
		
		//最大分派队列大小
		Element maxDispatchQueueSize = document.getElementById("maxDispatchQueueSize");
		CommonUtils.output(maxDispatchQueueSize.attr("value"));
		
		//最大响应时间
		//启用最大响应时间
		
		
	}
}
