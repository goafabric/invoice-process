package org.goafabric.invoice.consumer.config;

import java.util.concurrent.CountDownLatch;

public interface LatchConsumer {
    CountDownLatch getLatch();
}
