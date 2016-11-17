package xuyihao.JsoupTest.function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xuyihao.JsoupTest.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;

/**
 * Created by Xuyh at 2016/11/17 下午 05:12.
 */
public class WebServerInformationSearch {
    private RequestSendGetter sendGetter = null;

    public WebServerInformationSearch(RequestSendGetter requestSendGetter){
        sendGetter = requestSendGetter;
    }

    public void searchWebServerInfomation(String href){
        String response = sendGetter.getHtmlResp("/ibm/console/" + href);
        Document doc = sendGetter.parseHtmlToDoc(response);

        //Web 服务器名称
        Element serverName = doc.getElementById("serverName");
        CommonUtils.output("serverName--->" + serverName.text());
        //类型
        Element webServerType = doc.getElementsByAttributeValue("name", "webServerType").last();
        for(Element option : webServerType.getElementsByTag("option")){
            if(option.attr("selected").equals("selected")){
                CommonUtils.output("webServerType--->" + option.text());
            }
        }
        //主机名
        Element hostname = doc.getElementById("hostname");
        CommonUtils.output("hostname--->" + hostname.text());
        // 端口
        Element port = doc.getElementById("port");
        CommonUtils.output("port--->" + port.attr("value"));
        //Web 服务器安装位置
        Element installPath = doc.getElementsByAttributeValue("name", "installPath").last();
        CommonUtils.output("installPath--->" + installPath.attr("value"));
        // 配置文件名
        Element configFileName = doc.getElementsByAttributeValue("name", "configFileName").last();
        CommonUtils.output("configFileName--->" + configFileName.attr("value"));
        //服务名称
        Element serviceName = doc.getElementsByAttributeValue("name", "serviceName").last();
        CommonUtils.output("serviceName--->" + serviceName.attr("value"));
        //平台类型
        Element platformType = doc.getElementById("platformType");
        CommonUtils.output("platformType--->" + platformType.text());
    }
}
