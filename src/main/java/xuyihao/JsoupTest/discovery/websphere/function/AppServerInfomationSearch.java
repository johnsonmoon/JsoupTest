package xuyihao.JsoupTest.discovery.websphere.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xuyihao.JsoupTest.discovery.websphere.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;
import xuyihao.JsoupTest.util.StackTraceUtil;

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
		if (developmentMode != null) {
			if (developmentMode.attr("checked").equals("checked")) {
				CommonUtils.output("developmentMode");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 并行启动
		Element parallelStartEnabled = doc.getElementById("parallelStartEnabled");
		if (parallelStartEnabled != null) {
			if (parallelStartEnabled.attr("checked").equals("checked")) {
				CommonUtils.output("parallelStartEnabled");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		Element internalClassesAccessMode = doc.getElementById("internalClassesAccessMode");
		if (internalClassesAccessMode != null) {
			Elements options = internalClassesAccessMode.getElementsByTag("option");
			// 访问内部服务器类
			for (int i = 0; i < options.size(); i++) {
				Element option = options.get(i);
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output(option.attr("value") + " || " + option.text());
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 特定于服务器的应用程序设置
		// 类装入器策略
		Element applicationClassLoaderPolicy = doc.getElementById("applicationClassLoaderPolicy");
		if (applicationClassLoaderPolicy != null) {
			Elements options2 = applicationClassLoaderPolicy.getElementsByTag("option");
			for (int j = 0; j < options2.size(); j++) {
				Element option2 = options2.get(j);
				if (options2.attr("selected").equals("selected")) {
					CommonUtils.output(option2.attr("value") + " || " + option2.text());
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 类装入方式
		Element applicationClassLoadingMode = doc.getElementById("applicationClassLoadingMode");
		if (applicationClassLoadingMode != null) {
			Elements options3 = applicationClassLoadingMode.getElementsByTag("option");
			for (int k = 0; k < options3.size(); k++) {
				Element option3 = options3.get(k);
				if (option3.attr("selected").equals("selected")) {
					CommonUtils.output(option3.attr("value") + " || " + option3.text());
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// ------------------------------------------具体信息
		getPortInfo(doc);
		Elements detailInfos = doc.getElementsByClass("nav-bullet");
		CommonUtils.output(String.valueOf(detailInfos.size()));
		for (int i2 = 0; i2 < detailInfos.size(); i2++) {
			Element detailInfo = detailInfos.get(i2);
			Element refEle = detailInfo.getElementsByTag("a").first();
			String ref = refEle.attr("href");
			if (ref.contains("forwardName=SessionManager")) {// 会话管理
				getSessionManagementInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=SIPContainer")) {// SIP容器
				getSIPContainerinfo(ref);
				continue;
			}
			if (ref.contains("servermanagement.AppserverWebContainer.do")) {
				getWebContainerInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=PortletContainer")) {
				getPortletInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=EJBContainer")) {
				getEJBContainerInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=EJBCache")) {
				getEJBCacheInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=EJBTimer")) {
				getEJBTimerInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=ApplicationProfileService")) {
				getApplicationProfilingInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=TransactionService")) {
				getTransactionServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=DynamicCache")) {
				getDynamicCacheInfo(ref);
				continue;
			}
			if (ref.contains("CompensationService")) {
				getCompensationServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=I18NService")) {
				getI18NServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=ObjectPoolService")) {
				getObjectPoolServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=ObjectRequestBroker")) {
				getObjectRequestBrokerInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=StartupBeansService")) {
				getStartupBeansServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=ActivitySessionService")) {
				getActivitySessionServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=WorkAreaService")) {
				getWorkAreaServiceInfo(ref);
				continue;
			}
			if (ref.contains("forwardName=SIBService")) {
				getSIBServiceInfo(ref);
				continue;
			}
		}

	}

	/**
	 * 获取端口信息
	 *
	 * @param document
	 */
	private void getPortInfo(Document document) {
		Elements table = document.getElementsByClass("table-row");
		if (table == null) {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		for (Element row : table) {
			Elements infos = row.getElementsByClass("collection-table-text");
			CommonUtils.output(infos.first().text() + "--->" + infos.last().text());
		}
	}

	/**
	 * 会话管理信息
	 * 
	 * @param
	 */
	private void getSessionManagementInfo(String href) {
		CommonUtils.output("\r\n会话管理信息");

		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 会话跟踪机制
		// 启用 SSL 标识跟踪
		Element enableSSLTracking = document.getElementById("enableSSLTracking");
		if (enableSSLTracking != null) {
			if (enableSSLTracking.attr("checked").equals("checked")) {
				CommonUtils.output(enableSSLTracking.attr("name") + "  yes");
			} else {
				CommonUtils.output(enableSSLTracking.attr("name") + "  no");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用 cookie
		Element enableCookies = document.getElementById("enableCookies");
		if (enableCookies != null) {
			if (enableCookies.attr("checked").equals("checked")) {
				CommonUtils.output(enableCookies.attr("name") + "  yes");
			} else {
				CommonUtils.output(enableCookies.attr("name") + "  no");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用 URL 重写
		Element enableUrlRewriting = document.getElementById("enableUrlRewriting");
		if (enableUrlRewriting != null) {
			if (enableUrlRewriting.attr("checked").equals("checked")) {
				CommonUtils.output(enableUrlRewriting.attr("name") + "  yes");
			} else {
				CommonUtils.output(enableUrlRewriting.attr("name" + "  no"));
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用协议切换重写
		Element enableProtocolSwitchRewriting = document.getElementById("enableProtocolSwitchRewriting");
		if (enableProtocolSwitchRewriting != null) {
			if (enableProtocolSwitchRewriting.attr("checked").equals("checked")) {
				CommonUtils.output(enableProtocolSwitchRewriting.attr("name") + "  yes");
			} else {
				CommonUtils.output(enableProtocolSwitchRewriting.attr("name") + "  no");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 内存中最大会话量
		Element maxInMemorySessionCount = document.getElementById("maxInMemorySessionCount");
		if (maxInMemorySessionCount != null) {
			CommonUtils.output("maxInMemorySessionCount" + maxInMemorySessionCount.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 会话允许溢出
		Element allowOverflow = document.getElementById("allowOverflow");
		if (allowOverflow != null) {
			if (allowOverflow.attr("checked").equals("checked")) {
				CommonUtils.output(allowOverflow.attr("name" + "  yes"));
			} else {
				CommonUtils.output(allowOverflow.attr("name") + "  no");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 会话超时
		Element vnone = document.getElementById("vnone");
		if (vnone == null) {
			vnone = document.getElementById("none");
			if (vnone == null) {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
		}
		Element invalidationTimeout = document.getElementById("invalidationTimeout");
		if (vnone != null && invalidationTimeout != null) {
			if (vnone.attr("checked").equals("checked")) {
				CommonUtils.output("无超时");
			} else {
				CommonUtils.output(invalidationTimeout.attr("value") + invalidationTimeout.text());
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 安全性集成
		Element enableSecurityIntegration = document.getElementById("enableSecurityIntegration");
		if (enableSecurityIntegration != null) {
			if (enableSecurityIntegration.attr("checked").equals("checked")) {
				CommonUtils.output(enableSecurityIntegration.attr("安全性集成") + "on");
			} else {
				CommonUtils.output(enableSecurityIntegration.attr("安全性集成") + "off");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 序列化会话访问
		// 允许序列访问
		Element allowSerializedSessionAccess = document.getElementById("allowSerializedSessionAccess");
		if (allowSerializedSessionAccess != null) {
			if (allowSerializedSessionAccess.attr("checked").equals("checked")) {
				CommonUtils.output("允许序列访问");
			} else {
				CommonUtils.output("不允许序列访问");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 最长等待时间
		Element maxWaitTime = document.getElementById("maxWaitTime");
		if (maxWaitTime != null) {
			CommonUtils.output(maxWaitTime.attr("value") + maxWaitTime.text());
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 允许访问超时
		Element accessSessionOnTimeout = document.getElementById("accessSessionOnTimeout");
		if (accessSessionOnTimeout != null) {
			if (accessSessionOnTimeout.attr("checked").equals("checked")) {
				CommonUtils.output("允许访问超时");
			} else {
				CommonUtils.output("不允许访问超时");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		CommonUtils.output("\r\n");
	}

	/**
	 * SIP容器
	 * 
	 * @param
	 */
	private void getSIPContainerinfo(String href) {
		CommonUtils.output("\r\nSIP容器信息");

		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 常规属性
		// 最大应用程序会话数
		Element maxAppSessions = document.getElementById("maxAppSessions");
		if (maxAppSessions != null) {
			CommonUtils.output(maxAppSessions.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 每平均时间段最大消息数
		Element maxMessagesPerSecond = document.getElementById("maxMessagesPerSecond");//8.X--6.X
		if (maxMessagesPerSecond != null) {
			CommonUtils.output(maxMessagesPerSecond.attr("value"));
		} else {
			maxMessagesPerSecond = document.getElementById("maxMessageRate");//7.X
			if (maxMessagesPerSecond != null) {
				CommonUtils.output(maxMessagesPerSecond.attr("value"));
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
		}

		// 最大分派队列大小
		Element maxDispatchQueueSize = document.getElementById("maxDispatchQueueSize");
		if (maxDispatchQueueSize != null) {
			CommonUtils.output(maxDispatchQueueSize.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 最大响应时间
		// 启用最大响应时间
		Element enableResponseTime = document.getElementById("enableResponseTime");
		if (enableResponseTime != null) {
			if (enableResponseTime.attr("checked").equals("checked")) {
				CommonUtils.output(enableResponseTime.attr("name") + " checked");
			} else {
				CommonUtils.output(enableResponseTime.attr("name") + " unchecked");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 最大响应时间
		Element maxResponseTime = document.getElementById("maxResponseTime");
		if (maxResponseTime != null) {
			CommonUtils.output(maxResponseTime.attr("value") + "毫秒");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 平均时间段,用来计算平均值的时间段（单位：毫秒）。
		Element averagingPeriod = document.getElementById("averagingPeriod");//6.1
		if (averagingPeriod != null) {
			CommonUtils.output(averagingPeriod.attr("value"));
		} else {
			averagingPeriod = document.getElementById("statAveragePeriod");//8.5
			if (averagingPeriod != null) {
				CommonUtils.output(averagingPeriod.attr("value"));
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
		}

		// 统计信息更新率,容器计算平均值和将统计信息发布至 PMI 的时间间隔。
		Element statisticUpdateRate = document.getElementById("statisticUpdateRate");//6.1
		if (statisticUpdateRate != null) {
			CommonUtils.output(statisticUpdateRate.attr("value"));
		} else {
			statisticUpdateRate = document.getElementById("statUpdateRange");//8.5
			if (statisticUpdateRate != null) {
				CommonUtils.output(statisticUpdateRate.attr("value"));
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
		}

	}

	/**
	 * Web 容器
	 * 
	 * @param href
	 */
	private void getWebContainerInfo(String href) {
		CommonUtils.output("\r\nWeb容器");

		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 启用 servlet 高速缓存
		Element enableServletCaching = document.getElementById("enableServletCaching");
		if (enableServletCaching != null) {
			if (enableServletCaching.attr("checked").equals("checked")) {
				CommonUtils.output("enableServletCaching" + "-->checked");
			} else {
				CommonUtils.output("enableServletCaching" + "-->unchecked");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 禁用 servlet 请求和响应池
		Element disablePooling = document.getElementById("disablePooling");
		if (disablePooling != null) {
			if (disablePooling.attr("checked").equals("checked")) {
				CommonUtils.output("disablePooling" + "-->checked");
			} else {
				CommonUtils.output("disablePooling" + "-->unchecked");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Portlet 容器
	 * 
	 * @param href
	 */
	private void getPortletInfo(String href) {
		CommonUtils.output("\r\nPortlet 容器");

		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 启用 portlet 片段高速缓存
		Element enablePortletCaching = document.getElementById("enablePortletCaching");
		if (enablePortletCaching != null) {
			if (enablePortletCaching.attr("checked").equals("checked")) {
				CommonUtils.output("enablePortletCaching" + "-->checked");
			} else {
				CommonUtils.output("enablePortletCaching" + "-->unchecked");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * EJBContainer
	 * 
	 * @param href
	 */
	private void getEJBContainerInfo(String href) {
		CommonUtils.output("\r\nEJB 容器");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 不活动的池清除时间间隔---指定容器检查可用的 bean 实例池的间隔，以确定是否可以删除一些实例以减少内存的使用。
		Element inactivePoolCleanupInterval = document.getElementById("inactivePoolCleanupInterval");
		if (inactivePoolCleanupInterval != null) {
			CommonUtils.output(inactivePoolCleanupInterval.attr("value") + "ms");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * EJB 高速缓存设置
	 * 
	 * @param href
	 */
	private void getEJBCacheInfo(String href) {
		CommonUtils.output("\r\nEJB 高速缓存设置");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 清除时间间隔-->指定容器尝试从高速缓存中除去未使用的项的间隔，这样可以将项的总数减少到高速缓存大小的值。
		Element cleanupInterval = document.getElementById("cleanupInterval");
		if (cleanupInterval != null) {
			CommonUtils.output(cleanupInterval.attr("value") + "ms");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

		// 高速缓存大小-->指定 EJB 容器内的活动实例列表中的存储区数。
		Element cacheSize = document.getElementById("cacheSize");
		if (cacheSize != null) {
			CommonUtils.output(cacheSize.attr("value") + "存储区");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * EJB 计时器服务设置
	 * 
	 * @param href
	 */
	private void getEJBTimerInfo(String href) {
		CommonUtils.output("\r\nEJB 计时器服务设置");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 使用内部 EJB 计时器服务 Scheduler 实例 OR 使用定制 Scheduler 实例
		Element useInternal = document.getElementById("useInternal");
		if (useInternal != null) {
			if (useInternal.attr("checked").equals("checked")) {// 使用内部 EJB 计时器服务
				// Scheduler 实例
				// 数据源 JNDI 名称-->指定存储持久 EJB 计时器的数据源的名称。
				Element datasourceJNDIName = document.getElementById("datasourceJNDIName");
				for (Element option : datasourceJNDIName.getElementsByTag("option")) {
					if (option.attr("selected").equals("selected")) {
						CommonUtils.output("datasourceJNDIName" + "---> " + option.attr("value"));
					}
				}
				// 表前缀--->指定附加至 Scheduler 表的前缀字符串。如果每个 Scheduler 指定不同的前缀字符串，则多个独立
				// Scheduler 可共享同一数据库。
				Element tablePrefix = document.getElementById("tablePrefix");
				if (tablePrefix != null) {
					CommonUtils.output(tablePrefix.attr("value"));
				} else {
					CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
				}
				// 轮询时间间隔--->指定 Scheduler 轮询数据库以获取到期 EJB 计时器的时间间隔（以秒计）。
				Element pollInterval = document.getElementById("pollInterval");
				if (pollInterval != null) {
					CommonUtils.output(pollInterval.attr("value") + "s");
				} else {
					CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
				}
				// 计时器线程数--->指定用于计时器的所需要的最大线程数。
				Element numAlarmThreads = document.getElementById("numAlarmThreads");
				if (numAlarmThreads != null) {
					CommonUtils.output(numAlarmThreads.attr("value"));
				} else {
					CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
				}
			} else {// 使用定制 Scheduler 实例
				// Scheduler JNDI 名称--->指定要用于管理和维持 EJB 计时器的 Scheduler 实例的 JNDI 名称。
				Element schedulerJNDIName = document.getElementById("schedulerJNDIName");
				for (Element option : schedulerJNDIName.getElementsByTag("option")) {
					if (option.attr("selected").equals("selected")) {
						CommonUtils.output("schedulerJNDIName" + "--->" + option.attr("value"));
					}
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}

	}

	/**
	 * Application profiling 服务
	 * 
	 * @param href
	 */
	private void getApplicationProfilingInfo(String href) {
		CommonUtils.output("\r\nEJB 计时器服务设置");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);

		// 在服务器启动时启用服务--->指定在服务器启动时此服务器是否尝试启动指定的服务。
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable-->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 5.x 兼容性方式--->指定在设置为 True 时，使用 application profiling 的 J2EE 1.3
		// 应用程序的运行状态与它们在 V5.x 发行版中运行状态相同。以此方式操作会导致数据库访问期间发生意外死锁。此处，任务不会在 J2EE 1.3 和
		// J2EE 1.4 应用程序之间的远程调用上传播。此行为可能会导致使用意外的访问意向策略，如果服务器上安装了配置有 application
		// profiling 的应用程序，则此方式还会导致性能降低。在设置为 False 时，使用 application profiling 的 J2EE
		// 1.3 应用程序在运行时使用的约束与 J2EE 1.4
		// 应用程序所使用的相同。在此方式中，仅在开始新工作单元时才建立任务。完整的工作单元只在一个任务下运行。从 V6.0 发行版起，已不支持在 V5.x
		// 兼容性方式设置为 True 的情况下运行的 J2EE 1.3 应用程序。缺省值是 True。
		Element compatibility = document.getElementById("compatibility");
		if (compatibility != null) {
			CommonUtils.output("compatibility-->" + compatibility.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * 事务服务
	 * 
	 * @param href
	 */
	private void getTransactionServiceInfo(String href) {
		CommonUtils.output("\r\nEJB 事务服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// -------------------------------------------
		// 配置
		// 常规属性
		// 总事务生存期超时
		Element totalTranLifetimeTimeout = document.getElementById("totalTranLifetimeTimeout");
		if (totalTranLifetimeTimeout != null) {
			CommonUtils.output("totalTranLifetimeTimeout--->" + totalTranLifetimeTimeout.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 异步响应超时
		Element asyncResponseTimeout = document.getElementById("asyncResponseTimeout");
		if (asyncResponseTimeout != null) {
			CommonUtils.output("asyncResponseTimeout--->" + asyncResponseTimeout.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 客户机非活动超时
		Element clientInactivityTimeout = document.getElementById("clientInactivityTimeout");
		if (clientInactivityTimeout != null) {
			CommonUtils.output("clientInactivityTimeout--->" + clientInactivityTimeout.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 最大事务超时数
		Element propogatedOrBMTTranLifetimeTimeout = document.getElementById("propogatedOrBMTTranLifetimeTimeout");
		if (propogatedOrBMTTranLifetimeTimeout != null) {
			CommonUtils.output("propogatedOrBMTTranLifetimeTimeout--->" + propogatedOrBMTTranLifetimeTimeout.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 试探性重试限制
		Element heuristicRetryLimit = document.getElementById("heuristicRetryLimit");
		if (heuristicRetryLimit != null) {
			CommonUtils.output("heuristicRetryLimit--->" + heuristicRetryLimit.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 试探性重试等待
		Element heuristicRetryWait = document.getElementById("heuristicRetryWait");
		if (heuristicRetryWait != null) {
			CommonUtils.output("heuristicRetryWait--->" + heuristicRetryWait.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 为试探性报告启用记录
		Element enableLoggingForHeuristicReporting = document.getElementById("enableLoggingForHeuristicReporting");
		if (enableLoggingForHeuristicReporting != null) {
			CommonUtils
					.output("enableLoggingForHeuristicReporting" + " ---> " + enableLoggingForHeuristicReporting.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 试探性完成指导
		Element LPSHeuristicCompletion = document.getElementById("LPSHeuristicCompletion");
		if (LPSHeuristicCompletion != null) {
			Elements options = LPSHeuristicCompletion.getElementsByTag("option");
			// COMMIT 落实 || ROLLBACK 回滚 || MANUAL 手工
			for (Element option : options) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("LPSHeuristicCompletion ---> " + option.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用文件锁定
		Element enableFileLocking = document.getElementById("enableFileLocking");
		if (enableFileLocking != null) {
			CommonUtils.output("enableFileLocking ---> " + enableFileLocking.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用协议安全性
		Element enableProtocolSecurity = document.getElementById("enableProtocolSecurity");
		if (enableProtocolSecurity != null) {
			CommonUtils.output("enableProtocolSecurity ---> " + enableProtocolSecurity.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 运行时
		Element tabsItem = document.getElementsByClass("tabs-item").last();
		if (tabsItem != null) {
			String hrefSub = tabsItem.attr("href");
			String response2 = sendGetter.getHtmlResp("/ibm/console/" + hrefSub);
			Document document2 = sendGetter.parseHtmlToDoc(response2);
			// 事务日志目录
			Element mbeanTransactionLogDirectory = document2.getElementById("mbeanTransactionLogDirectory");
			if (mbeanTransactionLogDirectory != null) {
				CommonUtils.output("mbeanTransactionLogDirectory--->" + mbeanTransactionLogDirectory.text());
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
			// 手工事务
			Element mbeanManualTransactions = document2.getElementById("mbeanManualTransactions");
			if (mbeanManualTransactions != null) {
				CommonUtils.output("mbeanManualTransactions--->" + mbeanManualTransactions.text());
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
			// 重试事务
			Element mbeanRetryTransactions = document2.getElementById("mbeanRetryTransactions");
			if (mbeanRetryTransactions != null) {
				CommonUtils.output("mbeanRetryTransactions--->" + mbeanRetryTransactions.text());
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
			// 试探性事务
			Element mbeanHeuristicTransactions = document2.getElementById("mbeanHeuristicTransactions");
			if (mbeanHeuristicTransactions != null) {
				CommonUtils.output("mbeanHeuristicTransactions--->" + mbeanHeuristicTransactions.text());
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
			// 已导入的已就绪事务
			Element mbeanImportedPreparedTransactions = document2.getElementById("mbeanImportedPreparedTransactions");
			if (mbeanImportedPreparedTransactions != null) {
				CommonUtils.output("mbeanImportedPreparedTransactions--->" + mbeanImportedPreparedTransactions.text());
			} else {
				CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * 动态高速缓存服务
	 *
	 * @param href
	 */
	private void getDynamicCacheInfo(String href) {
		CommonUtils.output("\r\n动态高速缓存服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 常规属性
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 高速缓存大小
		// cacheSize
		Element cacheSize = document.getElementById("cacheSize");
		if (cacheSize != null) {
			CommonUtils.output("cacheSize--->" + cacheSize.attr("value") + " 条目");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 缺省优先级
		Element defaultPriority = document.getElementById("defaultPriority");
		if (defaultPriority != null) {
			CommonUtils.output("defaultPriority--->" + defaultPriority.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 磁盘高速缓存设置
		// 启用磁盘减负
		Element enableDiskOffload = document.getElementById("enableDiskOffload");
		if (enableDiskOffload != null) {
			CommonUtils.output("enableDiskOffload--->" + enableDiskOffload.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Compensation 服务
	 *
	 * @param href
	 */
	private void getCompensationServiceInfo(String href) {
		CommonUtils.output("\r\nCompensation 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 恢复日志目录
		Element recoveryLogDirectory = document.getElementById("recoveryLogDirectory");
		if (recoveryLogDirectory != null) {
			CommonUtils.output("recoveryLogDirectory--->" + recoveryLogDirectory.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 恢复日志文件大小
		Element recoveryLogFileSize = document.getElementById("recoveryLogFileSize");
		if (recoveryLogFileSize != null) {
			CommonUtils.output("recoveryLogFileSize--->" + recoveryLogFileSize.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 补偿处理程序重试限制
		Element compensationHandlerRetryLimit = document.getElementById("compensationHandlerRetryLimit");
		if (compensationHandlerRetryLimit != null) {
			CommonUtils.output("compensationHandlerRetryLimit--->" + compensationHandlerRetryLimit.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 补偿处理程序重试时间间隔
		Element compensationHandlerRetryInterval = document.getElementById("compensationHandlerRetryInterval");
		if (compensationHandlerRetryInterval != null) {
			CommonUtils.output("compensationHandlerRetryInterval--->" + compensationHandlerRetryInterval.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Internationalization 服务
	 *
	 * @param href
	 */
	private void getI18NServiceInfo(String href) {
		CommonUtils.output("\r\nInternationalization 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Object pool 服务
	 *
	 * @param href
	 */
	private void getObjectPoolServiceInfo(String href) {
		CommonUtils.output("\r\nObject pool 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * ORB 服务
	 *
	 * @param href
	 */
	private void getObjectRequestBrokerInfo(String href) {
		CommonUtils.output("\r\nORB 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 常规属性
		// 请求超时
		Element requestTimeout = document.getElementById("requestTimeout");
		if (requestTimeout != null) {
			CommonUtils.output("requestTimeout--->" + requestTimeout.attr("value") + "秒");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 请求重试次数
		Element requestRetriesCount = document.getElementById("requestRetriesCount");
		if (requestRetriesCount != null) {
			CommonUtils.output("requestRetriesCount--->" + requestRetriesCount.attr("value") + "重试次数");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 请求重试延迟
		Element requestRetriesDelay = document.getElementById("requestRetriesDelay");
		if (requestRetriesDelay != null) {
			CommonUtils.output("requestRetriesDelay--->" + requestRetriesDelay.attr("value") + "毫秒");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 连接高速缓存最大值
		Element connectionCacheMaximum = document.getElementById("connectionCacheMaximum");
		if (connectionCacheMaximum != null) {
			CommonUtils.output("connectionCacheMaximum--->" + connectionCacheMaximum.attr("value") + "连接");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 连接高速缓存最小值
		Element connectionCacheMinimum = document.getElementById("connectionCacheMinimum");
		if (connectionCacheMinimum != null) {
			CommonUtils.output("connectionCacheMinimum--->" + connectionCacheMinimum.attr("value") + "连接");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// ORB 跟踪
		Element commTraceEnabled = document.getElementById("commTraceEnabled");
		if (commTraceEnabled != null) {
			CommonUtils.output("commTraceEnabled--->" + commTraceEnabled.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 定位请求超时
		Element locateRequestTimeout = document.getElementById("locateRequestTimeout");
		if (locateRequestTimeout != null) {
			CommonUtils.output("locateRequestTimeout--->" + locateRequestTimeout.attr("value") + "秒");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 强制隧道(必需时 || 总是 || 从不)
		Element forceTunnel = document.getElementById("forceTunnel");
		if (forceTunnel != null) {
			Elements options = forceTunnel.getElementsByTag("option");
			for (Element option : options) {
				if (option.attr("selected").equals("selected")) {
					CommonUtils.output("forceTunnel--->" + option.attr("value"));
				}
			}
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 隧道代理 URL
		Element tunnelAgentURL = document.getElementById("tunnelAgentURL");
		if (tunnelAgentURL != null) {
			CommonUtils.output("tunnelAgentURL--->" + tunnelAgentURL.attr("value"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 按引用传递
		Element noLocalCopies = document.getElementById("noLocalCopies");
		if (noLocalCopies != null) {
			CommonUtils.output("noLocalCopies--->" + noLocalCopies.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Startup bean 服务
	 *
	 * @param href
	 */
	private void getStartupBeansServiceInfo(String href) {
		CommonUtils.output("\r\nStartup bean 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * ActivitySession 服务
	 *
	 * @param href
	 */
	private void getActivitySessionServiceInfo(String href) {
		CommonUtils.output("\r\nActivitySession 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 缺省超时
		Element defaultTimeout = document.getElementById("defaultTimeout");
		if (defaultTimeout != null) {
			CommonUtils.output("defaultTimeout-->" + defaultTimeout.attr("value") + "秒");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * Work Area 服务
	 *
	 * @param href
	 */
	private void getWorkAreaServiceInfo(String href) {
		CommonUtils.output("\r\nWork Area 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 最大发送大小
		Element maxSendSize = document.getElementById("maxSendSize");
		if (maxSendSize != null) {
			CommonUtils.output("maxSendSize--->" + maxSendSize.attr("value") + "字节");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 最大接收大小
		Element maxReceiveSize = document.getElementById("maxReceiveSize");
		if (maxReceiveSize != null) {
			CommonUtils.output("maxReceiveSize--->" + maxReceiveSize.attr("value") + "字节");
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用 Web Service 传播
		Element enableWebServicePropagation = document.getElementById("enableWebServicePropagation");
		if (enableWebServicePropagation != null) {
			CommonUtils.output("enableWebServicePropagation--->" + enableWebServicePropagation.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}

	/**
	 * SIB 服务
	 *
	 * @param href
	 */
	private void getSIBServiceInfo(String href) {
		CommonUtils.output("\r\nSIB 服务");
		String response = sendGetter.getHtmlResp("/ibm/console/" + href);
		Document document = sendGetter.parseHtmlToDoc(response);
		// 在服务器启动时启用服务
		Element enable = document.getElementById("enable");
		if (enable != null) {
			CommonUtils.output("enable--->" + enable.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
		// 启用配置重新装入
		Element configurationReloadEnabled = document.getElementById("configurationReloadEnabled");
		if (configurationReloadEnabled != null) {
			CommonUtils.output("configurationReloadEnabled--->" + configurationReloadEnabled.attr("checked"));
		} else {
			CommonUtils.output(StackTraceUtil.getCurrentSourceLineInfoString() + "########为空#########");
		}
	}
}
