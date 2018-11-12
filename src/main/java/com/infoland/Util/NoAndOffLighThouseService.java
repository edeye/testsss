package com.infoland.Util;

public class NoAndOffLighThouseService {
    byte Qu_index = 0x0;

    byte Qu_type = 0x0;

    // 红灯开
    public boolean redLighNo() {
        byte[] pData = new byte[]{1, 0, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 黄灯开
    public boolean yellowLighNo() {
        byte[] pData = new byte[]{0, 1, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 绿灯开
    public boolean greenLighNo() {
        byte[] pData = new byte[]{0, 0, 1, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);
    }

    // 红灯闪烁
    public boolean redLighNoAndOff() {
        byte[] pData = new byte[]{2, 0, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);
    }

    // 黄灯闪烁
    public boolean yellowLighNoAndOff() {
        byte[] pData = new byte[]{0, 2, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 绿灯闪烁
    public boolean greenLighNoAndOff() {
        byte[] pData = new byte[]{0, 0, 2, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 红灯关
    public boolean redLighOff() {
        byte[] pData = new byte[]{0, 0, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 黄灯关
    public boolean yellowLighOff() {
        byte[] pData = new byte[]{0, 0, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);

    }

    // 绿灯关
    public boolean greenLighOff() {
        byte[] pData = new byte[]{0, 0, 0, 0, 0, 0};
        return ConnectLighThouseService.CONNECT.Usb_Qu_write(Qu_index, Qu_type, pData);
    }

    // 检测连接
    public int ConnectNumber() {
        return ConnectLighThouseService.CONNECT.Usb_Qu_Getstate();
    }
}
