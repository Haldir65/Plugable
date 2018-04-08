package com.afterecho.gradle.util

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import org.gradle.api.Project

public class MyInject {
    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"inster before =======\") ; "

    public static void injectDir(String path, String packageName,Project project) {
        pool.appendClassPath(path)
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle")
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {
                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName)
                    boolean isMyPackage = index != -1
                    println("====${filePath}=====${isMyPackage?"is my class ":"is not my class"}=========")
                    if (isMyPackage) {
                        int end = filePath.length() - 6 // .class 这几个字正好6位
                        String className = filePath.substring(index, end)
                                .replace('\\', '.').replace('/', '.')

                        //开始修改class文件
                        CtClass c = pool.getCtClass(className)
                        println("className is ${className}")
                        if (c.isFrozen()) {
                            c.defrost()
                        }
                        String name = file.getName()

                        String importStr = "com.me.harris.bookstore.MobclickAgent"
                        String importApp = "com.me.harris.bookstore.App"
                        pool.importPackage(importStr)
                        pool.importPackage(importApp)

                        if (name == "MainActivity.class") {
                            injectActivity(c, path)
                        }else if (name.contains("MainActivity")) {
                            injectAnoumousClasses(c,path)
                        }
                            injectConstructors(c, path, filePath)
                    }
                }
            }
        }
    }

    private static void injectConstructors(CtClass c,String path,String filePath) {
        if (c.isFrozen()) {
            c.defrost()
        }
        CtConstructor[] cts = c.getDeclaredConstructors()
        if (cts == null || cts.length == 0) {
            //手动创建一个构造函数
            CtConstructor constructor = new CtConstructor(new CtClass[0], c)
//                            constructor.insertBeforeBody(injectStr)
            constructor.insertBeforeBody("System.out.println(\"insert  before = "+filePath+" ======\") ; ")
            c.addConstructor(constructor)
        } else {
            //如果已经有构造函数，则添加一行打印代码
//                            cts[0].insertBeforeBody(injectStr)
            cts[0].insertBeforeBody("System.out.println(\"insert  before = "+filePath+" ======\") ; ")

        }
        c.writeFile(path)
        c.detach()
    }

    private static void injectAnoumousClasses(CtClass c, String path) {
        for(CtMethod method : c.getDeclaredMethods()){
            println("匿名内部类的方法找到一个 : " + method.getName())
            if (c.isFrozen()) {
                c.defrost()
            }
            if(checkOnClickMethod(method)){
                println("开始在匿名内部类的 onClick中插入方法 : " + method.getName() )
                injectMethod(method)
            }
        }
    }

    private static void injectActivity(CtClass c,String path) {
        //获取到OnCreate方法

        CtMethod ctMethod = c.getDeclaredMethod("onCreate")

        println("方法名 = " + ctMethod)

        String insetBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
                                                """
        //在方法开头插入代码
        ctMethod.insertBefore(insetBeforeStr)
//        c.writeFile(path)


        for(CtMethod method : c.getDeclaredMethods()){
//            println("CtMethod my : " + method.getName())
            if (c.isFrozen()) {
                c.defrost()
            }
            if(checkOnClickMethod(method)){
                println("开始在onClick中插入方法 : " + method.getName() )
                injectMethod(method)
            }
        }
        c.writeFile(path)
    }

    private static boolean checkOnClickMethod(CtMethod method ){
        return method.getName().endsWith("onClick")  && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].getName().equals("android.view.View") ;
    }

    private static void injectMethod(CtMethod method){
        method.insertAfter("System.out.println((\$1).getTag());")
        method.insertAfter("MobclickAgent.onEvent(App.getApp(), (\$1).getTag().toString());")
        //javaAssist中 美元符号1代表方法的第1个参数
    }

}
