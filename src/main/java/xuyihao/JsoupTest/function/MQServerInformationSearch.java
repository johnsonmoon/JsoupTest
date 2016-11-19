package xuyihao.JsoupTest.function;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xml.internal.security.utils.EncryptionElementProxy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xuyihao.JsoupTest.RequestSendGetter;
import xuyihao.JsoupTest.util.CommonUtils;

/**
 * Created by Xuyh at 2016/11/18 下午 03:36.
 */
public class MQServerInformationSearch {
    private RequestSendGetter sendGetter;

    public MQServerInformationSearch(RequestSendGetter sendGetter) {
        this.sendGetter = sendGetter;
    }

    public void searchMQServerInformation(String href){
        String response = sendGetter.getHtmlResp("/ibm/console/" + href);
        Document doc = sendGetter.parseHtmlToDoc(response);
        //UUID
        Element uuid = doc.getElementById("uuid");
        CommonUtils.output("uuid--->" + uuid.text());
        //服务器类型
        Elements MQServerType = doc.getElementsByAttributeValue("name", "type");
        for(Element type : MQServerType){
            if(type.attr("checked").equals("checked")){
                CommonUtils.output("MQServerType--->" + type.attr("value"));
            }
        }
        //使用绑定传输方式
        Element useBindingsMode = doc.getElementById("useBindingsMode");
        CommonUtils.output("useBindingsMode--->" + useBindingsMode.attr("checked"));
        //WebSphere MQ 端口
        Element port = doc.getElementById("port");
        CommonUtils.output("port--->" + port.attr("value"));
        //传输链名称
        Element transportChainNameSelect = doc.getElementById("transportChainNameSelect");
        Elements options = transportChainNameSelect.getElementsByTag("option");
        for (Element option : options){
            if(option.attr("selected").equals("selected")){
                CommonUtils.output("transportChainNameSelect--->" + option.attr("value"));
            }
        }
        //WebSphere MQ 通道
        Element channel = doc.getElementById("channel");
        CommonUtils.output("channel--->" + channel.attr("value"));
        //消息传递认证别名
        Element secAuthAlias = doc.getElementById("secAuthAlias");
        Elements options2 = secAuthAlias.getElementsByTag("option");
        for(Element option2 : options2){
            if(option2.attr("selected").equals("selected")){
                CommonUtils.output("secAuthAlias--->" + option2.attr("value"));
            }
        }
        //在消息中接收到的信任用户标识
        Element trustUserIdentifiers = doc.getElementById("trustUserIdentifiers");
        CommonUtils.output("trustUserIdentifiers--->" + trustUserIdentifiers.attr("checked"));
        //自动发现资源
        Element autoDiscovResources = doc.getElementById("autoDiscovResources");
        CommonUtils.output("autoDiscovResources--->" + autoDiscovResources.attr("checked"));
        //资源发现认证别名
        Element discovAuthAlias = doc.getElementById("discovAuthAlias");
        Elements options3 = discovAuthAlias.getElementsByTag("option");
        for(Element option3 : options3){
            if(option3.attr("selected").equals("selected")){
                CommonUtils.output("discovAuthAlias--->" + option3.attr("value"));
            }
        }
        //应答队列
        Element replyToQueue = doc.getElementById("replyToQueue");
        CommonUtils.output("replyToQueue--->" + replyToQueue.attr("value"));
    }
}
