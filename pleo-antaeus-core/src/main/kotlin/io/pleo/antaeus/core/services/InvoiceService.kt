/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchAllPending(): List<Invoice> {
        return dal.fetchPendingInvoices()
    }

    fun fetchAllPaid(): List<Invoice> {
        return dal.fetchPaidInvoices()
    }

    fun updateInvoiceStatus(id: Int, status: Boolean) {
        dal.updateInvoiceStatus(id, status)
    }
}
