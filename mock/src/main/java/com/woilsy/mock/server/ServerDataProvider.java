package com.woilsy.mock.server;

import org.jetbrains.annotations.Nullable;

public interface ServerDataProvider {

    @Nullable
    String getDataFromPath(String path, String method);

}
