package com.woilsy.mock.server;

import java.io.IOException;

public interface IMockServer {

    void start() throws IOException;

    void stop();

}
