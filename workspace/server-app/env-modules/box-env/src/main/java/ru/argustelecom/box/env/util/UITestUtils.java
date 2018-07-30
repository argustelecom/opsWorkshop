package ru.argustelecom.box.env.util;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UITestUtils {

    private UITestUtils() {
    }

    public static <T> T getOrElse(List<T> list, int index, Supplier<T> defaultValueSupplier) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        } else {
            return defaultValueSupplier.get();
        }
    }

    public static <T> T getOrElse(List<T> list,  Supplier<T> defaultValueSupplier) {
        return getOrElse(list, 0, defaultValueSupplier);
    }

    public static String uniqueId(String original) {
        return original + UUID.randomUUID().toString().substring(0, 10);
    }

    public static String uniqueId() {
        return uniqueId("");
    }

}