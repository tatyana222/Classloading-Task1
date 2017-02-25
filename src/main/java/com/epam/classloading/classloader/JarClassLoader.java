package com.epam.classloading.classloader;


import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader extends ClassLoader {

    private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

    private String jarFileName;
    private String pathToJars;

    public JarClassLoader(String jarFileName, String pathToJars) {
        this.jarFileName = jarFileName;
        this.pathToJars = pathToJars;
    }

    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> result = classCache.get(name);

        if (result == null)
            result = super.findSystemClass(name);

        System.out.println("== loadClass(" + name + ")");
        return result;
    }

    public void loadJar() throws IOException {
        JarFile jarFile = new JarFile(pathToJars + jarFileName);

        Enumeration<JarEntry> jarEntries = jarFile.entries();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (jarEntry.isDirectory())
                continue;

            if (jarEntry.getName().endsWith(".class")) {
                byte[] classData = loadClassData(jarFile, jarEntry);

                if (classData != null) {
                    String className = jarEntry.getName().replace('/', '.').substring(0,jarEntry.getName().length() - 6);
                    Class<?> clazz = defineClass(className, classData, 0, classData.length);
                    classCache.put(clazz.getName(), clazz);
                }
            }
        }
    }

    private byte[] loadClassData(JarFile jarFile, JarEntry jarEntry) throws IOException {
        long size = jarEntry.getSize();
        if (size <= 0)
            return null;
        else if (size > Integer.MAX_VALUE) {
            throw new IOException("Class size too long");
        }

        byte[] buffer = new byte[(int) size];
        InputStream is = jarFile.getInputStream(jarEntry);
        is.read(buffer);

        return buffer;
    }
}