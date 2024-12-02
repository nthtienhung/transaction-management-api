package com.iceteasoftware.notification.util;

/**
 * Author: Tran Duc Thinh
 * Date: 11/29/2024
 * Time: 10:52 AM
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String userId) {
        currentUser.set(userId);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void remove() {
        currentUser.remove();
    }
}
