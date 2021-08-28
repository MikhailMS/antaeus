package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class InvoiceServiceTest {
    private val dal = mockk<AntaeusDal> {
        every { fetchInvoice(404) } returns null
        every { fetchInvoice(1) } returns Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID)
        every { fetchPaidInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID))
        every { fetchPendingInvoices() } returns listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
    }

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will find invoice with id 1`() {
        val expectedInvoice = Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID)
        val foundInvoice    = invoiceService.fetch(1)

        assertEquals(expectedInvoice, foundInvoice)
    }

    @Test
    fun `will find all paid invoices`() {
        val expectedInvoices = listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID))
        val foundInvoices    = invoiceService.fetchAllPaid()

        assertEquals(expectedInvoices, foundInvoices)
    }

    @Test
    fun `will find all pending invoices`() {
        val expectedInvoices = listOf(Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PENDING))
        val foundInvoices    = invoiceService.fetchAllPending()

        assertEquals(expectedInvoices, foundInvoices)
    }
}
