package com.ericsson.addroneapplication.communication.data;

import com.ericsson.addroneapplication.communication.CommMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by ebarnaw on 2016-10-13.
 */
public class SignalData {
    private int commandValue;
    private int parameterValue;

    public SignalData(Command command, Parameter parameter) {
        this.commandValue = command.getValue();
        this.parameterValue = parameter.getValue();
    }

    public SignalData(Command command, int parameterValue) {
        this.commandValue = command.getValue();
        this.parameterValue = parameterValue;
    }

    public SignalData(CommMessage message) {
        ByteBuffer buffer = message.getByteBuffer();
        this.commandValue = buffer.getInt();
        this.parameterValue = buffer.getInt();
    }

    public Command getCommand() {
        return Command.getCommand(commandValue);
    }

    public Parameter getParameter() {
        return Parameter.getParameter(parameterValue);
    }

    public int getParameterValue() {
        return parameterValue;
    }

    public boolean equals(SignalData command) {
        return this.getCommand() == command.getCommand() && this.getParameter() == command.getParameter();
    }

    public CommMessage getMessage() {
        byte[] payload = new byte[CommMessage.getPayloadSizeByType(CommMessage.MessageType.SIGNAL)];
        ByteBuffer buffer = ByteBuffer.allocate(payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(commandValue);
        buffer.putInt(parameterValue);
        System.arraycopy(buffer.array(), 0, payload, 0, payload.length);
        return new CommMessage(CommMessage.MessageType.SIGNAL, payload);
    }

    public static SignalData.Command parseCommand(final byte[] src)
    {
        ByteBuffer buffer = ByteBuffer.wrap(src, 0, 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return SignalData.Command.getCommand(buffer.getInt());
    }

    public static short parseAllPacketsNumber(final byte[] src)
    {
        ByteBuffer buffer = ByteBuffer.wrap(src, CommMessage.SIGNAL_COMMAND_SIZE, 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    public static short parseActualPacketNumber(final byte[] src)
    {
        ByteBuffer buffer = ByteBuffer.wrap(src, CommMessage.SIGNAL_COMMAND_SIZE + 2, 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    public static boolean hasPayload(final SignalData.Command command) {
        switch (command)
        {
            case CALIBRATION_SETTINGS_DATA:
            case CONTROL_SETTINGS_DATA:
            case ROUTE_CONTAINER_DATA:
                return true;

            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "SignalData( " + getCommand().toString() + ", " + getParameter().toString() + " )";
    }

    public enum Command
    {
        DUMMY(0),

        START_CMD(100007),

        APP_LOOP(100008),
        FLIGHT_LOOP(100009),
        CALIBRATE_ACCEL(100010),
        CALIBRATE_MAGNET(100011),
        CALIBRATE_ESC(100012),
        UPLOAD_SETTINGS(100013),
        DOWNLOAD_SETTINGS(100014),
        CALIBRATE_RADIO(100015),
        CHECK_RADIO(100016),
        SOFTWARE_UPGRADE(100017),
        SYSTEM_RESET(100018),
        UPLOAD_ROUTE(100019),
        DOWNLOAD_ROUTE(100020),
        SENSORS_LOGGER(100021),

        CALIBRATION_SETTINGS(100022),
        CONTROL_SETTINGS(100023),
        ROUTE_CONTAINER(100024),

        CALIBRATION_SETTINGS_DATA(100025),
        CONTROL_SETTINGS_DATA(100026),
        ROUTE_CONTAINER_DATA(100027),

        PING_VALUE(100028);

        private final int value;

        Command(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        static public Command getCommand(int value) {
            if (value == DUMMY.getValue()) return DUMMY;
            else if (value == START_CMD.getValue()) return START_CMD;
            else if (value == APP_LOOP.getValue()) return APP_LOOP;
            else if (value == FLIGHT_LOOP.getValue()) return FLIGHT_LOOP;
            else if (value == CALIBRATE_ACCEL.getValue()) return CALIBRATE_ACCEL;
            else if (value == CALIBRATE_MAGNET.getValue()) return CALIBRATE_MAGNET;
            else if (value == CALIBRATE_ESC.getValue()) return CALIBRATE_ESC;
            else if (value == UPLOAD_SETTINGS.getValue()) return UPLOAD_SETTINGS;
            else if (value == DOWNLOAD_SETTINGS.getValue()) return DOWNLOAD_SETTINGS;
            else if (value == CALIBRATE_RADIO.getValue()) return CALIBRATE_RADIO;
            else if (value == CHECK_RADIO.getValue()) return CHECK_RADIO;
            else if (value == SOFTWARE_UPGRADE.getValue()) return SOFTWARE_UPGRADE;
            else if (value == SYSTEM_RESET.getValue()) return SYSTEM_RESET;
            else if (value == UPLOAD_ROUTE.getValue()) return UPLOAD_ROUTE;
            else if (value == DOWNLOAD_ROUTE.getValue()) return DOWNLOAD_ROUTE;
            else if (value == SENSORS_LOGGER.getValue()) return SENSORS_LOGGER;
            else if (value == CALIBRATION_SETTINGS.getValue()) return CALIBRATION_SETTINGS;
            else if (value == CONTROL_SETTINGS.getValue()) return CONTROL_SETTINGS;
            else if (value == ROUTE_CONTAINER.getValue()) return ROUTE_CONTAINER;
            else if (value == CALIBRATION_SETTINGS_DATA.getValue()) return CALIBRATION_SETTINGS_DATA;
            else if (value == CONTROL_SETTINGS_DATA.getValue()) return CONTROL_SETTINGS_DATA;
            else if (value == ROUTE_CONTAINER_DATA.getValue()) return ROUTE_CONTAINER_DATA;
            else if (value == PING_VALUE.getValue()) return PING_VALUE;
            else return DUMMY; // TODO throw some exception
        }
    }

    public enum Parameter
    {
        DUMMY_PARAMETER(0),

        START(1000011),
        ACK(1000012),
        DATA_ACK(1000013),

        BREAK(1000014),
        BREAK_ACK(1000015),
        BREAK_FAIL(1000016),

        DONE(1000017),
        READY(1000018),
        FAIL(1000019),
        SKIP(1000020),

        NON_STATIC(1000021),
        NOT_ALLOWED(1000022),

        BAD_CRC(1000023),
        TIMEOUT(1000024);

        private final int value;

        Parameter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        static public Parameter getParameter(int value) {
            if (value == DUMMY_PARAMETER.getValue()) return DUMMY_PARAMETER;
            else if (value == START.getValue()) return START;
            else if (value == ACK.getValue()) return ACK;
            else if (value == DATA_ACK.getValue()) return DATA_ACK;
            else if (value == BREAK.getValue()) return BREAK;
            else if (value == BREAK_ACK.getValue()) return BREAK_ACK;
            else if (value == BREAK_FAIL.getValue()) return BREAK_FAIL;
            else if (value == DONE.getValue()) return DONE;
            else if (value == READY.getValue()) return READY;
            else if (value == FAIL.getValue()) return FAIL;
            else if (value == SKIP.getValue()) return SKIP;
            else if (value == NON_STATIC.getValue()) return NON_STATIC;
            else if (value == NOT_ALLOWED.getValue()) return NOT_ALLOWED;
            else if (value == BAD_CRC.getValue()) return BAD_CRC;
            else if (value == TIMEOUT.getValue()) return TIMEOUT;
            else return DUMMY_PARAMETER; // TODO throw some exception
        }
    }
}
