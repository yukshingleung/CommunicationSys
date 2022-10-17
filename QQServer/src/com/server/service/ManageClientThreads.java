package com.server.service;

import java.util.HashMap;
import java.util.Set;

public class ManageClientThreads{
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static void addClientThread(String userId, ServerConnectClientThread scct) {
        hm.put(userId, scct);
    }

    public static ServerConnectClientThread getClientThread (String userId) {
        return hm.get(userId);
    }

    public static void removeClientThread (String userId) {
        hm.remove(userId);
    }

    public static String getOnlineUser() {
        Set<String> users = hm.keySet();
        String onlineUserList = "";
        for (String user : users) {
            onlineUserList += user + " ";
        }
        return onlineUserList;
    }
}
