/*!Action 
 action.name=发现webSphere基本信息（http）
 action.descr=通过http发现webSphere中间件信息
 action.version=1.0.0
 action.protocols=websphere
 discovery.output=Middleware
 discovery.identifier=$node.ip/websphere/$websphere.params.port
 */

def isExist = $websphere.isExist();

if (isExist) {
    def instance = createInstance();
	def isLogon = $websphere.logon();

    if(isLogon){

        getWebSphereBaseInfo(instance);



    }else{
        $logger.logError("webphere管理台无法登录_"+e.getMessage());
    }


	return $ci;
}

def getWebSphereBaseInfo(instance){

    def resp = $websphere.getHtmlResp("/ibm/console/navigation.do?wpageid=com.ibm.isclite.welcomeportlet.layoutElement.A&moduleRef=com.ibm.isclite.ISCAdminPortlet");

    def doc = $websphere.parseHtmlToDoc(resp);

    def version = doc.getElementsByClass("portlet-table-body").last();
    
}




def createInstance() {

	def webSphereInstance = $ci.create("Middleware", "WebSphere", "WebSphere_"+$websphere.params.ip);
    webSphereInstance.ip = $websphere.params.ip;
    webSphereInstance.port = $websphere.params.port;
    $ci.createRelationship("RunsOn", webSphereInstance.id, $node.ciId);

	return webSphereInstance;
}

