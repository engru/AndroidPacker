package com.wx.packer

import com.android.build.gradle.api.BaseVariant
import com.android.builder.Version
import com.wx.packer.extension.GradleExtension
import com.wx.packer.model.VersionType
import com.wx.packer.task.GradleTask
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePlugin implements Plugin<Project> {

    private final String PLUGIN_NAME = "packer"

    Project project;
    GradleExtension extension
    private def templateEngine = new SimpleTemplateEngine()

    @Override
    void apply(Project project) {
        this.project = project
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw new PluginException(
                    "'com.android.application' plugin must be applied", null)
        }
        if (new StringVersion(Version.ANDROID_GRADLE_PLUGIN_VERSION)
                < new StringVersion("2.2.0")) {
            throw new PluginException(
                    "'com.android.tools.build:gradle' must be v2.2.0 or above", null)
        }
        //project.configurations.create(PLUGIN_NAME).extendsFrom(project.configurations.compile)


        extension = project.extensions.create(PLUGIN_NAME, GradleExtension,project)

        project.afterEvaluate {

            String dependentTask = "assembleRelease"
            for(String taskName in project.gradle.startParameter.taskNames) {

                //println("--taskName="+taskName)
                if (taskName.toLowerCase(Locale.ENGLISH)
                        .contains(dependentTask.toLowerCase(Locale.ENGLISH))
                    && extension.version.type == VersionType.TYPE_PROP) {
                    //println("--store version--")
                    def versionPropsFile = extension.version.versionFile
                    if (versionPropsFile != null && versionPropsFile.canRead()) {
                        def Properties versionProps = new Properties()
                        versionProps.load(new FileInputStream(versionPropsFile))
                        def code = extension.version.versionCode

                        versionProps['VERSION_CODE'] = code.toString()
                        versionProps.store(versionPropsFile.newWriter(), null)
                    }
                }
            }

            project.android.applicationVariants.all { BaseVariant variant ->
                if(extension.output.renameOutput){
                    extension.output.generateOutputName(project,variant)
                }
                //generateOutput(variant)
                //addTasks(variant)
            }
        }

    }

    void generateOutput(BaseVariant variant){
        //println("---genenrate--output--")

        def map = [
                'appName'    : extension.product,
                'projectName': project.rootProject.name,
                'flavorName' : variant.flavorName,
                'buildType'  : variant.buildType.name,
                'versionName': variant.versionName,
                'versionCode': variant.versionCode
        ]

        def defaultTemplate = !variant.flavorName.equals("") && variant.flavorName != null ?
                '$appName-$flavorName-$buildType-$versionName' : '$appName-$buildType-$versionName'
        def template = extension.output.na == null ? defaultTemplate : nameFormat
        def fileName = templateEngine.createTemplate(template).make(map).toString();
        if (variant.buildType.zipAlignEnabled) {
            def file = variant.outputs[0].outputFile
            variant.outputs[0].outputFile = new File(file.parent, fileName + ".apk")
        }

        def androidGradlePlugin = AdvancedBuildVersionPlugin.getAndroidPluginVersion(project)
        if (androidGradlePlugin != null && androidGradlePlugin.version.equals("1.3.0")) {
            // android gradle 1.3.0 bug: https://code.google.com/p/android/issues/detail?id=182248
            project.getLogger().log(LogLevel.WARN, "could not make unaligned file. You should use android gradle 1.3.1 and above")
            return;
        }
        def file = variant.outputs[0].packageApplication.outputFile
        variant.outputs[0].packageApplication.outputFile =
                new File(file.parent, fileName + "-unaligned.apk")
    }


    void addTasks(BaseVariant vt) {
        debug("addTasks() for ${vt.name}")
        def variantTask = project.task("apk${vt.name.capitalize()}",
                type: GradleTask) {
            variant = vt
            extension = project.packer
            dependsOn vt.assemble
        }

        debug("addTasks() new variant task:${variantTask.name}")

        def buildTypeName = vt.buildType.name
        if (vt.name != buildTypeName) {
            def taskName = "apk${buildTypeName.capitalize()}"
            def task = project.tasks.findByName(taskName)
            if (task == null) {
                task = project.task(taskName)
            }
            task.dependsOn(variantTask)
            debug("addTasks() build type task ${taskName}")


        }
    }

    void debug(String msg) {
        project.logger.info(msg)
    }
}