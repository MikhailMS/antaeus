package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingCronServiceTest {

    private val dal             = mockk<AntaeusDal> {}
    private val paymentProvider = mockk<PaymentProvider> {}

    private val invoiceService = InvoiceService(dal = dal)
    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService, retries = 2, timeout = 100)
    private val billingCronService = BillingCronService(billingService, "*/1 * * * * ?")

     @Test
     fun `billingCronService works smoothly`() {
         every { billingService.processInvoices() } returns Unit
         every { dal.fetchPendingInvoices() } returns listOf(Invoice(1, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
         every { paymentProvider.charge(Invoice(1, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) } returns true
         every { invoiceService.updateInvoiceStatus(1, true) } returns Unit

         billingCronService.start()

         Thread.sleep(2000)

         verify { invoiceService.updateInvoiceStatus(1, true) }
         verify { paymentProvider.charge(Invoice(1, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }
     }
}