/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.config.spring;

import org.gridgain.grid.*;
import org.gridgain.grid.kernal.*;
import org.gridgain.grid.kernal.processors.config.*;
import org.jetbrains.annotations.*;

/**
 * Spring configuration processor.
 */
public class GridSpringConfigurationProcessor extends GridConfigurationProcessor {
    /**
     * @param ctx Kernal context.
     */
    public GridSpringConfigurationProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public GridConfiguration loadConfiguration(@Nullable String cfgPath) throws GridException {
        return super.loadConfiguration(cfgPath);
    }
}
