package com.epam.classloading.runner;

import com.epam.classloading.classloader.JarClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);
    private static final String PATH_TO_JARS = "jars/";

    public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        Scanner scanner = new Scanner(System.in);

        while(true) {
            showMenu();
            int choice = Integer.valueOf(scanner.nextLine());

            switch(choice) {
                case 1:
                    selectJar("Module1.jar");
                    break;
                case 2:
                    selectJar("Module2.jar");
                    break;
                case 3:
                    selectJar("Module3.jar");
                    break;
                case 4:
                    System.out.println("Application will exit");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Selected option is not available");
                    break;

            }
        }

    }

    private static void selectJar(String jarFileName) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        JarClassLoader jarClassLoader = new JarClassLoader(jarFileName, Main.class.getClassLoader());
        jarClassLoader.loadJar();

        Class<?> clazz = jarClassLoader.loadClass("com.epam.modules.impl.Module");
        Object moduleInstance = clazz.newInstance();

        Method runMethod = clazz.getDeclaredMethod("run");
        runMethod.invoke(moduleInstance);

        LOG.debug("Classloader: " + clazz.getClassLoader());

        // Instead of our own classloader we can also use URLClassloader. It's doing the same job
        // But for more flexibility custom classloader can be used

//        URL jarLocation = new URL("file:/" + PATH_TO_JARS + jarFileName);
//        LOG.debug("Jar location: " + jarLocation);
//        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new File(PATH_TO_JARS + jarFileName).toURI().toURL()});
//
//        Class<?> clazz = classLoader.loadClass("com.epam.modules.impl.Module");
//        Object moduleInstance = clazz.newInstance();
//
//        Method runMethod = clazz.getDeclaredMethod("run");
//        runMethod.invoke(moduleInstance);
    }

    private static void showMenu() {
        LOG.info("********************************************");
        LOG.info("Please select the option to load class from:");
        LOG.info("1 Module1.jar");
        LOG.info("2 Module2.jar");
        LOG.info("3 Module3.jar");
        LOG.info("4 Exit");
        LOG.info("********************************************");
    }
}
