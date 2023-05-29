// inlcude the jenkins api

import jenkins.*
import hudson.*
import jenkins.model.*
import hudson.model.*
import hudson.security.*
import groovy.json.*
import org.jenkinsci.plugins.matrixauth.*
import org.jenkinsci.plugins.workflow.cps.replay.*

// CHANGE THIS VARIABLE TO EDIT THE DEFAULT ROLES OF AN USER
roles = [
    //Nodo
    hudson.model.Computer.BUILD,
    hudson.model.Computer.CONFIGURE,
    hudson.model.Computer.CONNECT,
    hudson.model.Computer.CREATE,
    hudson.model.Computer.DELETE,
    hudson.model.Computer.DISCONNECT,
    // Tarea
    hudson.model.Item.BUILD,
    hudson.model.Item.CANCEL,
    hudson.model.Item.CONFIGURE,
    hudson.model.Item.CREATE,
    hudson.model.Item.DELETE,
    hudson.model.Item.DISCOVER,
    com.cloudbees.hudson.plugins.folder.relocate.RelocationAction.RELOCATE,
    hudson.model.Item.READ,
    hudson.model.Item.WORKSPACE,
    hudson.model.Item.EXTENDED_READ,
    // Ejecutar
    hudson.model.Run.DELETE,
    hudson.model.Run.UPDATE,
    ReplayAction.REPLAY,
    // Vistas
    hudson.model.View.CONFIGURE,
    hudson.model.View.CREATE,
    hudson.model.View.DELETE,
    hudson.model.View.READ,
    // SCM
    hudson.scm.SCM.TAG,
    
    // Lockable Resources
    //org.jenkins.plugins.lockableresources.actions.LockableResourcesRootAction.UNLOCK,
    //org.jenkins.plugins.lockableresources.actions.LockableResourcesRootAction.VIEW,
    //org.jenkins.plugins.lockableresources.actions.LockableResourcesRootAction.STEAL,
    //org.jenkins.plugins.lockableresources.actions.LockableResourcesRootAction.RESERVE
]

def clear_roles (strategy, user) {
    def permissions = strategy.getGrantedPermissionEntries()
    permissions.each { k, v ->
        def prototype = new PermissionEntry(AuthorizationType.USER, user)
        if (v.contains(prototype)) {
            permissions[k].remove(prototype)
        }
    }
}

println "reading users..."

//read json text from first argument
def json = new JsonSlurper().parseText(args[0])

//print number of users read
println "read " + json.users.size() + " users"

// get the jenkins instance
def instance = jenkins.model.Jenkins.getInstance()
def strategy = instance.getAuthorizationStrategy()

// create a new jenkins users with the credentials
json.users.each { user ->
    println user.name

    // check if user.name exists
    if (instance.securityRealm.getUser(user.name) == null) {
       instance.securityRealm.createAccount(user.name, user.pwd)
       println "created user: " + user.name
    } else {
        clear_roles(strategy, user.name)
    }

    // set permissions
    strategy.add(jenkins.model.Jenkins.READ, new PermissionEntry(AuthorizationType.USER, user.name))
    roles.each { role->
        strategy.add(role, new PermissionEntry(AuthorizationType.USER, user.name))
    }

}

instance.save()
