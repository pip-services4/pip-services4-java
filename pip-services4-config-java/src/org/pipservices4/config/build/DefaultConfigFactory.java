package org.pipservices4.config.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.config.auth.MemoryCredentialStore;
import org.pipservices4.components.build.Factory;
import org.pipservices4.config.config.JsonConfigReader;
import org.pipservices4.config.config.MemoryConfigReader;
import org.pipservices4.config.config.YamlConfigReader;
import org.pipservices4.config.connect.MemoryDiscovery;

/**
 * Creates [[ICredentialStore]] components by their descriptors.
 * 
 * @see [[IFactory]]
 * @see [[ICredentialStore]]
 * @see [[MemoryCredentialStore]]
 */
public class DefaultConfigFactory extends Factory {
    public static final Descriptor MemoryCredentialStoreDescriptor = new Descriptor("pip-services", "credential-store",
            "memory", "*", "1.0");        
    public static final Descriptor MemoryConfigReaderDescriptor = new Descriptor("pip-services", "config-reader",
            "memory", "*", "1.0");
    public static final Descriptor JsonConfigReaderDescriptor = new Descriptor("pip-services", "config-reader", "json",
            "*", "1.0");
    public static final Descriptor YamlConfigReaderDescriptor = new Descriptor("pip-services", "config-reader", "yaml",
            "*", "1.0");
    public static final Descriptor MemoryDiscoveryDescriptor = new Descriptor("pip-services", "discovery",
            "memory", "*", "1.0");            

    /**
     * Create a new instance of the factory.
     */
    public DefaultConfigFactory() {
        registerAsType(MemoryCredentialStoreDescriptor, MemoryCredentialStore.class);
        registerAsType(MemoryConfigReaderDescriptor, MemoryConfigReader.class);
        registerAsType(JsonConfigReaderDescriptor, JsonConfigReader.class);
        registerAsType(YamlConfigReaderDescriptor, YamlConfigReader.class);
        registerAsType(MemoryDiscoveryDescriptor, MemoryDiscovery.class);
    }
}
