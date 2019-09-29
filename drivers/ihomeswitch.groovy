/*
 * iHome Switch
 *
 * Based off of simple URL switch https://github.com/hubitat/HubitatPublic/blob/master/examples/drivers/httpGetSwitch.groovy
 * 
 */
metadata {
    definition(name: "iHomeSwitch", namespace: "syzygy", author: "syzygy") {
        capability "Actuator"
        capability "Switch"
        capability "Sensor"
        attribute "Switch", "string"
    }
}

preferences {
}

def updated() {
}

def parse(String description) {
}

def powerState(power) {
    def key = getParent().getApiKey()
    if (key) {
    def url = "https://api.evrythng.com/thngs/${device.deviceNetworkId}/actions/${power}"
    log.debug "Sending on PUT request to ${url}"
    
    def Params = [
        uri     : url,
                body    : "{\"type\":\"${power}\"}",
                headers : ["Authorization": "${key}",
                           "Content-Type": "application/json",
                           "Accept": "application/json",
                           ]
        ]
    log.debug Params
    try{
      httpPost(Params) { resp -> 
         if (resp.status == 200) {
            log.debug "Success"
         }
      }
    } catch(e) {
            log.debug "ERROR in httpPost: ${e}"
    }
    }
    else {
        log.debug "No API Key Found"
    }
}

def on() {
    powerState("_turnOn")
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
    powerState("_turnOff")
    sendEvent(name: "switch", value: "off", isStateChange: true)
}
