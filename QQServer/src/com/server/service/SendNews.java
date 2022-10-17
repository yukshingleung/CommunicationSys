package com.server.service;

import com.common.Message;
import com.common.MessageType;
import com.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class SendNews implements Runnable{
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            System.out.println("请输入需要推送的消息: ");
            String news = Utility.readString(100);
            if ("exit".equals(news)) {
                break;
            }
            Message msg = new Message();
            msg.setContent(news);
            msg.setSender("服务器");
            msg.setMesType(MessageType.MESSAGE_TOALL_MES);
            String date = new Date().toString();
            msg.setSendTime(date);
            System.out.println(msg.getSender() + " 对所有人说: " + news);

            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();

            Collection<ServerConnectClientThread> values = hm.values();
            for (ServerConnectClientThread value : values) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(value.getSocket().getOutputStream());
                    oos.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
