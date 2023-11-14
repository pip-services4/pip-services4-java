package org.pipservices4.rpc.trace;

import org.pipservices4.components.context.IContext;
import org.pipservices4.observability.count.CounterTiming;
import org.pipservices4.observability.count.ICounters;
import org.pipservices4.observability.log.ILogger;
import org.pipservices4.observability.trace.TraceTiming;

public class InstrumentTiming {

    private final IContext _context;
    private final String _name;
    private final String _verb;
    private ILogger _logger;
    private ICounters _counters;
    private CounterTiming _counterTiming;
    private TraceTiming _traceTiming;

    public InstrumentTiming(IContext context, String name, String verb,
                            ILogger logger, ICounters counters,
                            CounterTiming counterTiming, TraceTiming traceTiming) {
        this._context = context;
        this._name = name;
        this._verb = verb != null ? verb : "call";
        this._logger = logger;
        this._counters = counters;
        this._counterTiming = counterTiming;
        this._traceTiming = traceTiming;
    }

    private void clear() {
        // Clear references to avoid double processing
        this._counters = null;
        this._logger = null;
        this._counterTiming = null;
        this._traceTiming = null;
    }

    public void endTiming(Exception err) {
        if (err == null) {
            this.endSuccess();
        } else {
            this.endFailure(err);
        }
    }

    public void endTiming() {
        this.endTiming(null);
    }

    public void endSuccess() {
        if (this._counterTiming != null) {
            this._counterTiming.endTiming();
        }
        if (this._traceTiming != null) {
            this._traceTiming.endTrace();
        }

        this.clear();
    }

    public void endFailure(Exception err) {
        if (this._counterTiming != null) {
            this._counterTiming.endTiming();
        }

        if (err != null) {
            if (this._logger != null) {
                this._logger.error(this._context, err, "Failed to call %s method", this._name);
            }
            if (this._counters != null) {
                this._counters.incrementOne(this._name + "." + this._verb + "_errors");
            }
            if (this._traceTiming != null) {
                this._traceTiming.endFailure(err);
            }
        } else {
            if (this._traceTiming != null) {
                this._traceTiming.endTrace();
            }
        }

        this.clear();
    }
}
