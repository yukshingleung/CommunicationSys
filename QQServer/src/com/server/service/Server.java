package com.server.service;

import com.common.Message;
import com.common.MessageType;
import com.common.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private ServerSocket ss = null;
    // HashMap 没有处理线程安全，因此在多线程情况下是不安全的
    // 而 ConcurrentHashMap 处理的线程是安全的，即线程同步处理，在多线程情况下是安全的
    // private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static HashMap<String, User> validUsers = new HashMap<>();
    private static HashMap<String, ArrayList<Message>> offLineMsgDb = new HashMap<>();
    private static HashMap<String, ArrayList<Message>> offLineFileDb = new HashMap<>();

    public static HashMap<String, ArrayList<Message>> getOffLineFileDb() {
        return offLineFileDb;
    }

    public static HashMap<String, ArrayList<Message>> getOffLineMsgDb() {
        return offLineMsgDb;
    }

    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "666666"));
        validUsers.put("300", new User("300", "888888"));
        validUsers.put("eric", new User("eric", "234567"));
        validUsers.put("ivan", new User("ivan", "345678"));
        validUsers.put("jack", new User("jack", "456789"));
    }

    public Server() {
        System.out.println("服务端在 9999 端口监听");
        new Thread(new SendNews()).start();
        try {
            ss = new ServerSocket(9999);
            while (true) {
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Message msg = new Message();
                User u = (User) ois.readObject();

                if (checkUser(u.getUserId(), u.getPasswd())) {
                    msg.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(msg);
                    // 检查是否有离线消息
                    if (offLineMsgDb.containsKey(u.getUserId())) {
                        System.out.println("该用户 " + u.getUserId() + " 有离线消息");
                        ArrayList<Message> messages = offLineMsgDb.get(u.getUserId());
                        for (Message message : messages) {
                            new ObjectOutputStream(socket.getOutputStream()).writeObject(message);
                        }
                        offLineMsgDb.remove(u.getUserId());
                    }
                    // 检查是否有离线文件
                    if (offLineFileDb.containsKey(u.getUserId())) {
                        System.out.println("该用户 " + u.getUserId() + " 有离线文件");
                        // 把离线文件传给上线用户
                        ArrayList<Message> messages = offLineFileDb.get(u.getUserId());
                        for (Message message : messages) {
                            new ObjectOutputStream(socket.getOutputStream()).writeObject(message);
                        }
                        offLineFileDb.remove(u.getUserId());
                    }

                    ServerConnectClientThread scct = new ServerConnectClientThread(socket, u.getUserId());
                    scct.start();
                    ManageClientThreads.addClientThread(u.getUserId(), scct);

                } else {
                    System.out.println("用户 id=" + u.getUserId() + " pwd=" + u.getPasswd() + " 验证失败");
                    msg.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(msg);
                    // 登陆失败需要关闭socket
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkUser(String userId, String pwd) {
        User user = validUsers.get(userId);
        if(user == null) {
            System.out.println("用户 " + userId + " 不存在");
            return false;
        }
        if (!user.getPasswd().equals(pwd)) {
            System.out.println("用户 " + userId + " 密码错误");
            return false;
        }
        return true;
    }
}
