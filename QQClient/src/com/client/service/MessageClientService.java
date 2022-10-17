package com.client.service;

import com.common.Message;
import com.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class MessageClientService {
    public void sendMessageToOne(String content, String senderId, String receiverId) {
        Message msg = new Message();
        msg.setMesType(MessageType.MESSAGE_COMM_MES);
        msg.setSender(senderId);
        msg.setReceiver(receiverId);
        msg.setContent(content);
        String date = new Date().toString();
        msg.setSendTime(date);
        System.out.println(senderId + " 在 " + date + " 对 " + receiverId + " 说: " + content);

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAll(String content, String senderId) {
        Message msg = new Message();
        msg.setMesType(MessageType.MESSAGE_TOALL_MES);
        msg.setSender(senderId);
        msg.setContent(content);
        String date = new Date().toString();
        msg.setSendTime(date);
        System.out.println(senderId + " 在 " + date + " 对大家说: " + content);

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
