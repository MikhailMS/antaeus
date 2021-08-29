package io.pleo.antaeus.app

import com.sksamuel.hoplite.Masked

data class Configuration(val dbConf: DatabaseConf, val billingServiceConf: BillingServiceConf)
data class DatabaseConf(val tempPrefix: String, val tempSuffix: String,val url: String, val driver: String, val user: String, val password: Masked)
data class BillingServiceConf(val cronExpression: String, val retries: Int, val timeout: Long)

