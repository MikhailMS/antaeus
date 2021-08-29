package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {

    fun processInvoices() {
        getPendingInvoices().forEach {
            println(it.id)
            invoiceService.updateInvoice(it.id, paymentProvider.charge(it))
        }
    }

    private fun getPendingInvoices() : List<Invoice> {
        return invoiceService.fetchAllPending()
    }
}
