package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal


class BillingServiceTest {
    private val dal             = mockk<AntaeusDal> {}
    private val paymentProvider = mockk<PaymentProvider> {}

    private val invoiceService = InvoiceService(dal = dal)
    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService, retries = 2, timeout = 100)


    @Test
    fun `paymentProvider throws exception - customer associated with invoice is not found`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(9, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(9, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }.throws(CustomerNotFoundException(3))

        assertThrows<CustomerNotFoundException> {
            paymentProvider.charge(Invoice(9, 3, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        }
    }

    @Test
    fun `paymentProvider throws exception - currency associated with invoice is not a match in customer`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(10, 5, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(10, 5, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }.throws(CurrencyMismatchException(10, 5))

        assertThrows<CurrencyMismatchException> {
            paymentProvider.charge(Invoice(10, 5, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        }
    }

    @Test
    fun `paymentProvider throws exception - there is an issue with network`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(8, 2, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(8, 2, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }.throws(NetworkException())

        assertThrows<NetworkException> {
            paymentProvider.charge(Invoice(8, 2, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        }
    }

    @Test
    fun `paymentProvider throws exception - should never happen but never say never`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(7, 6, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(7, 6, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }.throws(Exception())

        assertThrows<Exception> {
            paymentProvider.charge(Invoice(7, 6, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        }
    }

    @Test
    fun `invoice payment succeeds, so Invoice record in DB is updated`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) } returns true
        every { invoiceService.updateInvoiceStatus(1, true) } returns Unit

        billingService.processInvoices()

        verify (exactly = 1) { invoiceService.updateInvoiceStatus(1, true) }
    }

    @Test
    fun `invoice payment does not succeed, so Invoice record in DB is not updated`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) } returns false
        every { invoiceService.updateInvoiceStatus(1, false) } returns Unit

        billingService.processInvoices()

        verify (exactly = 0) { invoiceService.updateInvoiceStatus(1, false) }
    }

    @Test
    fun `invoice payment does not succeed after retries, so Invoice record in DB is not updated`() {
        every { dal.fetchPendingInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        every { paymentProvider.charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }.throws(NetworkException())
        every { invoiceService.updateInvoiceStatus(1, false) } returns Unit

        billingService.processInvoices()

        verify (exactly = 3) { paymentProvider.charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) }
        verify (exactly = 0) { invoiceService.updateInvoiceStatus(1, false) }
    }
}