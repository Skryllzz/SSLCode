package com.zenyte.discord;

import java.util.Arrays;
import java.util.List;

public class DiscordUtils {

    private static final String[] admins = {
            "215640141400637440", // Noele
            "202342920106409984", // Kris
            "155961471283625984" // Tom
    };


    public static final List<String> ADMINS = Arrays.asList(admins);

    public static boolean isAdmin(String id) {
        return ADMINS.contains(id);
    }
}
