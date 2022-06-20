package com.lhstack.opensearch.annotation;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:46
 * @Modify by
 */
public interface IdGenerator {

    /**
     * 生成下一个id
     * @return
     */
    String nextId();
}
