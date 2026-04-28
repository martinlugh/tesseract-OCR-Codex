package com.example.ocrcloud.service;

import java.awt.image.BufferedImage;

public interface ImagePreprocessService {
    BufferedImage preprocess(BufferedImage source);
}
