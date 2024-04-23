package edu.crypto;

import java.util.ArrayList;
import java.util.Collections;

public class AES {

    public byte[] rotWord(byte[] word) {
        byte[] rotatedWord = new byte[word.length];
        for (int i = 0; i < word.length; i++) {
            rotatedWord[i] = word[(i + 1) % word.length];
        }
        return rotatedWord;
    }

    public byte[] subWord(byte[] word) {
        byte[] substitutedWord = new byte[word.length];
        for (int i = 0; i < word.length; i++) {
            int substituteRow = (word[i] & 0xF0) >> 4;
            int substituteColumn = word[i] & 0x0F;
            substitutedWord[i] = (byte) Structures.sBox[substituteRow][substituteColumn];
        }
        return substitutedWord;
    }

    public byte[] rConXor(byte[] word , int i) {
        byte[] xorWord = new byte[word.length];
        for (int j = 0; j < word.length; j++) {
            xorWord[j] = (byte) (word[j] ^ (byte) Structures.rCon[i][j]);
        }
        return xorWord;
    }

    public byte[] xorWord(byte[] word , byte[] word2) {
        byte[] xorWord = new byte[word.length];
        for (int i = 0; i < word.length; i++) {
            xorWord[i] = (byte) (word[i] ^ word2[i]);
        }
        return xorWord;
    }

    public ArrayList<byte[][]> keyExpansion(byte[][] key) {
        ArrayList<byte[][]> keys = new ArrayList<>();
        keys.add(key);
        byte[][] previousKey = key;
        for (int i = 0; i < 10; i++) {
            byte[][] roundKey = new byte[key.length][key[0].length];
            byte[] word0 = previousKey[0];
            byte[] word1 = previousKey[1];
            byte[] word2 = previousKey[2];
            byte[] word3 = previousKey[3];
            byte[] rotatedWord3 = rotWord(word3);
            byte[] substitutedWord3 = subWord(rotatedWord3);
            byte[] rConXORWord = rConXor(substitutedWord3, i);
            roundKey[0] = xorWord(word0, rConXORWord);
            roundKey[1] = xorWord(word1, roundKey[0]);
            roundKey[2] = xorWord(word2, roundKey[1]);
            roundKey[3] = xorWord(word3, roundKey[2]);
            keys.add(roundKey);
            previousKey = roundKey;
        }
        return keys;
    }

