package com.scaiz.analyze.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class CacheManager {

  private ConcurrentMap<String, Object> cache = new ConcurrentHashMap<>();

  private static final int DEFAULT_MAX_SIZE = 1000;

  private int maxSize;

  public CacheManager() {
    this.maxSize = DEFAULT_MAX_SIZE;
  }

  public CacheManager(int maxSize) {
    this.maxSize = maxSize;
  }


  public <T> void  put(String key, T value) {
    if (cache.size() >= this.maxSize) {
      cache.keySet().forEach(k -> {
        if (ThreadLocalRandom.current().nextInt(10) == 0) {
          cache.remove(key);
        }
      });
    }
    cache.putIfAbsent(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) cache.get(key);
  }

  public boolean exists(String key) {
    return cache.containsKey(key);
  }
}
