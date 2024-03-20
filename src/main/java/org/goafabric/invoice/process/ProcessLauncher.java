package org.goafabric.invoice.process;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProcessLauncher implements CommandLineRunner {
    private @Value("${test.mode:false}") Boolean testMode;
    private @Value("${process.autostart:false}") Boolean processAutoStart;

    private final ApplicationContext applicationContext;

    public ProcessLauncher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        if ( ((args.length > 0) && ("-check-integrity".equals(args[0]))) ) {
            SpringApplication.exit(applicationContext, () -> 0);
        }

        if (processAutoStart) {
            applicationContext.getBean(InvoiceProcess.class).run().get();
            SpringApplication.exit(applicationContext, () -> 0);
        }

    }
}
