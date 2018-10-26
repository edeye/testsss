package com.infoland.Controller;


import com.infoland.Server.AccessServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AccessController {

    @Autowired
    private AccessServer accessServer;

    /**
     * 连接
     */
    @RequestMapping(value = "/link")
    private void linkDevice() {
        accessServer.linkDevice();
    }

    /**
     * 断连
     */
    @RequestMapping(value = "/dis")
    private void disDevice() {
        accessServer.disDevice();
    }

    /**
     * 开门
     */
    @RequestMapping(value = "/open")
    private void openDoor() {
        accessServer.openDoor();
    }

    /**
     * 进入监控状态
     */
    @RequestMapping(value = "/setServerRuning")
    private void setWatchingServerRuning() {
        accessServer.setWatchingServerRuning();
    }

    //========================================调试用===============================//

    /**
     * 设置接收服务器
     */
    @RequestMapping(value = "/setServer")
    private void setWatchingServer() {
        accessServer.setWatchingServer();
    }


    /**
     * 设置超级密码
     */
    @RequestMapping(value = "/setSuperPassword")
    private void setSuperPassword() {
        accessServer.setSuperPassword();
    }

    /**
     * 启用数字键盘
     */
    @RequestMapping(value = "/openNumKeyboard")
    private void openNumKeyboard() {
        accessServer.openNumKeyboard();
    }

    /**
     * 开启键盘和密码双重验证
     */
    @RequestMapping(value = "/openNumAndCardMode")
    private void openNumAndCardMode() {
        accessServer.openNumAndCardMode();
    }

    //=================================没用=======================//

    /**
     * 添加一张卡
     */
    @RequestMapping(value = "/addUser")
    private void addUser() {
        accessServer.addUser();
    }

    /**
     * 删除一张卡
     */
    @RequestMapping(value = "/deleteUser")
    private void deleteUser() {
        accessServer.deleteUser();
    }

    /**
     * 删除所有卡
     */
    @RequestMapping(value = "/deleteAllUser")
    private void deleteAllUser() {
        accessServer.deleteAllUser();
    }

    /**
     * 查询数量
     */
    @RequestMapping(value = "/queryUserNum")
    private void queryUserNum() {
        accessServer.queryUserNum();
    }


}
