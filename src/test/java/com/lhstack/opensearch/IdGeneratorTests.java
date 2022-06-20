package com.lhstack.opensearch;

import com.lhstack.opensearch.annotation.IdGeneratorFactory;
import com.lhstack.opensearch.annotation.IdInsertStrategy;
import com.lhstack.opensearch.idgenerator.CustomIdGenerator;
import org.junit.jupiter.api.Test;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 15:42
 * @Modify by
 */
class IdGeneratorTests {

    @Test
    void testUUID() {
        String nextId = IdGeneratorFactory.nextId(IdInsertStrategy.UUID, null);
        System.out.println(nextId);

        System.out.println(IdGeneratorFactory.nextId(IdInsertStrategy.ASSIGN_UUID, null));
        System.out.println(IdGeneratorFactory.nextId(IdInsertStrategy.SNOW_FLAKE, null));
        System.out.println(IdGeneratorFactory.nextId(IdInsertStrategy.SNOW_FLAKE, null));
        System.out.println(IdGeneratorFactory.nextId(IdInsertStrategy.SNOW_FLAKE, null));
        System.out.println(IdGeneratorFactory.nextId(IdInsertStrategy.CUSTOM, CustomIdGenerator.class));
    }
}
