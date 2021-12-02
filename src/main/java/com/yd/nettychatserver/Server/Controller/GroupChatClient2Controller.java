package com.yd.nettychatserver.Server.Controller;

import com.yd.nettychatserver.Server.Service.NettyChatClient;

public class GroupChatClient2Controller {
    public static void main(String[] args) {
        try {
            NettyChatClient.startChat();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

