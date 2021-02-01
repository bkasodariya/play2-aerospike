package model.helper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * Extract all <b>Class</b>es from a given package. Will scan jars too.
 * 
 */
public final class ClassReader {

    private ClassReader() {
        // do not instantiate
    }
    
    @SuppressWarnings("rawtypes")
    public static List<Class> getClasses(String packagePath) throws IOException, URISyntaxException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<String>();
        String packageName = packagePath.replace(".", "/");
        packageURL = classLoader.getResource(packageName);
        if ("jar".equals(packageURL.getProtocol())) {
            // build jar file name, then loop through zipped entries
            String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
            JarFile jf = new JarFile(jarFileName.substring(5, jarFileName.indexOf("!")));
            Enumeration<JarEntry> jarEntries = jf.entries();
            while (jarEntries.hasMoreElements()) {
                String entryName = jarEntries.nextElement().getName();
                if (entryName.startsWith(packageName) && entryName.endsWith(".class") && entryName.length() > packageName.length() + 5) {
                    names.add(entryName.replace("/", ".").substring(0, entryName.length() - 6));
                }
            }
            // loop through files in classpath
        } else {
            URI uri = new URI(packageURL.toString());
            File folder = new File(uri.getPath());
            // won't work with path which contains blank (%20)
            // File folder = new File(packageURL.getFile()); 
            File[] contenuti = folder.listFiles();
            for (File actual : contenuti) {
                String entryName = actual.getName();
                entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                names.add(entryName);
            }
        }
        List<Class> clazzez = null;
        if (names.size() > 0) {
            clazzez = new ArrayList<Class>();
            for (String name : names) {
                if ("jar".equals(packageURL.getProtocol())) {
                    clazzez.add(classLoader.loadClass(name));
                }
                else{
                    clazzez.add(classLoader.loadClass(packagePath+"."+name));
                }
            }
        }
        return clazzez;
    }
}
