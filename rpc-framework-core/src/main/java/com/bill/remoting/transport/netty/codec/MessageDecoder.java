package com.bill.remoting.transport.netty.codec;

import com.bill.remoting.constants.RpcConstants;
import com.bill.remoting.dto.Message;
import com.bill.remoting.dto.Request;
import com.bill.remoting.dto.Response;
import com.bill.serialize.Serializer;
import com.bill.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;

@Slf4j
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    public MessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf byteBuf && byteBuf.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
            try {
                return decodeFrame(byteBuf);
            } catch (Exception e) {
                log.error("Decode frame error !!");
                throw e;
            } finally {
                byteBuf.release();
            }
        }
        return decode;
    }

    /**
     * 传输协议
     * 4B magic_number + 1B version + 4B full_length + 1B message_type + 1B codec + 1B compress + 4B request_id + NB body
     * netty 传输编码器
     */
    private Object decodeFrame(ByteBuf byteBuf) {
        //必须按照写的顺序读取
        byte[] magicNumber = new byte[RpcConstants.MAGIC_NUMBER.length];
        byteBuf.readBytes(magicNumber);
        //todo check magic number
        int version = byteBuf.readInt();
        //todo check version
        int fullLength = byteBuf.readInt();
        byte messageType = byteBuf.readByte();
        byte codec = byteBuf.readByte();
        byte compress = byteBuf.readByte();
        int requestId = byteBuf.readInt();
        Message message = Message.builder().codec(codec).compress(compress).requestId(requestId).messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            message.setData(RpcConstants.PING);
            return message;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            message.setData(RpcConstants.PONG);
            return message;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            byteBuf.readBytes(bs);
            //todo decompress
            Serializer serializer = new KryoSerializer();
            if (messageType == RpcConstants.REQUEST_TYPE) {
                message.setData(serializer.deserialize(bs, Request.class));
            } else {
                message.setData(serializer.deserialize(bs, Response.class));
            }
        }
        return message;
    }
}
