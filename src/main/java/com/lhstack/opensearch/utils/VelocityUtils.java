package com.lhstack.opensearch.utils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/21 10:38
 * @Modify by
 */
public class VelocityUtils {

    private static final VelocityEngine VELOCITY_ENGINE = new VelocityEngine();

    static {
        VELOCITY_ENGINE.setProperty("resource.loader.file.class", "com.lhstack.opensearch.velocity.StringResourceLoader");
    }

    public static String process(String content, Map<String, Object> params) {
        VelocityContext velocityContext = new VelocityContext(params);
        StringWriter sw = new StringWriter();
        VELOCITY_ENGINE.getTemplate(content).merge(velocityContext, sw);
        return sw.getBuffer().toString();
    }
}
