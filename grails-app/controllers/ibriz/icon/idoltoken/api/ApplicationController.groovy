package ibriz.icon.idoltoken.api

import grails.core.GrailsApplication
import grails.util.Environment
import grails.plugins.*
import org.grails.web.json.JSONObject

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def index() {
        println "TTTTTTTTTT"
        [grailsApplication: grailsApplication, pluginManager: pluginManager]
    }
    def test(){
        render([test: "TESTSETSETSETS"] as JSONObject)
    }
}
