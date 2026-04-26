package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.ImagePreprocessService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@Service
public class ImagePreprocessServiceImpl implements ImagePreprocessService {

    private final OcrProperties properties;

    public ImagePreprocessServiceImpl(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public BufferedImage preprocess(BufferedImage source) {
        BufferedImage oriented = autoRotateForDocument(source);
        BufferedImage gray = toGray(oriented);
        BufferedImage denoise = denoise(gray);
        BufferedImage binary = toBinary(denoise);
        BufferedImage deskew = deskew(binary);
        return normalizeResolution(deskew);
    }

    private BufferedImage toGray(BufferedImage src) {
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }

    private BufferedImage denoise(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int w = src.getWidth();
        int h = src.getHeight();
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int sum = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int rgb = src.getRGB(x + dx, y + dy) & 0xff;
                        sum += rgb;
                    }
                }
                int avg = sum / 9;
                int val = (avg << 16) | (avg << 8) | avg;
                out.setRGB(x, y, (0xff << 24) | val);
            }
        }
        return out;
    }

    private BufferedImage toBinary(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        int threshold = otsuThreshold(src);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int gray = src.getRGB(x, y) & 0xff;
                int value = gray > threshold ? 0xffffffff : 0xff000000;
                out.setRGB(x, y, value);
            }
        }
        return out;
    }

    private int otsuThreshold(BufferedImage src) {
        int[] hist = new int[256];
        int w = src.getWidth();
        int h = src.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                hist[src.getRGB(x, y) & 0xff]++;
            }
        }
        int total = w * h;
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * hist[i];
        }
        float sumB = 0;
        int wB = 0;
        float varMax = 0;
        int threshold = 127;
        for (int i = 0; i < 256; i++) {
            wB += hist[i];
            if (wB == 0) {
                continue;
            }
            int wF = total - wB;
            if (wF == 0) {
                break;
            }
            sumB += (float) (i * hist[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
        return threshold;
    }

    private BufferedImage deskew(BufferedImage src) {
        double bestAngle = 0.0;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (double angle = -5.0; angle <= 5.0; angle += 0.5) {
            BufferedImage rotated = rotate(src, angle);
            double score = horizontalProjectionScore(rotated);
            if (score > bestScore) {
                bestScore = score;
                bestAngle = angle;
            }
        }
        return rotate(src, bestAngle);
    }

    private double horizontalProjectionScore(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        double[] rows = new double[h];
        for (int y = 0; y < h; y++) {
            int black = 0;
            for (int x = 0; x < w; x++) {
                int pixel = image.getRGB(x, y) & 0xff;
                if (pixel < 128) {
                    black++;
                }
            }
            rows[y] = black;
        }
        double mean = 0;
        for (double row : rows) {
            mean += row;
        }
        mean /= h;
        double var = 0;
        for (double row : rows) {
            double diff = row - mean;
            var += diff * diff;
        }
        return var / h;
    }

    private BufferedImage normalizeResolution(BufferedImage src) {
        double scale = Math.max(1.0, properties.getNormalizedDpi() / 150.0);
        int targetW = (int) Math.round(src.getWidth() * scale);
        int targetH = (int) Math.round(src.getHeight() * scale);
        BufferedImage output = new BufferedImage(targetW, targetH, src.getType());
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, targetW, targetH, null);
        g.dispose();
        return output;
    }

    private BufferedImage autoRotateForDocument(BufferedImage src) {
        if (src.getWidth() > src.getHeight() * 1.4) {
            return rotate(src, 90.0);
        }
        return src;
    }

    private BufferedImage rotate(BufferedImage src, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(h * cos + w * sin);
        BufferedImage rotated = new BufferedImage(newW, newH, src.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newW, newH);
        AffineTransform at = new AffineTransform();
        at.translate((double) (newW - w) / 2, (double) (newH - h) / 2);
        at.rotate(rads, (double) w / 2, (double) h / 2);
        g2d.drawRenderedImage(src, at);
        g2d.dispose();
        return rotated;
    }
}
