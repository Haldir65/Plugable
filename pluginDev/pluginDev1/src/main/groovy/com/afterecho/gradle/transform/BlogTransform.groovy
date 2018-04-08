package com.afterecho.gradle.transform

import com.afterecho.gradle.util.MyInject
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class BlogTransform extends Transform {


    Project project

    BlogTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "BlogDoNothing"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return Collections.singleton(QualifiedContent.Scope.PROJECT)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        println '==================Plugin transform start=================='

        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        def outDir = outputProvider.getContentLocation("awesome", outputTypes, scopes, Format.DIRECTORY)

        outDir.deleteDir()
        outDir.mkdirs()

//        project.logger.error("catch me ${outDir.path}")
        // ..\Plugable\androidProjects\app\build\intermediates\transforms\BlogDoNothing\debug\0

        transformInvocation.inputs.each { TransformInput transformInput ->

            // 首先是directory
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
//                def dst = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
//                project.logger.error("=directoryInputs======${dst}======") // ..\Plugable\androidProjects\app\build\intermediates\transforms\BlogDoNothing\debug\0
//                println(it.getFile().getAbsolutePath()) // ..\Plugable\androidProjects\app\build\intermediates\classes\debug
//                int pathBitLen = directoryInput.file.toString().length()
//                directoryInput.file.traverse {
//                    def path = "${directoryInput.toString().substring(pathBitLen)}"
////                    project.logger.error("${it.getAbsoluteFile().path}") // ..\Plugable\androidProjects\app\build\intermediates\classes\debug\android\support\transition\R$layout.class
//                    project.logger.error("${directoryInput.path}")
//                    if (directoryInput.isDirectory()){
////                        new File(outDir,path).mkdirs()
//                    }else {
//                        if(!path.endsWith("BuildConfig.class")) {
////                            new File(outDir, path).bytes = it.bytes
//                        }
//                    }
//                }

                MyInject.injectDir(directoryInput.file.absolutePath,"com\\me\\harris\\bookstore",project)
                // 获取output目录
//                MyInject.inject(directoryInput.file.absolutePath, project)
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)



            }

            // 接下来遍历jar文件
            transformInput.jarInputs.each { JarInput jarInput ->
                def dst = outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                project.logger.error("===jarInputs==========${jarInput.file.path}===================")
            }
        }
        println("===========hey guys =====================")
    }
}

