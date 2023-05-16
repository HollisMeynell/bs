package l.f.mappool.util;

import l.f.mappool.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextUtil {
    static ThreadLocal<Map<String, Object>> threadLocalService = new ThreadLocal<>();


    public static <T> T getContext(String name, Class<T> tClass){
        if (threadLocalService.get() == null) return null;
        var data = threadLocalService.get().get(name);
        if (data == null) return null;
        return tClass.cast( data );
    }

    public static void setContext(String name, Object o){
        if (threadLocalService.get() == null) {
            threadLocalService.set(new ConcurrentHashMap<>());
        }
        threadLocalService.get().put(name, o);
    }

    public static void clearContext() {
        if (threadLocalService.get() != null) threadLocalService.remove();
    }

    public static User getContextUser(){
        return getContext("**USER", User.class);
    }
    public static void setContextUser(User u){
        setContext("**USER", u);
    }
    public static void remove(){
        threadLocalService.remove();
    }
}