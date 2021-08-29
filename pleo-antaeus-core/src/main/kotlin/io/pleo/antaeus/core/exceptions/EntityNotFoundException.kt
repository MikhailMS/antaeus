package io.pleo.antaeus.core.exceptions

abstract class EntityNotFoundException(entity: String, id: Int) : Exception("$entity with ID '$id' was not found")
