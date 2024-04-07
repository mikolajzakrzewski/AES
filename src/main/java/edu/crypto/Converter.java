package edu.crypto;

import java.util.ArrayList;

public class Converter {

    public byte[][] bytesToBlock(byte[] bytes1d) {
        byte[][] bytes2d = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bytes2d[i][j] = bytes1d[4 * i + j];
            }
        }
        return bytes2d;
    }

    public byte[] blockToBytes(byte[][] bytes2d) {
        byte[] bytes1d = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bytes1d[4 * i + j] = bytes2d[i][j];
            }
        }
        return bytes1d;
    }

    public byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
        return bytes;
    }

    public ArrayList<byte[][]> stringTextToByteText(String stringText) {
        byte[] text = hexStringToByteArray(stringText);
        ArrayList<byte[][]> byteText = new ArrayList<>();
        for (int startingByte = 0; startingByte < text.length; startingByte += 16) {
            byte[][] block = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    block[i][j] = text[startingByte + (4 * i + j)];
                }
            }
            byteText.add(block);
        }
        return byteText;
    }

    public String byteTextToStringText(ArrayList<byte[][]> byteText) {
        StringBuilder stringText = new StringBuilder();
        for (byte[][] bytes2d : byteText) {
            byte[] bytes1d = this.blockToBytes(bytes2d);
            for (int i = 0; i < bytes1d.length; i++) {
                stringText.append(String.format("%02X", bytes1d[i]));
            }
        }
        return stringText.toString();
    }
}
