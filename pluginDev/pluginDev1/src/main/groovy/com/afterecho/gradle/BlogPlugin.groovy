package com.afterecho.gradle

import com.afterecho.gradle.extensions.BlogPluginExtension
import com.afterecho.gradle.task.ShowDevicesTask
import com.afterecho.gradle.task.WordsToEnumTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class BlogPlugin implements Plugin<Project> {


    @Override
    void apply(Project target) {

        target.tasks.create(name: "showDevices", type: ShowDevicesTask)
//        def showDevicesTask = target.tasks.create("showDevices") << {
//            def adbExe = target.android.getAdbExe().toString()
//            println "${adbExe} devices".execute().text
//        }
//        showDevicesTask.group = "blogplugin"
//        showDevicesTask.description = "Runs adb devices command"
        target.extensions.create('bpplugin', BlogPluginExtension)
        target.android.applicationVariants.all { variant ->
            File inputWordFile = new File(target.projectDir, target.extensions.bpplugin.words)
            File outputDir = new File(target.buildDir, "generated/source/wordsToEnum/${variant.dirName}")
            def task = target.tasks.create(name: "wordsToEnum${variant.name.capitalize()}", type: WordsToEnumTask){
                outDir = outputDir
                wordsFile = inputWordFile
                enumClassName = target.extensions.bpplugin.enumClass
                outputPackageName = target.extensions.bpplugin.outputPackage
            }
            variant.registerJavaGeneratingTask task, outputDir
        }


    }
}

