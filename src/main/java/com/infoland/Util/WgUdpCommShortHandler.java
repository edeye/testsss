package com.infoland.Util;

import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class WgUdpCommShortHandler implements IoHandler {

    public Queue<byte[]> queue;

    public WgUdpCommShortHandler() {
    }

    public WgUdpCommShortHandler(Queue<byte[]> queue) {
        super();
        this.queue = queue;
    }

    public void exceptionCaught(IoSession session, Throwable e) {
        e.printStackTrace();
        session.close(true);
    }

    public void messageReceived(IoSession session, Object message) {
        IoBuffer io = (IoBuffer) message;
        if (io.hasRemaining()) {
            byte[] validBytes = new byte[io.remaining()];
            io.get(validBytes, 0, io.remaining());
            if ((validBytes.length == WgUdpCommShort.WGPacketSize)
                    && (validBytes[0] == WgUdpCommShort.Type))  //型号固定
            {
                synchronized (queue) {
                    queue.offer(validBytes);
                }
            } else {
                //System.out.print("收到无效数据包: ????\r\n");
            }
            //System.out.println("");
        }
    }

    public void messageSent(IoSession session, Object message) {

    }

    public void sessionClosed(IoSession session) {

    }

    public void sessionCreated(IoSession session) {

    }

    public void sessionIdle(IoSession session, IdleStatus idle) {

    }

    public void sessionOpened(IoSession session) {

    }

}
