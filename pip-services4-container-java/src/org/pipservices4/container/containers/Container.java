package org.pipservices4.container.containers;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.build.*;
import org.pipservices4.components.context.*;
import org.pipservices4.observability.log.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.*;
import org.pipservices4.container.build.*;
import org.pipservices4.container.config.*;
import org.pipservices4.container.refer.*;

/**
 * Inversion of control (IoC) container that creates components and manages their lifecycle.
 * <p>
 * The container is driven by configuration, that usually stored in JSON or YAML file.
 * The configuration contains a list of components identified by type or locator, followed
 * by component configuration.
 * <p>
 * On container start it performs the following actions:
 * <ul>
 * <li>Creates components using their types or calls registered factories to create components using their locators
 * <li>Configures components that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/config/IConfigurable.html">IConfigurable</a> interface and passes them their configuration parameters
 * <li>Sets references to components that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/refer/IReferenceable.html">IReferenceable</a> interface and passes them references of all components in the container
 * <li>Opens components that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/run/IOpenable.html">IOpenable</a> interface
 * </ul>
 * <p>
 * On container stop actions are performed in reversed order:
 * <ul>
 * <li>Closes components that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/run/IClosable.html">IClosable</a> interface
 * <li>Unsets references in components that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/refer/IUnreferenceable.html">IUnreferenceable</a> interface
 * <li>Destroys components in the container.
 * </ul>
 * <p>
 * The component configuration can be parameterized by dynamic values. That allows specialized containers
 * to inject parameters from command line or from environment variables.
 * <p>
 * The container automatically creates a <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices/components/context/ContextInfo.html">ContextInfo</a> component that carries detail information
 * about the container and makes it available for other components.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>name: 					the context (container or process) name
 * <li>description: 		   	human-readable description of the context
 * <li>properties: 			    entire section of additional descriptive properties
 * <li>...
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ======= config.yml ========
 * - descriptor: mygroup:mycomponent1:default:default:1.0
 *   param1: 123
 *   param2: ABC
 *
 * - type: mycomponent2,mypackage
 *   param1: 321
 *   param2: XYZ
 * ============================
 *
 * Container container = new Container();
 * container.addFactory(new MyComponentFactory());
 *
 * ConfigParams parameters = ConfigParams.fromValue(process.env);
 * container.readConfigFromFile("123", "./config/config.yml", parameters);
 *
 * container.open("123");
 * System.out.println("Container is opened");
 * ...
 * container.close("123");
 * System.out.println("Container is closed");
 * }
 * </pre>
 *
 * @see IConfigurable
 * @see IReferenceable
 * @see IOpenable
 */
public class Container implements IConfigurable, IReferenceable, IUnreferenceable, IOpenable {
    protected ILogger _logger = new NullLogger();
    protected DefaultContainerFactory _factories = new DefaultContainerFactory();
    protected ContextInfo _info;
    protected ContainerConfig _config;
    protected ContainerReferences _references;

    /**
     * Creates a new instance of the container.
     *
     * @param name        (optional) a container name (accessible via ContextInfo)
     * @param description (optional) a container description (accessible via
     *                    ContextInfo)
     */
    public Container(String name, String description) {
        _info = new ContextInfo(name, description);
    }

    /**
     * Creates a new instance of the container.
     *
     * @param config container configuration
     */
    public Container(ContainerConfig config) {
        _config = config;
    }

    public ContainerConfig getConfig() {
        return _config;
    }

    public void setConfig(ContainerConfig value) {
        _config = value;
    }

    public IReferences getReferences() {
        return _references;
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException when configuration is wrong.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        _config = ContainerConfig.fromConfig(config);

    }

    /**
     * Reads container configuration from JSON or YAML file and parameterizes it
     * with given values.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param path          a path to configuration file
     * @param parameters    values to parameters the configuration or null to skip
     *                      parameterization.
     * @throws ApplicationException when error occured.
     */
    public void readConfigFromFile(IContext context, String path, ConfigParams parameters)
            throws ApplicationException {
        _config = ContainerConfigReader.readFromFile(context, path, parameters);
        _logger.trace(context, this._config.toString());
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     * @throws ConfigException    when configuration is wrong
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        // Override in child class

    }

    /**
     * Unsets (clears) previously set references to dependent components.
     */
    @Override
    public void unsetReferences() {
        // Override in child class

    }

    private void initReferences(IReferences references) throws ApplicationException {
        // Override in base classes
        ContextInfo existingInfo = references.getOneOptional(ContextInfo.class, new Descriptor("*", "context-info", "*", "*", "1.0"));
        if (existingInfo == null)
            references.put(new Descriptor("pip-services", "context-info", "default", "default", "1.0"), this._info);
        else
            this._info = existingInfo;

        references.put(new Descriptor("pip-services", "factory", "container", "default", "1.0"), this._factories);
    }

    /**
     * Adds a factory to the container. The factory is used to create components
     * added to the container by their locators (descriptors).
     *
     * @param factory a component factory to be added.
     */
    public void addFactory(IFactory factory) {
        _factories.add(factory);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _references != null;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        if (this._references != null)
            throw new InvalidStateException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "ALREADY_OPENED",
                    "Container was already opened"
            );


        try {
            _logger.trace(context, "Starting container.");

            // Create references with configured components
            _references = new ContainerReferences();
            initReferences(_references);
            _references.putFromConfig(_config);
            setReferences(_references);

            // Get reference to container info
            Descriptor infoDescriptor = new Descriptor("*", "context-info", "*", "*", "*");
            _info = (ContextInfo) _references.getOneRequired(infoDescriptor);

            _references.open(context);

            // Get reference to logger
            _logger = new CompositeLogger(_references);

            _logger.info(context, "Container %s started.", _info.getName());
        } catch (Exception ex) {
            _references = null;
            _logger.error(context, ex, "Failed to start container");
            throw ex;
        }

    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void close(IContext context) throws ApplicationException {
        if (_references == null)
            throw new InvalidStateException(context != null ? ContextResolver.getTraceId(context) : null, "NO_STARTED", "Container was not started");

        try {
            _logger.trace(context, "Stopping %s container", _info.getName());

            // Close and deference components
            _references.close(context);
            _references = null;

            _logger.info(context, "Container %s stopped", _info.getName());
        } catch (Exception ex) {
            _logger.error(context, ex, "Failed to stop container");
            throw ex;
        }

    }
}
