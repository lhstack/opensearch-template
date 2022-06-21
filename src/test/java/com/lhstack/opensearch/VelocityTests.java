package com.lhstack.opensearch;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/21 10:23
 * @Modify by
 */
class VelocityTests {

    @Test
    void test() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader.file.class", "com.lhstack.opensearch.velocity.StringResourceLoader");
        Template hello_world = engine.getTemplate("hello world");
        StringWriter sw = new StringWriter();
        hello_world.merge(new VelocityContext(),sw);
        String s = sw.getBuffer().toString();
        System.out.println(s);
    }
}
