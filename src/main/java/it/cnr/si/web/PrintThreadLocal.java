package it.cnr.si.web;

public  class  PrintThreadLocal {
    public static final ThreadLocal printThreadLocal = new ThreadLocal();


    public static void set(String user) {
        printThreadLocal.set(user);
    }

    public static void unset() {
        printThreadLocal.remove();
    }

    public static String get() {
        return (String) printThreadLocal.get();
    }
}
