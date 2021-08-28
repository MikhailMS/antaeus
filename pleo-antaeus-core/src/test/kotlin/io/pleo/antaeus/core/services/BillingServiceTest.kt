package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class BillingServiceTest {
    private val dal = mockk<AntaeusDal> {
        every { fetchPendingInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
    }

    private val paymentProvider = mockk<PaymentProvider> {
        every { charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)) } returns true
    }

    private val invoiceService = InvoiceService(dal = dal)

    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

    @Test
    fun `will perform invoice payment`() {
        // Placeholder for future test
        assertEquals(true, paymentProvider.charge(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING)))
    }
}