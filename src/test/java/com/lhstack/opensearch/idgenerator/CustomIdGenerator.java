package com.lhstack.opensearch.idgenerator;

import com.lhstack.opensearch.annotation.IdGenerator;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 15:44
 * @Modify by
 */
public class CustomIdGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
