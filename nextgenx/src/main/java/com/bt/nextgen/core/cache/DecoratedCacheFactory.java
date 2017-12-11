package com.bt.nextgen.core.cache;

import java.util.Properties;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import net.sf.ehcache.constructs.blocking.BlockingCache;

/**
 * 
 * @author L055167
 * 
 * Class that extend the functionality of cacheing to block multiple threads accessing the same method while cache is reloaded.
 *  
 */
public class DecoratedCacheFactory extends CacheDecoratorFactory{

	@Override
	public Ehcache createDecoratedEhcache(Ehcache cache, Properties properties) {
		return new BlockingCache(cache);
	}

	@Override
	public Ehcache createDefaultDecoratedEhcache(Ehcache cache,
			Properties properties) {
		return new BlockingCache(cache);
	}

}
