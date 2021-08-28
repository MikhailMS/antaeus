package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class InvoiceServiceTest {
    private val dal = mockk<AntaeusDal> {
        every { fetchInvoice(404) } returns null
        every { fetchInvoice(1) } returns Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID)
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
        val expected_invoice = Invoice(1, 1, Money(BigDecimal(200), Currency.GBP), InvoiceStatus.PAID)
        val found_invoice = invoiceService.fetch(1)

        assert(expected_invoice == found_invoice)
    }
}
