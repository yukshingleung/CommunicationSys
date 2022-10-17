package com.server.service;

import com.common.Message;
import com.common.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public void saveMsgToDb(Message msg) {
        HashMap<String, ArrayList<Message>> offLineDb = Server.getOffLineMsgDb();
        if (offLineDb.containsKey(msg.getReceiver())) {
            offLineDb.get(msg.getReceiver()).add(msg);
        } else {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(msg);
            offLineDb.put(msg.getReceiver(), messages);
        }
        System.out.println("离线消息: " + msg.getSender() + " - " + msg.getReceiver() + " - " +
                msg.getContent() + " - " + msg.getSendTime() + " - " + msg.getMesType());
    }

    public void saveFileToDb(Message msg) throws FileNotFoundException {
        HashMap<String, ArrayList<Message>> offLineDb = Server.getOffLineFileDb();
        if (offLineDb.containsKey(msg.getReceiver())) {
            offLineDb.get(msg.getReceiver()).add(msg);
        } else {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(msg);
            offLineDb.put(msg.getReceiver(), messages);
        }
        System.out.println("离线文件: " + msg.getReceiver() + " - " + msg.getDest() + " - " +
                msg.getFileName() + " - " + msg.getSendTime());
    }

    @Override
    public void run() { // 发送接受消息
        while (true) {
            System.out.println("服务端 - 与客户端 " + userId + " 保持通讯，读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();
                if (MessageType.MESSAGE_GET_ONLINE_FRIEND.equals(msg.getMesType())) {
                    System.out.println(msg.getSender() + " 获取在线用户列表");

                    String onlineUsers = ManageClientThreads.getOnlineUser();
                    Message msgRet = new Message();

                    msgRet.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    msgRet.setContent(onlineUsers);
                    msgRet.setReceiver(msg.getSender());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(msgRet);
                } else if (MessageType.MESSAGE_COMM_MES.equals(msg.getMesType())) {
                    // 接收者上线
                    if (ManageClientThreads.getHm().containsKey(msg.getReceiver())) {
                        ServerConnectClientThread clientThread = ManageClientThreads.getClientThread(msg.getReceiver());
                        ObjectOutputStream oos =
                                new ObjectOutputStream(clientThread.getSocket().getOutputStream());
                        oos.writeObject(msg);
                    } else {
                        // 如果 客户端线程 的 HashMap 中没有接收者的id，意味着该 id 未上线
                        saveMsgToDb(msg);
                    }
                }  else if (MessageType.MESSAGE_TOALL_MES.equals(msg.getMesType())) {
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Set<String> users = hm.keySet();
                    for (String user : users) {
                        if (!user.equals(msg.getSender())) {
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hm.get(user).getSocket().getOutputStream());
                            oos.writeObject(msg);
                        }
                    }
                } else if (MessageType.MESSAGE_CLIENT_EXIT.equals(msg.getMesType())) {
                    System.out.println(msg.getSender() + " exit");

                    ManageClientThreads.removeClientThread(msg.getSender());
                    socket.close();
                    break;
                } else if (MessageType.MESSAGE_FILE_MES.equals(msg.getMesType())) {
                    // 接收者上线
                    if (ManageClientThreads.getHm().containsKey(msg.getReceiver())) {
                        ObjectOutputStream oos =
                                new ObjectOutputStream(ManageClientThreads.getClientThread(msg.getReceiver()).getSocket().getOutputStream());
                        oos.writeObject(msg);
                    } else {
                        // 如果 客户端线程 的 HashMap 中没有接收者的id，意味着该 id 未上线
                        saveFileToDb(msg);
                    }
                } else {
                    System.out.println("其他类型");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
