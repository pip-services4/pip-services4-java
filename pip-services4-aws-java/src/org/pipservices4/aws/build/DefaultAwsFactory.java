package org.pipservices4.aws.build;

import org.pipservices4.aws.count.CloudWatchCounters;
import org.pipservices4.aws.log.CloudWatchLogger;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;

/**
 * Creates HTTP components by their descriptors.
 * 
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java//org/pipservices4/components/build/Factory.html">Factory</a>
 * @see CloudWatchLogger
 * @see CloudWatchCounters
 */
public class DefaultAwsFactory extends Factory {
	private static final Descriptor CloudWatchLoggerDescriptor = new Descriptor("pip-services", "logger", "cloudwatch", "*",
			"1.0");
	private static final Descriptor CloudWatchCountersDescriptor = new Descriptor("pip-services", "counters", "cloudwatch",
			"*", "1.0");

	/**
	 * Create a new instance of the factory.
	 */
	public DefaultAwsFactory() {
		super();
		registerAsType(DefaultAwsFactory.CloudWatchLoggerDescriptor, CloudWatchLogger.class);
		registerAsType(DefaultAwsFactory.CloudWatchCountersDescriptor, CloudWatchCounters.class);
	}
}
