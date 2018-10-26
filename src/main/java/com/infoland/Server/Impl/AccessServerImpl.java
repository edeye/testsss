package com.infoland.Server.Impl;

import com.infoland.Server.AccessServer;
import com.infoland.Util.WatchingShortHandler;
import com.infoland.Util.WgUdpCommShort;
import com.infoland.dao.LockInfoMapper;
import com.infoland.model.LockInfo;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


@Service
public class AccessServerImpl implements AccessServer {


    /**
     * @date 2018/10/24
     * @author Aiden
     * @describe mina 选择2.0.4 ,2.0.19不行
     */

    /**
     * controllerSN 门禁SN码
     * controllerIP 门禁IP
     * watchServerIP 本机IP
     * watchServerPort 本机端口（门禁默认端口位60000）
     */
    private static long controllerSN = 123316795;
    private static String controllerIP = "10.0.1.123";
    private static String watchServerIP = "10.0.1.25";
    private static int watchServerPort = 61005;
    private String keyNum = "";//单个数字键密码字节
    private String keyNumAll = "";//数字键密码
    private int keylen = 0;//密码长度
    private String timer = "2018-10-25 14:04:37";
    private int timeb = 0;//两次网络连接的时间差
    private byte[] recvBuff;
    private WgUdpCommShort pkt = new WgUdpCommShort();

    @Autowired
    private LockInfoMapper lockInfoMapper;

    /**
     * 建立连接
     */
    @Override
    public void linkDevice() {
        pkt.CommOpen(controllerIP);
        log(String.format("UDP连接打开，控制器SN = %d ", controllerSN));
    }

    /**
     * 断开连接
     */
    @Override
    public void disDevice() {
        pkt.CommClose();
        log(String.format("UDP连接关闭"));
    }

