package org.pipservices4.mysql.build;


import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.mysql.connect.MySqlConnection;

/**
 * Creates MySql components by their descriptors.
 *
 * @see Factory
 * @see org.pipservices4.mysql.connect.MySqlConnection
 */
public class DefaultMySqlFactory extends Factory {
    private static final Descriptor MySqlConnectionDescriptor = new Descriptor("pip-services", "connection", "mysql", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultMySqlFactory() {
        super();
        this.registerAsType(DefaultMySqlFactory.MySqlConnectionDescriptor, MySqlConnection.class);
    }
}
