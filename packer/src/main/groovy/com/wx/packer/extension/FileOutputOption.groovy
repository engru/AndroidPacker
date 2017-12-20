package com.wx.packer.extension

import com.android.build.gradle.api.BaseVariant
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

class FileOutputOption {

    String nameFormat

    boolean renameOutput = false

    private def templateEngine = new SimpleTemplateEngine()

    void renameOutput(boolean b) {
        renameOutput = b
    }

    void nameFormat(String format) {
        nameFormat = format
    }

    def generateOutputName(Project project, BaseVariant variant) {
        println("varint--"+variant.name)
        def map = [
                'appName'    : project.packer.product,
                'projectName': project.rootProject.name,
                'flavorName' : variant.flavorName,
                'buildType'  : variant.buildType.name,
                'versionName': variant.versionName,
                'versionCode': variant.versionCode
        ]

        def defaultTemplate = !variant.flavorName.equals("") && variant.flavorName != null ?
                '$appName-$flavorName-$buildType-$versionName' : '$appName-$buildType-$versionName'
        def template = nameFormat == null ? defaultTemplate : nameFormat
        def fileName = templateEngine.createTemplate(template).make(map).toString();
//        if (variant.buildType.zipAlignEnabled) {
//            def file = variant.outputs[0].outputFile
//            variant.outputs[0].outputFile = new File(file.parent, fileName + ".apk")
//        }
//
//        def file = variant.outputs[0].packageApplication.outputFile
//        variant.outputs[0].packageApplication.outputFile =
//                new File(file.parent, fileName + "-unaligned.apk")

        //println("iszipAnligen --"+variant.buildType.zipAlignEnabled)

        variant.outputs.all { output ->
            //println("outputfile --"+output.outputFile.name)
            def oldfile = output.outputFile

            if(variant.buildType.name.equals('release') && oldfile != null && oldfile.name.endsWith('.apk')) {
                output.outputFile = new File(oldfile.parent, fileName + ".apk")
                outputFileName = fileName + ".apk"
            }

            //println("outputfile new --"+output.outputFile.name)

        }
    }
}