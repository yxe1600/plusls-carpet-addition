package com.plusls.carpet.util;

import com.plusls.carpet.PluslsCarpetAdditionExtension;

public class StringUtil {
    public static String tr(String key, Object... objects) {
        return PluslsCarpetAdditionExtension.getSettingsManager()
                .tr(PluslsCarpetAdditionExtension.getSettingsManager().getCurrentLanguageCode(), key, objects);
    }

    public static String tr(String code, String key, Object... objects) {
        return PluslsCarpetAdditionExtension.getSettingsManager()
                .tr(code, key, objects);
    }
}
