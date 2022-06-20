package com.lhstack.opensearch.annotation;

import java.util.UUID;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:50
 * @Modify by
 */
public class UuidGenerator implements IdGenerator{
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
