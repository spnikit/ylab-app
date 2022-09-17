package com.edu.ulab.app.storage;

import java.util.UUID;

public class StorageUtils {

    public static Long generateId() {
        // generates random positive number of type long
        return UUID.randomUUID().getMostSignificantBits() & Integer.MAX_VALUE;
    }
}
