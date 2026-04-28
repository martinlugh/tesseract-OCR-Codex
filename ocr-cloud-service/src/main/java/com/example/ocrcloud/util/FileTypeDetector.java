package com.example.ocrcloud.util;

import com.example.ocrcloud.model.FileType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

@Component
public class FileTypeDetector {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "bmp", "tiff", "tif");

    public FileType detect(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return FileType.UNKNOWN;
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            return FileType.UNKNOWN;
        }
        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return FileType.IMAGE;
        }
        if ("pdf".equals(ext)) {
            return FileType.PDF;
        }
        return FileType.UNKNOWN;
    }
}