    /**
     * 远程开门
     * 0x40远程开门功能号，详见文档
     * doorId 门号，默认为1
     */
    @Override
    public void openDoor() {
        int doorId = 1;
        pkt.Reset();
        pkt.functionID = (byte) 0x40;
        pkt.iDevSn = controllerSN;
        pkt.data[0] = (byte) (doorId & 0xff);
        recvBuff = pkt.run();
        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                log("远程开门成功...");
            } else {
                log("网络连接中，但远程开门失败...");
            }
        } else {
            log("网络未连接，远程开门失败...");
        }
    }

    /*
     *监听服务器
     */
    @Override
    public void setWatchingServerRuning() {

        Queue<byte[]> queue = new LinkedList<byte[]>();
        // 创建UDP数据包NIO
        NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
        // NIO设置底层IOHandler
        acceptor.setHandler(new WatchingShortHandler(queue));
        // 设置是否重用地址？ 也就是每个发过来的udp信息都是一个地址？
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        // 绑定端口地址
        try {
            acceptor.bind(new InetSocketAddress(watchServerIP, watchServerPort));
        } catch (IOException e) {
            log("绑定接收服务器失败....");
            e.printStackTrace();
        }
        log("进入接收服务器监控状态....[如果在win7下使用 一定要注意防火墙设置]");
        //生成6位随机码
        int password = (int) ((Math.random() * 9 + 1) * 100000);
        log("已生成6位随机密码" + password);

        int cardkey = 0;//是否需要刷卡
        while (true) {
            if (!queue.isEmpty()) {
                byte[] recvBuff;
                synchronized (queue) {
                    recvBuff = queue.poll();
                }
                if (recvBuff[1] == 0x20) {
                    long user = WgUdpCommShort.getLongByByte(recvBuff, 16, 4);//卡号，第16位开始，长度4
                    int key48 = WgUdpCommShort.getIntByByte(recvBuff[48]);//键盘输入信息，第48位
                    int key12 = WgUdpCommShort.getIntByByte(recvBuff[12]);//锁信息，2表示上一次操作为远程开锁，1表示为刷卡

                    //控制器当前时间
                    String controllerTime = String.format("20%02X-%02X-%02X %02X:%02X:%02X", WgUdpCommShort.getIntByByte(recvBuff[51]),
                            WgUdpCommShort.getIntByByte(recvBuff[52]), WgUdpCommShort.getIntByByte(recvBuff[53]),
                            WgUdpCommShort.getIntByByte(recvBuff[37]), WgUdpCommShort.getIntByByte(recvBuff[38]),
                            WgUdpCommShort.getIntByByte(recvBuff[39]));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        timeb = (int) (sdf.parse(controllerTime).getTime() - sdf.parse(timer).getTime()) / 1000;
                        log("距离上一次接受信息已过" + timeb + "秒");
                        timer = controllerTime;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //四秒内
                    if (keylen != 0 && timeb > 4) {
                        log("距离上次输出超时，keyNum，keyNumAll,kenlen全部清空" + keyNum + "," + keyNumAll + "," + keylen);
                        keyNum = "";
                        keyNumAll = "";
                        keylen = 0;
                        cardkey = 0;
                    } else {
                        log("没有数据或者未超时" + keyNum + "," + keyNumAll + "," + keylen);
                    }

                    if (key12 == 1) {
                        if (key48 == 0) {
                            cardkey = 1;
                            log("刷过卡了，可以输入");
                        }
                    } else {
                        log("还没刷卡，不能输入");
                    }


                    if (cardkey == 1) {
                        String keyAll = getKey(key48);
                        log(String.format("当前账号......" + user));
                        log(String.format("当前键盘输入..." + key48));
                        log(String.format("当前密码....." + keyAll));

                        if (keylen == 6 || key48 == 27) {


                            if (keyNumAll.equals(String.valueOf(password))) {
                                openDoor();
                                log("密码正确，开门");
                            } else {
                                log("密码错误，不开");
                            }
                            LockInfo lockInfo = new LockInfo();
                            lockInfo.setCarid(String.valueOf(user));
                            lockInfo.setTime(controllerTime);
                            lockInfoMapper.insert(lockInfo);
                            log("存进数据库");
                            keylen = 0;
                            keyNum = "";
                            keyNumAll = "";
                            cardkey = 0;
                        }

                    }
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
// 如果 不是while(true), 则打开下面的注释...
//	  acceptor.unbind();
//	  acceptor.dispose();
//	  return 0;
    }


    /**
     * 设置接收服务器(本机)与测试
     */
    @Override
    public void setWatchingServer() {
        byte[] recvBuff;

        pkt.Reset();
        pkt.functionID = (byte) 0x90;
        pkt.iDevSn = controllerSN;
        //接收服务器ip（本机ip）
        String[] ip = watchServerIP.split("\\.");
        if (ip.length == 4) {
            pkt.data[0] = (byte) Integer.parseInt(ip[0]);
            pkt.data[1] = (byte) Integer.parseInt(ip[1]);
            pkt.data[2] = (byte) Integer.parseInt(ip[2]);
            pkt.data[3] = (byte) Integer.parseInt(ip[3]);
        }

        // 接收服务器的端口: 61005
        pkt.data[4] = (byte) (watchServerPort & 0xff);
        pkt.data[5] = (byte) ((watchServerPort >> 8) & 0xff);

        // 每隔5秒发送一次: 05 (定时上传信息的周期为5秒 [正常运行时每隔5秒发送一次 有刷卡时立即发送])
        pkt.data[6] = 0;
        //设置主动上传按键信息，这个功能和设置ip,端口是绑定的
        pkt.data[7] = 1;

        recvBuff = pkt.run();

        if (recvBuff != null) {
            if (recvBuff[8] == 1) {
                log("设置接收服务器的IP和端口 	 成功...");

            }
        }

        //  读取接收服务器的IP和端口 [功能号: 0x92]
        pkt.Reset();
        pkt.functionID = (byte) 0x92;
        pkt.iDevSn = controllerSN;
        recvBuff = pkt.run();
        if (recvBuff != null) {
            log("读取接收服务器的IP和端口 	 成功...");
        }

    }


    // =======================以下功能可能不会再用到了========================//


    /**
     * 设置超级密码，可以直接开门
     */
    @Override
    public void setSuperPassword() {
        pkt.Reset();
        pkt.functionID = (byte) 0x8C;
        pkt.iDevSn = controllerSN;
        long password = 0x00027009;
        pkt.data[0] = 0x01;
        pkt.data[4] = 0x09;
        pkt.data[5] = 0x70;
        pkt.data[6] = 0x02;
        pkt.data[7] = 0x00;
        recvBuff = pkt.run();
        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                log("密码添加或修改	 成功...");
            } else {
                log("密码添加或修改	 失败...");
            }
        }
    }

    /**
     * 启用数字键盘，初始状态下未开启
     */
    @Override
    public void openNumKeyboard() {
        int tabe = 1;
        pkt.Reset();
        pkt.functionID = (byte) 0xA4;
        pkt.iDevSn = controllerSN;
        pkt.data[0] = (byte) (tabe & 0xff);
        recvBuff = pkt.run();

        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                log("启用键盘成功...");
            } else {
                log("启用键盘失败...");
            }
        }
    }

    /**
     * 启用刷卡加密码双重验证模式
     */
    @Override
    public void openNumAndCardMode() {
        pkt.Reset();
        pkt.functionID = (byte) 0xA4;
        pkt.iDevSn = controllerSN;
        pkt.data[0] = 0x01;
        recvBuff = pkt.run();
        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                log("启用刷卡加密码双重验证模式成功...");
            } else {
                log("启用刷卡加密码双重验证模式失败...");
            }
        }
    }

    //================================以下功能也可能不会再用到了======================//

    /**
     * 添加或修改一个权限，需要NFC卡号
     */
    @Override
    public void addUser() {

        pkt.Reset();
        pkt.functionID = (byte) 0x50;
        pkt.iDevSn = controllerSN;
        // 0D D7 37 00 要添加或修改的权限中的卡号 = 0x0037D70D = 3659533 (十进制)
        long cardNOOfPrivilege = 0x0058d3d0;

        System.arraycopy(WgUdpCommShort.longToByte(cardNOOfPrivilege), 0, pkt.data, 0, 4);
        // 20 10 01 01 起始日期: 2010年01月01日 (必须大于2001年)
        pkt.data[4] = 0x20;
        pkt.data[5] = 0x10;
        pkt.data[6] = 0x01;
        pkt.data[7] = 0x01;
        // 20 29 12 31 截止日期: 2029年12月31日
        pkt.data[8] = 0x20;
        pkt.data[9] = 0x29;
        pkt.data[10] = 0x12;
        pkt.data[11] = 0x31;
        // 01 允许通过 一号门 [对单门, 双门, 四门控制器有效]
        pkt.data[12] = 0x01;
        //密码（启用数字键才有效）
        pkt.data[16] = 12;
        pkt.data[17] = 12;
        pkt.data[18] = 12;
        recvBuff = pkt.run();

        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                log("卡号添加或修改	 成功...");
            } else {
                log("卡号添加或修改	 失败...");
            }
        }
    }

    /**
     * 清除一个权限
     */
    @Override
    public void deleteUser() {
        pkt.Reset();
        pkt.functionID = (byte) 0x52;
        pkt.iDevSn = controllerSN;
        // 要删除的权限卡号0D D7 37 00 = 0x0037D70D = 3659533 (十进制)
        long cardNOOfPrivilegeToDelete = 0x0037D70D;
        System.arraycopy(WgUdpCommShort.longToByte(cardNOOfPrivilegeToDelete), 0, pkt.data, 0, 4);
        recvBuff = pkt.run();
        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                // 这时 刷卡号为= 0x0037D70D = 3659533 (十进制)的卡, 1号门继电器不会动作.
                log("1.12 权限删除(单个删除)	 成功...");

            }
        }
    }

    /**
     * 清除所有权限
     */
    @Override
    public void deleteAllUser() {
        pkt.Reset();
        pkt.functionID = (byte) 0x54;
        pkt.iDevSn = controllerSN;
        // SpecialFlag 标识(防止误设置) 1 0x55 [固定]
        System.arraycopy(WgUdpCommShort.longToByte(WgUdpCommShort.SpecialFlag), 0, pkt.data, 0, 4);

        recvBuff = pkt.run();

        if (recvBuff != null) {
            if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
                // 这时清空成功
                log(" 权限全部清除成功...");

            }
        }
    }

    /**
     * 权限总数读取
     */
    @Override
    public void queryUserNum() {
        pkt.Reset();
        pkt.functionID = (byte) 0x58;
        pkt.iDevSn = controllerSN;
        recvBuff = pkt.run();
        if (recvBuff != null) {
            long privilegeCount = WgUdpCommShort.getLongByByte(recvBuff, 8, 4);
            log("权限总数:" + privilegeCount);

        }
    }


    public String getKey(int key) {

        if (key == 16) {
            keyNum = "0";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 17) {
            keyNum = "1";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 18) {
            keyNum = "2";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 19) {
            keyNum = "3";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 20) {
            keyNum = "4";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 21) {
            keyNum = "5";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 22) {
            keyNum = "6";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 23) {
            keyNum = "7";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 24) {
            keyNum = "8";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        if (key == 25) {
            keyNum = "9";
            keyNumAll = keyNumAll + keyNum;
            keylen++;
        }
        return keyNumAll;
    }

    /**
     * 日志信息
     */
    public static void log(String info) {
        System.out.println(info);
    }

}
