package util;

import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


@Slf4j
public class ResourseHelper {


    public static StreamResource getStreamResource(String filename, String filePath) {
        return new StreamResource(
                filename,
                () -> {
                    try {
                        return new FileInputStream(filePath);
                    } catch (FileNotFoundException e) {
                        log.error("Failed to open file:", e);
                        throw new RuntimeException(e);
                    }
                });
    }
}
