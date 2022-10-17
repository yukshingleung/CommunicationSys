package com.client.service;

import com.common.Message;
import com.common.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("客户端线程 - 等待服务器端发送的消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject(); // 如果服务器没有发送msg对象，线程就会阻塞
                // 如果是返回在线用户消息类型
                if (MessageType.MESSAGE_RET_ONLINE_FRIEND.equals(msg.getMesType())) {
                    String[] onlineUsers = msg.getContent().split(" ");
                    System.out.println("\n=============== 当前在线用户列表 ===============");
                    for (String user : onlineUsers) {
                        System.out.println("用户: " + user);
                    }
                } else if (MessageType.MESSAGE_COMM_MES.equals(msg.getMesType())) {
                    // 如果是普通消息类型，即私聊
                    System.out.println("\n" + msg.getSendTime() + " - " + msg.getSender()
                            + " 对 " + msg.getReceiver() + " 说: " + msg.getContent());
                } else if (MessageType.MESSAGE_TOALL_MES.equals(msg.getMesType())) {
                    // 如果是群发消息类型
                    System.out.println("\n" + msg.getSendTime() + " - " + msg.getSender()
                            + " 对大家说: " + msg.getContent());
                } else if (MessageType.MESSAGE_FILE_MES.equals(msg.getMesType())) {
                    System.out.println("\n" + msg.getSender() + " 给 " + msg.getReceiver() + " 发送文件 "
                            + msg.getFileName() + " 到我的目录 " + msg.getDest() + msg.getFileName());

                    FileOutputStream fileOutputStream = new FileOutputStream(msg.getDest() + msg.getFileName());
                    fileOutputStream.write(msg.getFileBytes());

                    fileOutputStream.close();
                    System.out.println("保存文件成功");
                } else {
                    System.out.println("其他类型");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
