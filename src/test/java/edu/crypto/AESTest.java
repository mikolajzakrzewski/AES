package edu.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class AESTest {

    @Test
    void rotWordTest() {
        AES aes = new AES();
        byte[] word = {0x02, 0x03, 0x04, 0x05};
        byte[] rotatedWord = {0x03, 0x04, 0x05, 0x02};
        Assertions.assertArrayEquals(rotatedWord, aes.rotWord(word));
    }

    @Test
    void subWordTest() {
        AES aes = new AES();
        byte[] word = {0x3d, 0x2c, 0x04, 0x5a};
        byte[] substitutedWord = {0x27, 0x71, (byte) 0xF2, (byte) 0xBE};
        Assertions.assertArrayEquals(substitutedWord, aes.subWord(word));
    }

    @Test
    void substituteTest() {
        AES aes = new AES();
        byte[][] block = {
                {0x3d, 0x2c, 0x04, 0x5a},
                {0x3d, 0x2c, 0x04, 0x5a},
                {0x3d, 0x2c, 0x04, 0x5a},
                {0x3d, 0x2c, 0x04, 0x5a}
        };
        byte[][] substitutedBlock = {
                {0x27, 0x71, (byte) 0xF2, (byte) 0xBE},
                {0x27, 0x71, (byte) 0xF2, (byte) 0xBE},
                {0x27, 0x71, (byte) 0xF2, (byte) 0xBE},
                {0x27, 0x71, (byte) 0xF2, (byte) 0xBE}
        };
        Assertions.assertTrue(Arrays.deepEquals(substitutedBlock, aes.substitute(block, Structures.sBox)));
    }

    @Test
    void shiftRowsTest() {
        AES aes = new AES();
        byte[][] block = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        byte[][] shiftedBlock = {
                {1, 2, 3, 4},
                {6, 7, 8, 5},
                {11, 12, 9, 10},
                {16, 13, 14, 15}
        };
        Assertions.assertTrue(Arrays.deepEquals(shiftedBlock, aes.shiftRows(block)));
    }

    @Test
    void inverseShiftRowsTest() {
        AES aes = new AES();
        byte[][] shiftedBlock = {
                {1, 2, 3, 4},
                {6, 7, 8, 5},
                {11, 12, 9, 10},
                {16, 13, 14, 15}
        };
        byte[][] block = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Assertions.assertTrue(Arrays.deepEquals(block, aes.inverseShiftRows(shiftedBlock)));
    }

    @Test
    void mixColumns() {
        AES aes = new AES();
        byte[][] block = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        byte c0 = 0x02 ^ 0x0f ^ 9 ^ 13;
        byte c1 = 1 ^ 0x0a ^ 0x1b ^ 13;
        byte c2 = 1 ^ 5 ^ 0x12 ^ 0x17;
        byte c3 = 0x03 ^ 5 ^ 9 ^ 0x1a;
        byte c4 = 0x04 ^ 0x0a ^ 10 ^ 14;
        byte c5 = 2 ^ 0x0c ^ 0x1e ^ 14;
        byte c6 = 2 ^ 6 ^ 0x14 ^ 0x12;
        byte c7 = 0x06 ^ 6 ^ 10 ^ 0x1c;
        byte c8 = 0x06 ^ 0x09 ^ 11 ^ 15;
        byte c9 = 3 ^ 0x0e ^ 0x1d ^ 15;
        byte c10 = 3 ^ 7 ^ 0x16 ^ 0x11;
        byte c11 = 0x05 ^ 7 ^ 11 ^ 0x1e;
        byte c12 = 0x08 ^ 0x18 ^ 12 ^ 16;
        byte c13 = 4 ^ 0x10 ^ 0x14 ^ 16;
        byte c14 = 4 ^ 8 ^ 0x18 ^ 0x30;
        byte c15 = 0x0c ^ 8 ^ 12 ^ 0x20;
        byte[][] inversedBlock = {
                {c0, c4, c8, c12},
                {c1, c5, c9, c13},
                {c2, c6, c10, c14},
                {c3, c7, c11, c15}
        };
        Assertions.assertTrue(Arrays.deepEquals(aes.mixColumns(block), inversedBlock));
    }

    @Test
    void inverseMixColumns() {
        AES aes = new AES();
        byte c0 = 0x02 ^ 0x0f ^ 9 ^ 13;
        byte c1 = 1 ^ 0x0a ^ 0x1b ^ 13;
        byte c2 = 1 ^ 5 ^ 0x12 ^ 0x17;
        byte c3 = 0x03 ^ 5 ^ 9 ^ 0x1a;
        byte c4 = 0x04 ^ 0x0a ^ 10 ^ 14;
        byte c5 = 2 ^ 0x0c ^ 0x1e ^ 14;
        byte c6 = 2 ^ 6 ^ 0x14 ^ 0x12;
        byte c7 = 0x06 ^ 6 ^ 10 ^ 0x1c;
        byte c8 = 0x06 ^ 0x09 ^ 11 ^ 15;
        byte c9 = 3 ^ 0x0e ^ 0x1d ^ 15;
        byte c10 = 3 ^ 7 ^ 0x16 ^ 0x11;
        byte c11 = 0x05 ^ 7 ^ 11 ^ 0x1e;
        byte c12 = 0x08 ^ 0x18 ^ 12 ^ 16;
        byte c13 = 4 ^ 0x10 ^ 0x14 ^ 16;
        byte c14 = 4 ^ 8 ^ 0x18 ^ 0x30;
        byte c15 = 0x0c ^ 8 ^ 12 ^ 0x20;
        byte[][] inversedBlock = {
                {c0, c4, c8, c12},
                {c1, c5, c9, c13},
                {c2, c6, c10, c14},
                {c3, c7, c11, c15}
        };
        byte[][] block = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Assertions.assertTrue(Arrays.deepEquals(block, aes.inverseMixColumns(inversedBlock)));
    }

    @Test
    void cypherDecypherBlockTest() {
        AES aes = new AES();
        Converter converter = new Converter();
        String stringKey = "test key yes yes";
        String stringIncorrectKey = "this isn't a key";
        byte[] key = stringKey.getBytes();
        ArrayList<byte[][]> keys = aes.keyExpansion(converter.bytesToBlock(key));
        ArrayList<byte[][]> reversedKeys = aes.keyExpansion(converter.bytesToBlock(key));
        Collections.reverse(reversedKeys);
        byte[] incorrectKey = stringIncorrectKey.getBytes();
        ArrayList<byte[][]> incorrectKeys = aes.keyExpansion(converter.bytesToBlock(incorrectKey));
        ArrayList<byte[][]> reversedIncorrectKeys = aes.keyExpansion(converter.bytesToBlock(incorrectKey));
        Collections.reverse(reversedIncorrectKeys);
        byte[][] block = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Assertions.assertTrue(Arrays.deepEquals(block, aes.decipherBlock(aes.cypherBlock(block, keys), reversedKeys)));
        Assertions.assertFalse(Arrays.deepEquals(block, aes.decipherBlock(aes.cypherBlock(block, incorrectKeys), incorrectKeys)));
    }

    @Test
    void cypherDecipherTextTest() {
        AES aes = new AES();
        String stringKey = "test key yes yes";
        String stringIncorrectKey = "this isn't a key";
        String stringMessage = "test message yesa";
        byte[] key = stringKey.getBytes();
        byte[] incorrectKey = stringIncorrectKey.getBytes();
        byte[] message = stringMessage.getBytes();
        byte[] cypheredDecipheredMessage = aes.decipherText(aes.cypherText(message, key), key);
        byte[] incorrectCypheredDecipheredMessage = aes.decipherText(aes.cypherText(message, key), incorrectKey);
        String cypheredDecipheredMessageString = new String(cypheredDecipheredMessage, StandardCharsets.UTF_8);
        String incorrectCypheredDecipheredMessageString = new String(incorrectCypheredDecipheredMessage, StandardCharsets.UTF_8);
        Assertions.assertEquals(stringMessage, cypheredDecipheredMessageString);
        Assertions.assertNotEquals(stringMessage, incorrectCypheredDecipheredMessageString);
        Assertions.assertArrayEquals(message, cypheredDecipheredMessage);
        Assertions.assertFalse(Arrays.equals(message, incorrectCypheredDecipheredMessage));
    }

    @Test
    void cypherDecipherFileTest() throws IOException {
        AES aes = new AES();
        String stringKey = "test key yes yes";
        String stringIncorrectKey = "this isn't a key";
        byte[] key = stringKey.getBytes();
        byte[] incorrectKey = stringIncorrectKey.getBytes();
        byte[] message = Files.readAllBytes(Paths.get("imagew.webp"));
        ArrayList<byte[][]> cypheredMessage = aes.cypherText(message, key);
        byte[] decipheredMessage = aes.decipherText(cypheredMessage, key);
        byte[] incorrectDecipheredMessage = aes.decipherText(cypheredMessage, incorrectKey);
        Assertions.assertArrayEquals(message, decipheredMessage);
        Assertions.assertFalse(Arrays.equals(message, incorrectDecipheredMessage));
        File outputFile = new File("outputFile.webp");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(decipheredMessage);
        }
        File incorrectOutputFile = new File("incorrectOutputFile.webp");
        try (FileOutputStream outputStream = new FileOutputStream(incorrectOutputFile)) {
            outputStream.write(incorrectDecipheredMessage);
        }
    }
}