package com.transactionservice.util;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 2:02 PM
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
