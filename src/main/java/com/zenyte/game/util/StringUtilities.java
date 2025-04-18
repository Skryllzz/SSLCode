package com.zenyte.game.util;

import com.zenyte.network.io.RSBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtilities {
    private static final Logger log = LoggerFactory.getLogger(StringUtilities.class);
    private static final char[] CHAR_ARRAY = new char[]{8364, 0, 8218, 131, 8222, 8230, 8224, 8225, 136, 8240, 138, 8249, 140, 0, 711, 0, 0, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 152, 8482, 154, 8250, 156, 0, 731, 159};

    /*
       public static byte[] method555(CharSequence var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if(var4 > 0 && var4 < 128 || var4 >= 160 && var4 <= 255) {
            var2[var3] = (byte)var4;
         } else if(var4 == 8364) {
            var2[var3] = -128;
         } else if(var4 == 8218) {
            var2[var3] = -126;
         } else if(var4 == 402) {
            var2[var3] = -125;
         } else if(var4 == 8222) {
            var2[var3] = -124;
         } else if(var4 == 8230) {
            var2[var3] = -123;
         } else if(var4 == 8224) {
            var2[var3] = -122;
         } else if(var4 == 8225) {
            var2[var3] = -121;
         } else if(var4 == 710) {
            var2[var3] = -120;
         } else if(var4 == 8240) {
            var2[var3] = -119;
         } else if(var4 == 352) {
            var2[var3] = -118;
         } else if(var4 == 8249) {
            var2[var3] = -117;
         } else if(var4 == 338) {
            var2[var3] = -116;
         } else if(var4 == 381) {
            var2[var3] = -114;
         } else if(var4 == 8216) {
            var2[var3] = -111;
         } else if(var4 == 8217) {
            var2[var3] = -110;
         } else if(var4 == 8220) {
            var2[var3] = -109;
         } else if(var4 == 8221) {
            var2[var3] = -108;
         } else if(var4 == 8226) {
            var2[var3] = -107;
         } else if(var4 == 8211) {
            var2[var3] = -106;
         } else if(var4 == 8212) {
            var2[var3] = -105;
         } else if(var4 == 732) {
            var2[var3] = -104;
         } else if(var4 == 8482) {
            var2[var3] = -103;
         } else if(var4 == 353) {
            var2[var3] = -102;
         } else if(var4 == 8250) {
            var2[var3] = -101;
         } else if(var4 == 339) {
            var2[var3] = -100;
         } else if(var4 == 382) {
            var2[var3] = -98;
         } else if(var4 == 376) {
            var2[var3] = -97;
         } else {
            var2[var3] = 63;
         }
      }

      return var2;
   }
     */
    public static String readString(final RSBuffer stream, final int maximumLength) {
        try {
            final int length = Math.min(stream.getSmart(), maximumLength);
            final byte[] decompressedChars = new byte[length];
            stream.readerIndex(Huffman.decompress(stream.array(), stream.readerIndex(), decompressedChars, 0, length));
            return decodeString(decompressedChars, 0, length);
        } catch (final Exception exception_0) {
            exception_0.printStackTrace();
            return "Cabbage";
        }
    }

    private static String decodeString(final byte[] buffer, final int offset, final int length) {
        final char[] stringCharArray = new char[length];
        int index = 0;
        for (int i = 0; i < length; i++) {
            int characterCode = buffer[i + offset] & 255;
            if (characterCode != 0) {
                if ((characterCode >= 128) && (characterCode < 160)) {
                    char UTF16char = CHAR_ARRAY[characterCode - 128];
                    if (UTF16char == 0) {
                        UTF16char = 63;
                    }
                    characterCode = UTF16char;
                }
                stringCharArray[index++] = (char) characterCode;
            }
        }
        return new String(stringCharArray, 0, index);
    }

    public static String compile(final String[] strings, final int offset, final int length, final char separator) {
        try {
            final StringBuilder builder = new StringBuilder();
            for (int i = offset; i < length; i++) {
                builder.append(strings[i]);
                builder.append(separator);
            }
            if (builder.length() > 0) {
                builder.delete(builder.length() - 1, builder.length());
            }
            return builder.toString();
        } catch (final Exception e) {
            log.error("", e);
        }
        return "";
    }

    public static String escape(String str) {
        final int length = str.length();
        StringBuilder builder = new StringBuilder(length);
        boolean escaping = false;
        for (int var3 = 0; var3 < length; ++var3) {
            char var4 = str.charAt(var3);
            if (var4 == '<') {
                escaping = true;
            } else if (var4 == '>' && escaping) {
                escaping = false;
            } else if (!escaping) {
                builder.append(var4);
            }
        }
        return builder.toString();
    }
}
