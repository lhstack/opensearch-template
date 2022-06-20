package com.lhstack.opensearch.annotation;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:42
 * @Modify by
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    /**
     * id 插入策略
     *
     * @return
     */
    IdInsertStrategy idInsertStrategy() default IdInsertStrategy.ASSIGN_UUID;

    /**
     * 当IdInsertStrategy为Custom的时候，此参数生效
     * @return
     */
    Class<? extends IdGenerator> idGenerator() default IdGenerator.class;
}
