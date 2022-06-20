package com.lhstack.opensearch.annotation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:43
 * @Modify by
 */
public enum IdInsertStrategy {
    /**
     * 长uuid,包含-的
     */
    UUID,

    /**
     * 短UUID，去掉-的
     */
    ASSIGN_UUID,
    /**
     * 自定义id
     */
    CUSTOM,
    /**
     * 雪花id
     */
    SNOW_FLAKE
}
