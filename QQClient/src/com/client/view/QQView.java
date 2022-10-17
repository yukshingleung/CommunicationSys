package com.client.view;

import com.client.service.MessageClientService;
import com.client.service.FileClientService;
import com.client.service.UserClientService;
import com.utils.Utility;

public class QQView {
    private boolean loop = true;
    private String key = "";
    private UserClientService userClientService = new UserClientService();
    private MessageClientService messageClientService = new MessageClientService();
    private FileClientService fileClientService = new FileClientService();

    public static void main(String[] args) {
        new QQView().mainMenu();
    }

    private void mainMenu() {
        while (loop) {
            System.out.println("=============== 欢迎登陆网络通讯系统 ===============");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("请输入用户号: ");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密码: ");
                    String pwd = Utility.readString(6);
                    if (userClientService.checkUser(userId, pwd)) {
                        System.out.println("==== 登陆成功(用户 " + userId + ") ====");
                        while (true) {
                            System.out.println("=============== 网络通讯系统二级菜单(用户 " + userId + " )===============");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    System.out.println("\t\t 1 显示在线用户列表");
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("\t\t 2 群发消息");
                                    System.out.print("请输入想对大家说的话: ");
                                    String content2 = Utility.readString(100);
                                    messageClientService.sendMessageToAll(content2, userId);
                                    break;
                                case "3":
                                    System.out.println("\t\t 3 私聊消息");
                                    System.out.print("请输入在线的用户号: ");
                                    String receiver3 = Utility.readString(50);
                                    System.out.print("请输入消息: ");
                                    String content3 = Utility.readString(100);
                                    messageClientService.sendMessageToOne(content3, userId, receiver3);
                                    break;
                                case "4":
                                    System.out.println("\t\t 4 发送文件");
                                    System.out.print("请输入在线的用户号: ");
                                    String receiver4 = Utility.readString(50);
                                    System.out.print("请输入该文件名: ");
                                    String fileName = Utility.readString(50);
                                    System.out.print("请输入该文件所在路径: ");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入对方文件所在路径: ");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFile(fileName, src, dest, userId, receiver4);
                                    break;
                                case "9":
                                    System.out.println("\t\t 9 退出系统");
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("==== 登陆失败(用户"+ userId +") ====");
                    }
                    break;
                case "2":
                case "3":
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                    break;
            }
        }
    }
}
