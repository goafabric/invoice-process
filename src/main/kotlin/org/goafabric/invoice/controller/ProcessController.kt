package org.goafabric.invoice.controller

import org.goafabric.invoice.process.InvoiceProcess
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.IntStream

@RestController
@RequestMapping(value = ["processes"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ProcessController(private val invoiceProcess: InvoiceProcess) {
    @GetMapping("start") //@RolesAllowed("INVOICE")
    fun start(): String {
        invoiceProcess.run()
        return "launched"
    }

    @GetMapping("loop") //@RolesAllowed("INVOICE")
    fun loop(): String {
        IntStream.range(0, 10).forEach { i: Int -> invoiceProcess.run().get() }
        return "launched"
    }
}