    public byte[][] addRoundKey(byte[][] bytes, byte[][] key) {
        byte[][] combined = new byte[bytes.length][bytes[0].length];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes[0].length; j++) {
                combined[i][j] = (byte) (bytes[i][j] ^ key[j][i]);
            }
        }
        return combined;
    }

    public byte[][] substitute(byte[][] bytes, int[][] box) {
        byte[][] substitutedBytes = new byte[bytes.length][bytes[0].length];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes[0].length; j++) {
                int substituteRow = (bytes[i][j] & 0xF0) >> 4;
                int substituteColumn = bytes[i][j] & 0x0F;
                substitutedBytes[i][j] = (byte) box[substituteRow][substituteColumn];
            }
        }
        return substitutedBytes;
    }

    public byte[][] subBytes(byte[][] bytes) {
        return this.substitute(bytes, Structures.sBox);
    }

    public byte[][] inverseSubBytes(byte[][] substitutedBytes) {
        return this.substitute(substitutedBytes, Structures.inverseSBox);
    }

    public byte[][] shiftRows(byte[][] bytes) {
        byte[][] shiftedBytes = new byte[bytes.length][bytes[0].length];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes[0].length; j++) {
                shiftedBytes[i][j] = bytes[i][(j + i) % bytes.length];
            }
        }
        return shiftedBytes;
    }

    public byte[][] inverseShiftRows(byte[][] shiftedBytes) {
        byte[][] bytes = new byte[shiftedBytes.length][shiftedBytes[0].length];
        for (int i = 0; i < shiftedBytes.length; i++) {
            for (int j = 0; j < shiftedBytes[0].length; j++) {
                bytes[i][j] = shiftedBytes[i][(j - i + bytes.length) % bytes.length];
            }
        }
        return bytes;
    }

    public byte[][] mixColumns(byte[][] bytes) {
        byte[][] mixedBytes = new byte[bytes.length][bytes[0].length];
        for (int column = 0; column < bytes[0].length; column++) {
            for (int row = 0; row < bytes.length; row++) {
                byte fieldValue = 0;
                for (int i = 0; i < bytes[0].length; i++) {
                    switch (Structures.mixMatrix[row][i]) {
                        case 1:
                            fieldValue ^= bytes[i][column];
                            break;
                        case 2:
                            fieldValue ^= (byte) Structures.lookupTable2[(bytes[i][column] + 256) % 256];
                            break;
                        case 3:
                            fieldValue ^= (byte) Structures.lookupTable3[(bytes[i][column] + 256) % 256];
                            break;
                    }
                }
                mixedBytes[row][column] = fieldValue;
            }
        }
        return mixedBytes;
    }

    public byte[][] inverseMixColumns(byte[][] mixedBytes) {
        byte[][] bytes = new byte[mixedBytes.length][mixedBytes[0].length];
        for (int column = 0; column < mixedBytes[0].length; column++) {
            for (int row = 0; row < mixedBytes.length; row++) {
                byte fieldValue = 0;
                for (int i = 0; i < mixedBytes[0].length; i++) {
                    switch (Structures.inverseMixMatrix[row][i]) {
                        case 9:
                            fieldValue ^= (byte) Structures.lookupTable9[(mixedBytes[i][column] + 256) % 256];
                            break;
                        case 11:
                            fieldValue ^= (byte) Structures.lookupTable11[(mixedBytes[i][column] + 256) % 256];
                            break;
                        case 13:
                            fieldValue ^= (byte) Structures.lookupTable13[(mixedBytes[i][column] + 256) % 256];
                            break;
                        case 14:
                            fieldValue ^= (byte) Structures.lookupTable14[(mixedBytes[i][column] + 256) % 256];
                            break;
                    }
                }
                bytes[row][column] = fieldValue;
            }
        }
        return bytes;
    }

    public byte[][] cypherBlock(byte[][] bytes, ArrayList<byte[][]> roundKeys) {
        byte[][] cypheredBytes;
        cypheredBytes = this.addRoundKey(bytes, roundKeys.getFirst());
        for (int i = 1; i < roundKeys.size() - 1; i++) {
            cypheredBytes = this.subBytes(cypheredBytes);
            cypheredBytes = this.shiftRows(cypheredBytes);
            cypheredBytes = this.mixColumns(cypheredBytes);
            cypheredBytes = this.addRoundKey(cypheredBytes, roundKeys.get(i));
        }
        cypheredBytes = this.subBytes(cypheredBytes);
        cypheredBytes = this.shiftRows(cypheredBytes);
        cypheredBytes = this.addRoundKey(cypheredBytes, roundKeys.getLast());
        return cypheredBytes;
    }

    public ArrayList<byte[][]> cypherText(byte[] text, byte[] key) {
        int paddingBytesNumber = 16 - text.length % 16;
        ArrayList<byte[][]> byteText = new ArrayList<>();
        for (int startingByte = 0; startingByte < text.length; startingByte += 16) {
            byte[][] block = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (startingByte + (4 * i + j) > text.length - 1) {
                        block[j][i] = (byte) paddingBytesNumber;
                    } else {
                        block[j][i] = text[startingByte + (4 * i + j)];
                    }
                }
            }
            byteText.add(block);
        }
        if (paddingBytesNumber == 16) {
            byte[][] block = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    block[j][i] = (byte) 16;
                }
            }
            byteText.add(block);
        }
        Converter converter = new Converter();
        byte[][] key2d = converter.keyToKey2d(key);
        ArrayList<byte[][]> keys = this.keyExpansion(key2d);
        ArrayList<byte[][]> cypheredText = new ArrayList<>();
        for (byte[][] block : byteText) {
            cypheredText.add(cypherBlock(block, keys));
        }
        return cypheredText;
    }

    public byte[][] decipherBlock(byte[][] bytes, ArrayList<byte[][]> roundKeys) {
        byte[][] decipheredBytes;
        decipheredBytes = this.addRoundKey(bytes, roundKeys.getFirst());
        decipheredBytes = this.inverseShiftRows(decipheredBytes);
        decipheredBytes = this.inverseSubBytes(decipheredBytes);
        for (int i = 1; i < roundKeys.size() - 1; i++) {
            decipheredBytes = this.addRoundKey(decipheredBytes, roundKeys.get(i));
            decipheredBytes = this.inverseMixColumns(decipheredBytes);
            decipheredBytes = this.inverseShiftRows(decipheredBytes);
            decipheredBytes = this.inverseSubBytes(decipheredBytes);
        }
        decipheredBytes = this.addRoundKey(decipheredBytes, roundKeys.getLast());
        return decipheredBytes;
    }

    public byte[] decipherText(ArrayList<byte[][]> cypheredText, byte[] key) {
        byte[] decipheredText;
        Converter converter = new Converter();
        byte[][] key2d = converter.keyToKey2d(key);
        ArrayList<byte[][]> keys = this.keyExpansion(key2d);
        Collections.reverse(keys);
        ArrayList<byte[]> blocksWithoutPadding = new ArrayList<>();
        for (int blockNumber = 0; blockNumber < cypheredText.size(); blockNumber++) {
            byte[][] block = cypheredText.get(blockNumber);
            byte[][] decipheredBlock = decipherBlock(block, keys);
            byte[] decipheredBlock1d = converter.blockToBytes(decipheredBlock);
            int paddingBytesNumber = 0;
            if (blockNumber == cypheredText.size() - 1) {
                if (decipheredBlock[3][3] > 0 && decipheredBlock[3][3] < 17) {
                    paddingBytesNumber = decipheredBlock[3][3];
                }
            }
            byte[] blockWithoutPadding = new byte[decipheredBlock1d.length - paddingBytesNumber];
            System.arraycopy(decipheredBlock1d, 0, blockWithoutPadding, 0, blockWithoutPadding.length);
            blocksWithoutPadding.add(blockWithoutPadding);
        }
        int length = 0;
        for (byte[] block : blocksWithoutPadding) {
            for (byte ignored : block) {
                length++;
            }
        }
        decipheredText = new byte[length];
        for (int i = 0; i < blocksWithoutPadding.size(); i++) {
            System.arraycopy(blocksWithoutPadding.get(i), 0, decipheredText, i * 16, blocksWithoutPadding.get(i).length);
        }
        return decipheredText;
    }
}
