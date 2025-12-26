package com.labtools.nfc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Small APDU processor that:
 * - Recognizes SELECT by AID (CLA=0x00, INS=0xA4, P1=0x04, P2=0x00)
 * - Validates the AID equals the configured AID and responds with a payload + SW_OK
 * - For unknown SELECT AIDs returns 0x6A82 (File not found)
 * - For other APDUs echoes limited data + 0x9000 (OK)
 */
public class ApduProcessor {

    // Status words
    private static final byte[] SW_OK = {(byte) 0x90, (byte) 0x00};
    private static final byte[] SW_FILE_NOT_FOUND = {(byte) 0x6A, (byte) 0x82};

    // AID configured in apdu_config.xml: "4C41422D454D552E5644" (ASCII: "LAB-EMU.VD")
    private static final String AID_HEX = "4C41422D454D552E5644";
    private static final byte[] AID = hexStringToByteArray(AID_HEX);

    public byte[] process(byte[] apdu) {
        if (apdu == null || apdu.length < 4) {
            return SW_FILE_NOT_FOUND;
        }

        int cla = apdu[0] & 0xFF;
        int ins = apdu[1] & 0xFF;
        int p1 = apdu[2] & 0xFF;
        int p2 = apdu[3] & 0xFF;

        // SELECT by AID: 00 A4 04 00 <Lc> <AID>
        if (cla == 0x00 && ins == 0xA4 && p1 == 0x04 && p2 == 0x00) {
            if (apdu.length >= 5) {
                int lc = apdu[4] & 0xFF;
                if (apdu.length >= 5 + lc) {
                    byte[] aid = Arrays.copyOfRange(apdu, 5, 5 + lc);
                    if (Arrays.equals(aid, AID)) {
                        // Accepted: return a short token/payload + SW_OK
                        byte[] payload = "NFC-LAB-EMULATOR".getBytes(StandardCharsets.UTF_8);
                        return concat(payload, SW_OK);
                    } else {
                        return SW_FILE_NOT_FOUND;
                    }
                } else {
                    return SW_FILE_NOT_FOUND;
                }
            } else {
                return SW_FILE_NOT_FOUND;
            }
        }

        // Default behavior for any other command: echo back a short response + OK
        byte[] responseData;
        if (apdu.length > 0) {
            // For privacy/security, do not echo full APDUs in production
            responseData = Arrays.copyOf(apdu, Math.min(apdu.length, 32)); // limit length
        } else {
            responseData = "OK".getBytes(StandardCharsets.UTF_8);
        }
        return concat(responseData, SW_OK);
    }

    // Utility: concatenate two byte arrays
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Utility: hex string to byte array
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if ((len & 1) == 1) throw new IllegalArgumentException("Invalid hex string");
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}