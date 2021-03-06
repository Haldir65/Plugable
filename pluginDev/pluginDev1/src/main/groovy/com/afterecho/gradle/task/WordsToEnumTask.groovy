package com.afterecho.gradle.task

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import org.gradle.api.tasks.Input

import javax.lang.model.element.Modifier

import static com.squareup.javapoet.TypeSpec.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction



class WordsToEnumTask extends DefaultTask{

    String group = "blogplugin"
    String description = "Makes a list of words into an enum"

    @InputFile
    File wordsFile

    @OutputDirectory
    File outDir

    @Input
    String enumClassName

    @Input
    String outputPackageName

    @TaskAction
    def makeWordsIntoEnums() {
//        println wordsFile.absolutePath
//        println outDir.absolutePath

//        outDir.deleteDir()

        Builder wordsEnumBuilder = enumBuilder(enumClassName ).addModifiers(Modifier.PUBLIC)
        wordsFile.readLines().each {
            wordsEnumBuilder.addEnumConstant(it).build()
        }
        TypeSpec wordsEnum = wordsEnumBuilder.build()
        JavaFile javaFile = JavaFile.builder(outputPackageName, wordsEnum).build();
        javaFile.writeTo(outDir)
    }
}
