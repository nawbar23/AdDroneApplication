package com.ericsson.addroneapplication.comunication;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;

import java.util.Map;

/**
 * Created by nbar on 2016-08-23.
 * Handler for incoming byte stream.
 * Generates events when any of defined messages is received and is valid.
 * Map argument in constructor defines preamble byte vs message type.
 */

public class StreamProcessor implements TcpSocket.TcpSocketListener {
    private static final String DEBUG_TAG = "AdDrone:" + StreamProcessor.class.getSimpleName();

    private final Map<Byte, CommunicationMessage.MessageId> supportMap;

    private StreamProcessorListener listener;

    private boolean activePreamble;
    private Byte preambleByte;

    private byte[] messageBuffer;
    private int messageBufferCounter;

    public StreamProcessor(Map<Byte, CommunicationMessage.MessageId> supportMap, StreamProcessorListener listener) {
        this.supportMap = supportMap;
        this.listener = listener;

        this.activePreamble = false;

        this.messageBuffer = new byte[100];
        this.messageBufferCounter = 0;
    }

    @Override
    public void onPacketReceived(byte[] packet) {
        process(packet);
    }

    public void process(byte[] data) {
        final int dataSize = data.length;

        if (dataSize < 4 && !activePreamble) {
            // if any preamble is not active and data is shorter than preamble any data can not be received
            return;
        }

        for (int i = 0; i < dataSize; i++) {
            byte[] tmp = new byte[4];
            System.arraycopy(data, i, tmp, 0, 4);
            if (dataSize - i > 4 && isPreamble(tmp)) {
                activatePreamble(tmp[0]);
            }
            if (activePreamble) {
                messageBuffer[messageBufferCounter] = data[i];
                messageBufferCounter++;
                if (isMessageReceived()) {
                    listener.onMessageReceived(CommunicationMessage.messageFactory(
                            supportMap.get(preambleByte),
                            messageBuffer));
                    activePreamble = false;
                }
            }
        }
    }

    private boolean isPreamble(byte[] data) {
        return data[0] == data[1]
                &&  data[1] == data[2]
                && data[2] == data[3]
                && isPreambleByteSupported(data[0]);
    }

    private boolean isMessageReceived() {
        return messageBufferCounter == CommunicationMessage.getPayloadSizeById(supportMap.get(preambleByte));
    }

    private boolean isPreambleByteSupported(byte b) {
        return supportMap.containsKey(b);
    }

    private void activatePreamble(byte b) {
        activePreamble = true;
        preambleByte = b;
        messageBufferCounter = 0;
    }

    public interface StreamProcessorListener {
        void onMessageReceived(CommunicationMessage message);
    }
}
