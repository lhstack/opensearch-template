package com.lhstack.opensearch.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:47
 * @Modify by
 */
public class IdGeneratorFactory {

    private static final Map<Class<? extends IdGenerator>, IdGenerator> ID_GENERATOR_CACHE_MAP = new HashMap<>();

    static {
        ID_GENERATOR_CACHE_MAP.put(UuidGenerator.class, new UuidGenerator());
        ID_GENERATOR_CACHE_MAP.put(AssignUuidGenerator.class, new AssignUuidGenerator());
        ID_GENERATOR_CACHE_MAP.put(SnowFlakeIdGenerator.class, new SnowFlakeIdGenerator());

    }

    public static String nextId(IdInsertStrategy strategy, Class<? extends IdGenerator> idGeneratorClass) {
        return getOrNewInstanceIdGenerator(strategy, idGeneratorClass).nextId();
    }

    public static IdGenerator getOrNewInstanceIdGenerator(IdInsertStrategy strategy, Class<? extends IdGenerator> idGeneratorClass) {
        switch (strategy) {
            case UUID:
                return ID_GENERATOR_CACHE_MAP.get(UuidGenerator.class);
            case SNOW_FLAKE:
                return ID_GENERATOR_CACHE_MAP.get(SnowFlakeIdGenerator.class);
            case ASSIGN_UUID:
                return ID_GENERATOR_CACHE_MAP.get(AssignUuidGenerator.class);
            default: {
                return ID_GENERATOR_CACHE_MAP.computeIfAbsent(idGeneratorClass, key -> {
                    try {
                        return key.getConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }
        }
    }
}
