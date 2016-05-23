package com.kpro.rfidmatcher;

import android.nfc.Tag;

/**
 * @author Konrad Brzykcy.
 */
public class BetterTag {

    private Tag tag;

    public BetterTag(Tag tag) {
        this.tag = tag;
    }

    public String getId() {
        return bytesToHexString(tag.getId());
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }
}
