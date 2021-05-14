package com.woilsy.mock.entity;

public class ExcludeInfo {

    private final boolean needRedirect;

    private final String redirectBaseUrl;

    public ExcludeInfo(boolean needRedirect, String redirectBaseUrl) {
        this.needRedirect = needRedirect;
        this.redirectBaseUrl = redirectBaseUrl;
    }

    public boolean isNeedRedirect() {
        return needRedirect;
    }

    public String getRedirectBaseUrl() {
        return redirectBaseUrl;
    }
}
