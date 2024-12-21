package dev.iseal.sealLib.Helpers;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PacketHelper {

    public static Object[] readString(byte[] input, int offset) {
        // Read string length
        int stringLength = ByteBuffer.wrap(Arrays.copyOfRange(input, offset, offset + 4)).getInt();
        offset += 4;

        // Read string data
        byte[] stringBytes = Arrays.copyOfRange(input, offset, offset + stringLength);
        String stringData = new String(stringBytes);
        offset += stringLength;

        return new Object[]{stringData, offset};
    }

    public static Object[] readInt(byte[] input, int offset) {
        // Read effect ID
        int readInt = ByteBuffer.wrap(Arrays.copyOfRange(input, offset, offset + 4)).getInt();
        offset += 4;

        return new Object[]{readInt, offset};
    }

    public static Object[] readFloat(byte[] input, int offset) {
        // Read float value
        float readFloat = ByteBuffer.wrap(Arrays.copyOfRange(input, offset, offset + 4)).getFloat();
        offset += 4;

        return new Object[]{readFloat, offset};
    }

    public static Object[] readBoolean(byte[] input, int offset) {
        // Read boolean value
        boolean readBoolean = ByteBuffer.wrap(Arrays.copyOfRange(input, offset, offset + 1)).get() == 1;
        offset += 1;

        return new Object[]{readBoolean, offset};
    }

    public static void writeString(DataOutputStream input, String stringData) {
        try {
            byte[] stringBytes = stringData.getBytes();
            input.writeInt(stringBytes.length);
            input.write(stringBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeInt(DataOutputStream output, int intData) {
        try {
            output.writeInt(intData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFloat(DataOutputStream output, float floatData) {
        try {
            output.writeFloat(floatData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBoolean(DataOutputStream output, boolean booleanData) {
        try {
            output.writeBoolean(booleanData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
