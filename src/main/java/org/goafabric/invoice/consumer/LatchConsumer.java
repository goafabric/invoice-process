package org.goafabric.invoice.consumer;

import java.util.concurrent.CountDownLatch;

public interface LatchConsumer {
    CountDownLatch getLatch();
}
