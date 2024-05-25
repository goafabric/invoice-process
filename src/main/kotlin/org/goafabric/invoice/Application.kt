package org.goafabric.invoice

import org.goafabric.invoice.process.InvoiceProcess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext


@SpringBootApplication
class Application(@Autowired private val context: ApplicationContext,
                  @Value("\${process.autostart:false}") private val processAutoStart: Boolean):
    CommandLineRunner {
    override fun run(vararg args: String?) {
        if (args.isNotEmpty() && "-check-integrity" == args[0]) {
            SpringApplication.exit(context, ExitCodeGenerator { 0 })
        } else if (processAutoStart) {
            context.getBean(InvoiceProcess::class.java).run().get()
            SpringApplication.exit(context, ExitCodeGenerator { 0 })
        }
    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}