package com.xn.book.util;

import java.security.SecureRandom;
import java.util.UUID;

public class ShortUuidGenerator {

    private static final int SHORT_UUID_LENGTH = 8;
    private static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final SecureRandom random = new SecureRandom();

    public static String generateShortUuid() {
        return generateShortUuid(SHORT_UUID_LENGTH);
    }

    public static String generateShortUuid(int len) {
        // 生成标准UUID
        UUID uuid = UUID.randomUUID();

        // 将UUID的字节转换为长整型，然后进行哈希或其他转换以生成较短的值
        // 这里简单地使用SecureRandom结合UUID的leastSignificantBits生成随机索引选取字符
        long leastSigBits = uuid.getLeastSignificantBits();
        StringBuilder shortUuidBuilder = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            shortUuidBuilder.append(CHARACTERS[(int) (leastSigBits & (CHARACTERS.length - 1))]);
            leastSigBits >>>= 5; // 右移5位，减少冲突概率
        }

        return shortUuidBuilder.toString();
    }


    public static void main(String[] args) {

        System.out.println(generateShortUuid(8));
    }
}
