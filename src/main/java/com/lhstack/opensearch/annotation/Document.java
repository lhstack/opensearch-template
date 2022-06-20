package com.lhstack.opensearch.annotation;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:32
 * @Modify by
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {
    /**
     * 默认类名,index 文档名称
     *
     * @return
     */
    String value() default "";

    /**
     * mapping json文件，默认classpath:/mappings/Class.name
     *
     * @return
     */
    String mappingPath() default  "";

}
