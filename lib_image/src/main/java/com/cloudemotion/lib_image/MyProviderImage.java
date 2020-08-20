package com.cloudemotion.lib_image;

/**
 * Created by marwuinh@gmail.com on 3/16/19.
 */

public enum MyProviderImage {
    PROVIDER(BuildConfig.LIBRARY_PACKAGE_NAME);
    private String key = "";

    MyProviderImage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
