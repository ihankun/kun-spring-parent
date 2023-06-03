package com.ihankun.core.cache;

import com.ihankun.core.cache.core.ListCache;
import com.ihankun.core.cache.core.MapCache;
import com.ihankun.core.cache.core.SetCache;
import com.ihankun.core.cache.core.StringCache;

/**
 * @author hankun
 */
public class CacheManager<K, V> {

    private StringCache stringCache;
    private MapCache<K, V> mapCache;
    private ListCache<V> listCache;
    private SetCache<V> setCache;

    public CacheManager(StringCache stringCache, MapCache mapCache, ListCache listCache, SetCache setCache) {
        this.stringCache = stringCache;
        this.mapCache = mapCache;
        this.listCache = listCache;
        this.setCache = setCache;
    }

    public StringCache string() {
        return stringCache;
    }

    public MapCache<K, V> map() {
        return mapCache;
    }

    public SetCache<V> set() {
        return setCache;
    }

    public ListCache<V> list() {
        return listCache;
    }
}
