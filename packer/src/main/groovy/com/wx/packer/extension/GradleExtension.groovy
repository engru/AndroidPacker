package com.wx.packer.extension

import org.gradle.api.Project
import javax.inject.Inject


class GradleExtension {
    /** Project referenced by this plugin extension */
    final Project project

    String product
    VersionOption version
    FileOutputOption output
    DeployOption deploy


    @Inject
    GradleExtension(Project project){
        this.project = project
        product = project.name
        version = new VersionOption(project)
        output = new FileOutputOption()
        deploy = new DeployOption()
    }


    void version(Closure c) {
        project.configure(version, c)
    }

    void output(Closure c){
        project.configure(output, c)
    }

    void deploy(Closure c){
        project.configure(deploy, c)
    }


    int getVersionCode(){
        return version.version
    }

    String getVersionName(){
        return version.name
    }

}