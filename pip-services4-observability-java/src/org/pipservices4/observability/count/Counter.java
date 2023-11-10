package org.pipservices4.observability.count;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

/**
 * Data object to store measurement for a performance counter.
 * This object is used by {@link CachedCounters} to store counters.
 */
public class Counter {
	/** The counter unique name */
	private String _name;
	/** The counter type that defines measurement algorithm */
	private int _type;
	/** The last recorded value */
	private Float _last;
	/** The total count */
	private Integer _count;
	/** The minimum value */
	private Float _min;
	/** The maximum value */
	private Float _max;
	/** The average value */
	private Float _average;
	/** The recorded timestamp */
	private ZonedDateTime _time;

	/**
	 * Creates a instance of the data obejct
	 */
	public Counter() {
	}

	/**
	 * Creates a instance of the data obejct
	 * 
	 * @param name a counter name.
	 * @param type a counter type.
	 */
	public Counter(String name, int type) {
		_name = name;
		_type = type;
	}

	@JsonProperty("name")
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	@JsonProperty("type")
	public int getType() {
		return _type;
	}

	public void setType(int type) {
		_type = type;
	}

	@JsonProperty("last")
	public Float getLast() {
		return _last;
	}

	public void setLast(Float last) {
		_last = last;
	}

	@JsonProperty("count")
	public Integer getCount() {
		return _count;
	}

	public void setCount(Integer count) {
		_count = count;
	}

	@JsonProperty("min")
	public Float getMin() {
		return _min;
	}

	public void setMin(Float min) {
		_min = min;
	}

	@JsonProperty("max")
	public Float getMax() {
		return _max;
	}

	public void setMax(Float max) {
		_max = max;
	}

	@JsonProperty("average")
	public Float getAverage() {
		return _average;
	}

	public void setAverage(Float average) {
		_average = average;
	}

	@JsonProperty("time")
	public ZonedDateTime getTime() {
		return _time;
	}

	public void setTime(ZonedDateTime time) {
		_time = time;
	}
}
