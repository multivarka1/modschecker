package ru.multivarka;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CurseForgeFingerprintUtil {
    private static final int M = 0x5bd1e995;
    private static final int R = 24;
    private static final int SEED = 1;

    private CurseForgeFingerprintUtil() {
    }

    public static long fingerprint(Path path) throws Exception {
        byte[] data = Files.readAllBytes(path);
        return murmurHash2(data, 0, data.length, SEED);
    }

    private static long murmurHash2(byte[] data, int offset, int length, int seed) {
        int h = seed ^ length;
        int len4 = length >> 2;

        for (int i = 0; i < len4; i++) {
            int i4 = offset + (i << 2);
            int k = (data[i4] & 0xff)
                    | ((data[i4 + 1] & 0xff) << 8)
                    | ((data[i4 + 2] & 0xff) << 16)
                    | ((data[i4 + 3] & 0xff) << 24);
            k *= M;
            k ^= k >>> R;
            k *= M;

            h *= M;
            h ^= k;
        }

        int remaining = length & 3;
        int idx = offset + (len4 << 2);
        switch (remaining) {
            case 3:
                h ^= (data[idx + 2] & 0xff) << 16;
                break;
            case 2:
                h ^= (data[idx + 1] & 0xff) << 8;
                break;
            case 1:
                h ^= (data[idx] & 0xff);
                h *= M;
                break;
            default:
                break;
        }

        h ^= h >>> 13;
        h *= M;
        h ^= h >>> 15;
        return h & 0xffffffffL;
    }
}
