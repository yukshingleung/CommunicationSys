package com.client.service;

import com.common.Message;
import com.common.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class FileClientService {
    public void sendFile(String fileName, String src, String dest, String senderId, String receiverId) {
        Message msg = new Message();
        msg.setMesType(MessageType.MESSAGE_FILE_MES);
        msg.setFileName(fileName);
        msg.setSrc(src);
        msg.setDest(dest);
        msg.setSender(senderId);
        msg.setReceiver(receiverId);
        String date = new Date().toString();
        msg.setSendTime(date);

        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src + fileName).length()];

        try {
            fileInputStream = new FileInputStream(src + fileName);
            fileInputStream.read(fileBytes);

            msg.setFileBytes(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n" + senderId + " 给 " + receiverId + " 发送文件 "
                + fileName + " 到对方目录 " + dest);

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
