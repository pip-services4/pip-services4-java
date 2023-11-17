package org.pipservices4.http.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.http.controllers.HeartbeatRestController;
import org.pipservices4.http.controllers.HttpEndpoint;
import org.pipservices4.http.controllers.StatusRestController;

/**
 * Creates HTTP components by their descriptors.
 * 
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java//org/pipservices4/components/build/Factory.html">Factory</a>
 * @see HttpEndpoint
 * @see HeartbeatRestController
 * @see StatusRestController
 */
public class DefaultHttpFactory extends Factory {
	private static final Descriptor HttpEndpointDescriptor = new Descriptor("pip-services", "endpoint", "http", "*",
			"1.0");
	private static final Descriptor StatusControllerDescriptor = new Descriptor("pip-services", "status-controller", "http",
			"*", "1.0");
	private static final Descriptor HeartbeatControllerDescriptor = new Descriptor("pip-services", "heartbeat-controller",
			"http", "*", "1.0");

	/**
	 * Create a new instance of the factory.
	 */
	public DefaultHttpFactory() {
		registerAsType(DefaultHttpFactory.HttpEndpointDescriptor, HttpEndpoint.class);
		registerAsType(DefaultHttpFactory.StatusControllerDescriptor, StatusRestController.class);
		registerAsType(DefaultHttpFactory.HeartbeatControllerDescriptor, HeartbeatRestController.class);
	}
}
