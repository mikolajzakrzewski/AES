package edu.crypto;

import java.util.ArrayList;

public class Converter {

    public byte[][] keyToKey2d(byte[] key) {
        byte[][] key2d = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(key, 4 * i, key2d[i], 0, 4);
        }
        return key2d;
    }

    public byte[][] bytesToBlock(byte[] bytes1d) {
        byte[][] bytes2d = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bytes2d[j][i] = bytes1d[4 * i + j];
            }
        }
        return bytes2d;
    }

    public byte[] blockToBytes(byte[][] bytes2d) {
        byte[] bytes1d = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bytes1d[4 * i + j] = bytes2d[j][i];
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
                    block[j][i] = text[startingByte + (4 * i + j)];
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
            for (byte b : bytes1d) {
                stringText.append(String.format("%02X", b));
            }
        }
        return stringText.toString();
    }
}
