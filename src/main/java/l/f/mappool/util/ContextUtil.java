package l.f.mappool.util;

import io.micrometer.common.util.StringUtils;
import l.f.mappool.entity.LoginUser;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

public class ContextUtil {
    static ThreadLocal<Map<String, Object>> threadLocalService = new ThreadLocal<>();


    public static <T> T getContext(String name, Class<T> tClass) {
        if (threadLocalService.get() == null) return null;
        var data = threadLocalService.get().get(name);
        if (data == null) return null;
        return tClass.cast(data);
    }

    public static void setContext(String name, Object o) {
        if (threadLocalService.get() == null) {
            threadLocalService.set(new ConcurrentHashMap<>());
        }
        threadLocalService.get().put(name, o);
    }

    public static void clearContext() {
        if (threadLocalService.get() != null) threadLocalService.remove();
    }

    public static void clearContext(String key) {
        if (threadLocalService.get() != null) threadLocalService.get().remove(key);
    }

    public static LoginUser getContextUser() {
        return getContext("**USER", LoginUser.class);
    }

    public static void setContextUser(LoginUser u) {
        setContext("**USER", u);
    }

    private static void remove() {
        threadLocalService.remove();
    }

    public static List<Class<?>> getAllClass(String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        var urls = Thread.currentThread()
                .getContextClassLoader()
                .getResources(packageName.replace(".", "/"));
        urls.asIterator().forEachRemaining(url -> url(url, classes, packageName));
        return classes;
    }

    private static void url(URL url, List<Class<?>> classes, String packageName) {
        if (url == null) return;

        switch (url.getProtocol()) {
            case "file" -> {
                String path = url.getPath().replaceAll("%20", " ");
                addClass(classes, path, packageName);
            }
            case "jar" -> {

                JarURLConnection j;
                try {
                    j = (JarURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (j == null) return;
                JarFile file;
                try {
                    file = j.getJarFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (file == null) return;
                file.entries().asIterator().forEachRemaining(jarEntry -> {
                    String jarEntryName = jarEntry.getName();
                    if (!jarEntryName.endsWith(".class") || jarEntryName.endsWith("-info.class")) return;
                    String className = jarEntryName
                            .substring(0, jarEntryName.lastIndexOf("."))
                            .replaceAll("/", ".");
                    addClass(classes, className);
                });

            }
        }
    }

    private static void addClass(List<Class<?>> classes, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        if (files == null) return;
        for (var file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotBlank(packageName)) {
                    className = packageName + "." + className;
                }
                addClass(classes, className);
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotBlank(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotBlank(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classes, subPackagePath, subPackageName);
            }
        }
    }

    private static void addClass(List<Class<?>> classes, String className) {
        Class<?> cls;
        var classLoader = Thread.currentThread().getContextClassLoader();
        try {
            System.out.println(className);
            cls = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException|NoClassDefFoundError e) {
            return;
        }
        classes.add(cls);
    }
}