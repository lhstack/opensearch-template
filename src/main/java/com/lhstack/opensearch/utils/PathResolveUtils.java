package com.lhstack.opensearch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * @Description 路径解析Utils
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/21 10:01
 * @Modify by
 */
public class PathResolveUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathResolveUtils.class);

    private static final String CLASS_PATH_PREFIX = "classpath:";

    private static final String FILE_PATH_PREFIX = "file:";

    private static final String URL_PATH_PREFIX = "url:";

    public static final byte[] EMPTY_BYTES = new byte[0];


    public static byte[] readBytes(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            LOGGER.warn("PathResolveUtils resolve path failure,path is empty");
            return EMPTY_BYTES;
        }
        try {
            if (path.startsWith(CLASS_PATH_PREFIX)) {
                String newPath = path.substring(CLASS_PATH_PREFIX.length());
                newPath = resolvePath(newPath);
                try (InputStream in = getContextClassLoader().getResourceAsStream(newPath)) {
                    if (Objects.nonNull(in)) {
                        return in.readAllBytes();
                    }
                    return EMPTY_BYTES;
                }
            }
            if (path.startsWith(FILE_PATH_PREFIX)) {
                try (InputStream in = new FileInputStream(path.substring(FILE_PATH_PREFIX.length()))) {
                    return in.readAllBytes();
                }
            }
            if (path.startsWith(URL_PATH_PREFIX)) {
                try (InputStream in = new URL(path.substring(URL_PATH_PREFIX.length())).openStream()) {
                    return in.readAllBytes();
                }
            }
            return EMPTY_BYTES;
        } catch (Exception e) {
            LOGGER.error("readBytes failure,error {}", e.getMessage(), e);
            return EMPTY_BYTES;
        }
    }

    private static String resolvePath(String newPath) {
        if (newPath.indexOf('/') == 0 || newPath.indexOf('\\') == 0) {
            return newPath.substring(1);
        }
        return newPath;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
