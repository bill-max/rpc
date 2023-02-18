package com.bill.remoting.transport.netty.codec;

import com.bill.remoting.constants.RpcConstants;
import com.bill.remoting.dto.Message;
import com.bill.serialize.Serializer;
import com.bill.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message> {

    /**
     * 传输协议
     * 4B magic_number + 1B version + 4B full_length + 1B message_type + 1B codec + 1B compress + 4B request_id + NB body
     * netty 传输编码器
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER).writeByte(RpcConstants.VERSION);
        int idx = byteBuf.writerIndex();
        byteBuf.writerIndex(idx + 4)
                .writeByte(message.getMessageType())
                .writeByte(message.getCodec())
                .writeByte(message.getCompress())
                .writeInt(message.getRequestId());
        Object data = message.getData();
        Serializer serializer = new KryoSerializer();
        byte[] bytes = serializer.serialize(data);
        //todo 数据压缩部分
        byteBuf.writeBytes(bytes);
        int fullLength = bytes.length;
        int curIdx = byteBuf.writerIndex();
        byteBuf.writerIndex(idx).writeInt(fullLength).writeInt(curIdx);
    }
}
