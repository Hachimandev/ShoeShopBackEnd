package com.fit.shoeshopbackend.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        // Copy the default ObjectMapper config and configure it for default typing
        ObjectMapper cacheObjectMapper = objectMapper.copy();

        // Register custom Hibernate serializers to prevent serializing raw Hibernate proxies or persistent collections
        SimpleModule hibernateModule = new SimpleModule("HibernateModule");
        hibernateModule.addSerializer(PersistentCollection.class, new HibernateCollectionSerializer());
        hibernateModule.addSerializer(HibernateProxy.class, new HibernateProxySerializer());
        cacheObjectMapper.registerModule(hibernateModule);

        cacheObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Default TTL 10 minutes
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(cacheObjectMapper)
                        )
                );
    }

    private static class HibernateCollectionSerializer extends JsonSerializer<PersistentCollection> {
        @Override
        public void serialize(PersistentCollection value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            serializeValue(value, gen, serializers);
        }

        @Override
        public void serializeWithType(PersistentCollection value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serializeValue(value, gen, serializers);
        }

        private void serializeValue(PersistentCollection value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (!value.wasInitialized()) {
                serializers.defaultSerializeValue(null, gen);
                return;
            }

            // Convert Hibernate PersistentCollection implementation to standard JDK collections before serialization
            if (value instanceof List) {
                serializers.defaultSerializeValue(new ArrayList<>((List<?>) value), gen);
            } else if (value instanceof Set) {
                serializers.defaultSerializeValue(new LinkedHashSet<>((Set<?>) value), gen);
            } else if (value instanceof Map) {
                serializers.defaultSerializeValue(new LinkedHashMap<>((Map<?, ?>) value), gen);
            } else {
                serializers.defaultSerializeValue(new ArrayList<>((Collection<?>) value), gen);
            }
        }
    }

    private static class HibernateProxySerializer extends JsonSerializer<HibernateProxy> {
        @Override
        public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            serializeValue(value, gen, serializers);
        }

        @Override
        public void serializeWithType(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serializeValue(value, gen, serializers);
        }

        private void serializeValue(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value.getHibernateLazyInitializer().isUninitialized()) {
                serializers.defaultSerializeValue(null, gen);
                return;
            }
            Object implementation = value.getHibernateLazyInitializer().getImplementation();
            serializers.defaultSerializeValue(implementation, gen);
        }
    }
}
