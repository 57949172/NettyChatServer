package com.yd.nettychatserver.Server.Controller;


import com.yd.nettychatserver.Server.Service.IGroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupChatController {
    @Autowired
    private IGroupChatService iGroupChatService;


    /**
     * 初始化工作并开启监听
     */
    @GetMapping("/listen")
    public void listen(){
        try {
            iGroupChatService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 关闭
//     */
//    @GetMapping("/close")
//    public void close(){
//        iGroupChatService.close();
//    }
}
