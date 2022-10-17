package com.client.service;

import com.common.Message;
import com.common.MessageType;
import com.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {
    private User u = new User();
    private Socket socket;

    public boolean checkUser(String userId, String pwd){
        boolean b = false;
        u.setUserId(userId);
        u.setPasswd(pwd);

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message msg = (Message) ois.readObject();

            if (MessageType.MESSAGE_LOGIN_SUCCEED.equals(msg.getMesType())) {
                b = true;
                // 登陆成功，创建和服务器端保持通讯的线程
                ClientConnectServerThread ccst = new ClientConnectServerThread(socket);
                ccst.start();
                ManageClientConnectServerThread.addClientConnectServerThread(userId, ccst);

            } else {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    public void onlineFriendList () {
        Message msg = new Message();
        msg.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        msg.setSender(u.getUserId());

        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread
                            (u.getUserId()).getSocket().getOutputStream());

            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        Message msg = new Message();
        msg.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        msg.setSender(u.getUserId());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
            System.out.println(u.getUserId() + " exit");

            System.exit(0);// 结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
