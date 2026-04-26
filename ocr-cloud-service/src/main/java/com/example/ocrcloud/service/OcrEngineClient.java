package com.example.ocrcloud.service;

import java.io.File;

public interface OcrEngineClient {
    String engineName();

    String recognize(File file, String language);

    boolean health();
}
