import groovy.json.JsonOutput

// Thanks to https://scott.stevensononthe.net/2017/01/reverse-engineering-the-ihome-isp5-smartplug-communications/ 
// for the begninning pointers 'integrating' with iHome

definition(
    name: "iHome",
    namespace: "syzygy",
    author: "Keith Baker",
    description: "iHome",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "")

preferences {
	page(name: "mainPage")
    page(name: "devicesPage")
    page(name: "addDevices")
    page(name: "loginPage")
}

def login(username, password){
    def Params = [
                uri   : "https://www.ihomeaudio.com/api/v3/login/",
                body : "{\"password\": \"${password}\", \"email\": \"${username}\"}",
                headers : ["Content-Type": "application/json"]
        ]
    try{
      httpPost(Params) { resp -> 
         if (resp.status == 200) {
           log.debug "reply: ${resp.data}"
           log.debug "user_id: ${resp.data.evrythng_user_id}"
           state.evrythng_user_id = resp.data.evrythng_user_id
           log.debug "evrythng_api_key: ${resp.data.evrythng_api_key}"
           state.evrythng_api_key = resp.data.evrythng_api_key
         }
      }
    } catch(e) {
            log.debug "ERROR in httpPost: ${e}"
    }
}

def loginPage(){
	dynamicPage(name: "loginPage", title: "Login", nextPage: "mainPage") {
        section("Login") {
            input "thisUsername", "text", title: "iHome Username", submitOnChange: true
            input "thisPassword", "text", title: "iHome Password", submitOnChange: true, type: "password"
            if (thisUsername && thisPassword){
                login(thisUsername, thisPassword)
            }
        }
    }
}

def getApiKey() {
    return state.evrythng_api_key
}

def mainPage() {
	dynamicPage(name: "mainPage", title: " ", install: true, uninstall: true) {
		section("Login"){
            href name: "loginPageLink", title: "Login", description: "", page: "loginPage"
		}
        section("Device Setup") {
            href name: "devicesPageLink", title: "Select Devices", description: "", page: "devicesPage"
        }
	}
}

def devicesPage() {
    def options = [:]
    def devices = [:]
    def Params = [
                uri   : "https://api.evrythng.com/thngs",
                body  : '{\"perPage\": 100,\"sortOrder\": \"ASCENDING\"}',
                headers : ["Accept": "application/json",
                           "Content-Type": "application/json",
                           "Authorization": state.evrythng_api_key]
        ]
    log.debug "${Params}"
    try{
      httpGet(Params) { resp -> 
         if (resp.status == 200) {
           log.debug "reply: ${resp.data}"
           resp.data.each {
               options[it.id] = "${it.name} (${it.identifiers.serial_num})"
               devices[it.id] = ["name": it.name,
                   "room_name": it.customFields.room_name,
                   "type": it.customFields.service_type,
                   "product": it.product,
                   "serial": it.identifiers.serial_num]
           }
         }
      }
    } catch(e) {
            log.debug "ERROR in httpPost: ${e}"
        log.debug resp.data
    }
    state.devices = devices
    return dynamicPage(name: "devicesPage", title: " ", nextPage: "addDevices") {
        section("Devices") {
            input "selectedDevices", "enum", required:false, title:"Select iHome Devices", multiple:true, options:options
        }
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
    app.updateLabel("iHome")
}

def handler(evt) {
}

def getDevices() {
    state.devices = state.devices ?: [:]
}

def addDevices() {
    def devices = getDevices()
    def sectionText = ""

    selectedDevices.each { dni ->bridgeLinking
        log.debug "Processing Selected Device ${dni}"
        def selectedDevice = devices.find { it.value.id == dni }
        def d
        if (selectedDevice) {
            log.debug "Device Exists ${selectedDevice}"
            d = getChildDevices()?.find {
                it.id == selectedDevice.value.id
            }
        }
        
        if (!d) {
            log.debug "Creating iHome Device with Id: ${dni}"

            try {
                def newDevice = addChildDevice("syzygy", "iHomeSwitch", dni, location.hubs[0].id, [
                    "label": devices[dni].name,
                    "id": devices[dni].name,
                    completedSetup: true
                    ]
                )
                sectionText = sectionText + "Succesfully added iHome device with id ${dni} \r\n"
            } catch (e) {
                sectionText = sectionText + "An error occured ${e} \r\n"
            }   
        }
	} 
    log.debug sectionText
    return dynamicPage(name:"addDevices", title:"Devices Added", nextPage:"mainPage",  uninstall: true) {
        if(sectionText != ""){
		    section("Add iHome Results:") {
			     paragraph sectionText
		}
        }
        else{
            section("No devices added") {
			    paragraph "All selected devices have previously been added"
            }
        }
    }
}

def uninstalled() {
    getChildDevices().each {
        deleteChildDevice(it.deviceNetworkId)
    }
}
