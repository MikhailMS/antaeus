package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CustomerServiceTest {
    private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null
        every { fetchCustomer(1) } returns Customer(1, Currency.GBP)
    }

    private val customerService = CustomerService(dal = dal)

    @Test
    fun `will throw if customer is not found`() {
        assertThrows<CustomerNotFoundException> {
            customerService.fetch(404)
        }
    }

    @Test
    fun `will find customer with id 1`() {
        val expectedCustomer = Customer(1, Currency.GBP)
        val foundCustomer    = customerService.fetch(1)

        assertEquals(expectedCustomer, foundCustomer)
    }
}
