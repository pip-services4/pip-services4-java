package org.pipservices4.commons.errors;

/**
 * Defines standard error categories to application exceptions
 * supported by PipServices toolkit.
 */
public class ErrorCategory {
    /**
     * Unknown or unexpected errors
     */
    public final static String Unknown = "Unknown";

    /**
     * Internal errors caused by programming mistakes
     */
    public final static String Internal = "Internal";

    /**
     * Errors related to mistakes in user-defined configuration
     */
    public final static String Misconfiguration = "Misconfiguration";

    /**
     * Errors related to operations called in wrong component state.
     * For instance, business calls when component is not ready
     */
    public final static String InvalidState = "InvalidState";

    /**
     * Errors caused by remote calls timeouted and not returning results.
     * It allows to clearly separate communication related problems
     * from other application errors.
     */
    public final static String NoResponse = "NoResponse";

    /**
     * Errors returned by remote services or network
     * during call attempts
     */
    public final static String FailedInvocation = "FailedInvocation";

    /**
     * Errors in read/write file operations
     */
    public final static String FileError = "FileError";

    /**
     * Errors due to improper user requests, like
     * missing or wrong parameters
     */
    public final static String BadRequest = "BadRequest";

    /**
     * Access errors caused by missing user identity (authentication error)
     * or incorrect security permissions (authorization error).
     */
    public final static String Unauthorized = "Unauthorized";

    /**
     * Error caused by attempt to access missing object
     */
    public final static String NotFound = "NotFound";

    /**
     * Errors raised by conflicts between object versions that were
     * posted by the user and those that are stored on the server.
     */
    public final static String Conflict = "Conflict";

    /**
     * Errors caused by calls to unsupported
     * or not yet implemented functionality
     */
    public final static String Unsupported = "Unsupported";
}
