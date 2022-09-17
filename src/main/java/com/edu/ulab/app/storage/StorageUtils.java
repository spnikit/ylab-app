package com.edu.ulab.app.storage;

import java.util.UUID;

public class StorageUtils {

    public static Long generateId() {
        return UUID.randomUUID().getMostSignificantBits();
    }
}
