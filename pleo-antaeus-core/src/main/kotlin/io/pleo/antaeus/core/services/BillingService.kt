package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService:  InvoiceService,
    private val retries:         Int,
    private val timeout:         Long
) {

    fun processInvoices() {
        getPendingInvoices().forEach {
            payInvoice(it)
        }
    }

    private fun getPendingInvoices() : List<Invoice> {
        return invoiceService.fetchAllPending()
    }

    private fun payInvoice(invoice: Invoice) {
        var invoicePaid = false
        for (i in 0..retries) {
            try {
                invoicePaid = paymentProvider.charge(invoice)
                break
            } catch (exception: CustomerNotFoundException) {
                logger.error(exception) {}
            } catch (exception: CurrencyMismatchException) {
                logger.error(exception) {}
            } catch (exception: NetworkException) {
                logger.error(exception) {}
                if (retries > 0) {
                    Thread.sleep(timeout)
                }
            } catch (exception: Exception) {
                logger.error(exception) { "Unexpected error detected" }
            }
        }

        logger.debug("Is invoice with ID ${invoice.id} paid for? [${invoicePaid}]")
        if (invoicePaid) {
            invoiceService.updateInvoiceStatus(invoice.id, invoicePaid)
        }
    }
}
