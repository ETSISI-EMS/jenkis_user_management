import jenkins.model.Jenkins
import hudson.model.User

import jenkins.*
import hudson.*
import jenkins.model.*
import hudson.model.*
import hudson.security.*
import groovy.json.*
import org.jenkinsci.plugins.matrixauth.*

def clear_roles (strategy, user) {
    def permissions = strategy.getGrantedPermissionEntries()
    permissions.each { k, v ->
        def prototype = new PermissionEntry(AuthorizationType.USER, user)
        if (v.contains(prototype)) {
            permissions[k].remove(prototype)
        }
    }
}

// Users to keep
def user_important = new JsonSlurper().parseText(args[0])

// configure security
def instance = jenkins.model.Jenkins.getInstance()
def strategy = instance.getAuthorizationStrategy()

// make a set with the users to delete
def users = User.getAll().collect { it.id }
def users_to_delete = users - user_important.users.collect { it.name }

users_to_delete.each { user ->
    def ju = Jenkins.get().getUser(user)
    if (ju != null) {
        clear_roles(strategy, user)
        ju.delete()
    }
}

println "The following users were deleted: " + users_to_delete

instance.save()