package org.goafabric.invoice;

import org.goafabric.invoice.process.InvoiceProcess;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


/**
 * Created by amautsch on 26.06.2015.
 */

@SpringBootApplication//(exclude = RedisHealthContributorAutoConfiguration.class)
@RegisterReflectionForBinding(org.springframework.web.client.ResourceAccessException.class)
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(ApplicationContext context, @Value("${process.autostart:false}") Boolean processAutoStart) {
        return args -> {
            if ((args.length > 0) && ("-check-integrity".equals(args[0]))) {
                SpringApplication.exit(context, () -> 0);
            } else if (processAutoStart) {
                context.getBean(InvoiceProcess.class).run().get();
                SpringApplication.exit(context, () -> 0);
            }
        };
    }

}
