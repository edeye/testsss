package com.infoland.Util;


import com.sun.jna.Library;
import com.sun.jna.Native;

public interface ConnectLighThouseService extends Library {
    //公共硬盘地址:\\\\10.0.1.188\\Public\\dev\\9-项目资料\\无人值守项目\\Ux64_dllb
    ConnectLighThouseService CONNECT = Native.load("C:\\Ux64_dllb", ConnectLighThouseService.class);
    //ConnectLighThouseService CONNECT = Native.load("src/main/resources/light/Ux64_dllb", ConnectLighThouseService.class);

    //灯和蜂鸣器开关的方法
    boolean Usb_Qu_write(byte Qu_index, byte Qu_type, byte[] pData);

    //检测连接设备
    int Usb_Qu_Getstate();
}
