package com.ericsson.addroneapplication.communication.data;

import com.ericsson.addroneapplication.communication.CommMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class CalibrationSettings implements SignalPayloadData {

    private Data data;
    private Flags flags;

    public CalibrationSettings() {
        this.data = new Data();
        flags = new Flags(32, data.flagsValue);
        data.accelCalib[0] = 1.0f;
        data.accelCalib[4] = 1.0f;
        data.accelCalib[8] = 1.0f;

        data.magnetSoft[0] = 1.0f;
        data.magnetSoft[4] = 1.0f;
        data.magnetSoft[8] = 1.0f;

        data.altimeterSetting = 1013.2f;
        data.temperatureSetting = 288.15f;

        data.crcValue = CommMessage.computeCrc32(data.serialize());
    }

    public CalibrationSettings(final byte[] dataArray) {
        ByteBuffer buffer = ByteBuffer.wrap(dataArray);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        this.data = new Data();
        for (int i = 0; i < data.gyroOffset.length; i++) {
            data.gyroOffset[i] = buffer.getFloat();
        }
        for (int i = 0; i < data.accelCalib.length; i++) {
            data.accelCalib[i] = buffer.getFloat();
        }
        for (int i = 0; i < data.magnetSoft.length; i++) {
            data.magnetSoft[i] = buffer.getFloat();
        }
        for (int i = 0; i < data.magnetHard.length; i++) {
            data.magnetHard[i] = buffer.getFloat();
        }

        data.altimeterSetting = buffer.getFloat();

        for (int i = 0; i < data.radioLevels.length; i++) {
            data.radioLevels[i] = buffer.getFloat();
        }
        for (int i = 0; i < data.pwmInputLevels.length; i++) {
            data.pwmInputLevels[i] = buffer.get();
        }

        data.boardType = buffer.getInt();
        data.flagsValue = buffer.getInt();
        data.crcValue = buffer.getInt();

        flags = new Flags(32, data.flagsValue);
    }

    @Override
    public SignalData.Command getDataType() {
        return SignalData.Command.CALIBRATION_SETTINGS_DATA;
    }

    public boolean isValid() {
        return data.crcValue == CommMessage.computeCrc32(data.serialize());
    }

    public ArrayList<CommMessage> getMessages() throws Exception {
        return CommMessage.buildMessagesList(getDataType(), data.serialize());
    }

    private class Data {
        float[] gyroOffset; // Vect3Df
        float[] accelCalib; // Mat3Df
        float[] magnetSoft; // Mat3Df
        float[] magnetHard; // Vect3Df
        float altimeterSetting;
        float temperatureSetting;
        float[] radioLevels; // Mat4Df
        byte[] pwmInputLevels; // 8 params
        int boardType;
        int flagsValue;
        int crcValue;

        Data() {
            gyroOffset = new float[3];
            accelCalib = new float[9];
            magnetSoft = new float[9];
            magnetHard = new float[3];
            radioLevels = new float[16];
            pwmInputLevels = new byte[8];
        }

        int getDataArraySize() {
            return 198;
        }

        byte[] serialize() {
            byte[] dataArray = new byte[getDataArraySize()];
            ByteBuffer buffer = ByteBuffer.allocate(dataArray.length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < data.gyroOffset.length; i++) {
                buffer.putFloat(data.gyroOffset[i]);
            }
            for (int i = 0; i < data.accelCalib.length; i++) {
                buffer.putFloat(data.accelCalib[i]);
            }
            for (int i = 0; i < data.magnetSoft.length; i++) {
                buffer.putFloat(data.magnetSoft[i]);
            }
            for (int i = 0; i < data.magnetHard.length; i++) {
                buffer.putFloat(data.magnetHard[i]);
            }

            buffer.putFloat(data.altimeterSetting);
            buffer.putFloat(data.temperatureSetting);

            for (int i = 0; i < data.radioLevels.length; i++) {
                buffer.putFloat(data.radioLevels[i]);
            }
            for (int i = 0; i < data.pwmInputLevels.length; i++) {
                buffer.put(data.pwmInputLevels[i]);
            }

            buffer.putInt(data.boardType);
            buffer.putInt(data.flagsValue);
            buffer.putInt(data.crcValue);

            System.arraycopy(buffer.array(), 0, dataArray, 0, dataArray.length);
            return dataArray;
        }
    }

    public byte[] serialize() {
        return data.serialize();
    }

    public float[] getGyroOffset() {
        return data.gyroOffset;
    }

    public float[] getAccelCalib() {
        return data.accelCalib;
    }

    public float[] getMagnetSoft() {
        return data.magnetSoft;
    }

    public float[] getMagnetHard() {
        return data.magnetHard;
    }

    public float getAltimeterSetting() {
        return data.altimeterSetting;
    }

    public float getTemperatureSetting() {
        return data.temperatureSetting;
    }

    public float[] getRadioLevels() {
        return data.radioLevels;
    }

    public byte[] getPwmInputLevels() {
        return data.pwmInputLevels;
    }

    public int getBoardType() {
        return data.boardType;
    }

    public Flags getFlags() {
        return flags;
    }
}