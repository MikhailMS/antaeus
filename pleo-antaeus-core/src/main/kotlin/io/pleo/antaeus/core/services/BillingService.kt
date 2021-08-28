package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {

    fun run() {
        val invoices = getPendingInvoices()
        processInvoice(invoices)
    }

    private fun getPendingInvoices() : List<Invoice> {
        return invoiceService.fetchAllPending()
    }
    private fun processInvoice(invoices: List<Invoice>) {
        invoices.forEach {
            invoiceService.updateInvoice(it.id, paymentProvider.charge(it))
        }
    }
}
