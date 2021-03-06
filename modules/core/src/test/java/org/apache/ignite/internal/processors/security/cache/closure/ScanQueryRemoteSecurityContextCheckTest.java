/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.security.cache.closure;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.internal.processors.security.AbstractCacheOperationRemoteSecurityContextCheckTest;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.lang.IgniteRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.ignite.Ignition.localIgnite;

/**
 * Testing operation security context when the filter of ScanQuery is executed on remote node.
 * <p>
 * The initiator node broadcasts a task to 'run' node that starts ScanQuery's filter. That filter is executed on
 * 'check' node and broadcasts a task to 'endpoint' nodes. On every step, it is performed verification that
 * operation security context is the initiator context.
 */
@RunWith(JUnit4.class)
public class ScanQueryRemoteSecurityContextCheckTest extends AbstractCacheOperationRemoteSecurityContextCheckTest {
    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGridAllowAll(SRV_INITIATOR);

        startClientAllowAll(CLNT_INITIATOR);

        startGridAllowAll(SRV_RUN);

        startClientAllowAll(CLNT_RUN);

        startGridAllowAll(SRV_CHECK);

        startGridAllowAll(SRV_ENDPOINT);

        startClientAllowAll(CLNT_ENDPOINT);

        G.allGrids().get(0).cluster().active(true);
    }

    /** {@inheritDoc} */
    @Override protected void setupVerifier(Verifier verifier) {
        verifier
            .expect(SRV_RUN, 1)
            .expect(CLNT_RUN, 1)
            .expect(SRV_CHECK, 2)
            .expect(SRV_ENDPOINT, 2)
            .expect(CLNT_ENDPOINT, 2);
    }

    /** */
    @Test
    public void test() throws Exception {
        grid(SRV_INITIATOR).cache(CACHE_NAME)
            .put(prmKey(grid(SRV_CHECK)), 1);

        awaitPartitionMapExchange();

        runAndCheck(grid(SRV_INITIATOR), operations());
        runAndCheck(grid(CLNT_INITIATOR), operations());
    }

    /** {@inheritDoc} */
    @Override protected Collection<UUID> nodesToCheck() {
        return Collections.singletonList(nodeId(SRV_CHECK));
    }

    /**
     * Stream of runnables to call query methods.
     */
    private Stream<IgniteRunnable> operations() {
        return Stream.of(
            () -> {
                VERIFIER.register();

                localIgnite().cache(CACHE_NAME).query(new ScanQuery<>(createRunner(SRV_CHECK))).getAll();
            },
            () -> {
                VERIFIER.register();

                localIgnite().cache(CACHE_NAME).query(
                    new ScanQuery<>((k, v) -> true),
                    createRunner(SRV_CHECK)
                ).getAll();
            }
        );
    }
}
