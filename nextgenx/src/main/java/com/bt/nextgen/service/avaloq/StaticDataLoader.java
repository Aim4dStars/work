package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.cache.CacheType;

/**
 * Static data loading info and task.
 */
public class StaticDataLoader implements Comparable<StaticDataLoader> {
	/**
	 * Task to load data.
	 */
	public static interface LoaderTask {
		void load();
	}
	
	/** Name of static data. */
	private final String name;
	
	/** Description for static data. */
	private final String description;
	
	/** Type of static data cache. */
	private final CacheType cacheType;
	
	/** Task to load/reload static data. */
	private final LoaderTask loaderTask;
	

	public StaticDataLoader(String name, String description, CacheType cacheType, LoaderTask loaderTask) {
		this.name = name;
		this.description = description;
		this.cacheType = cacheType;
		this.loaderTask = loaderTask;
	}
	

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public CacheType getCacheType() {
		return cacheType;
	}

	public LoaderTask getLoaderTask() {
		return loaderTask;
	}

	@Override
	public int compareTo(StaticDataLoader other) {
		return name.compareTo(other.name);
	}
}
