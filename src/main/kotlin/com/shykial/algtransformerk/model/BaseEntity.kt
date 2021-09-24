package com.shykial.algtransformerk.model

import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
abstract class BaseEntity {
    @Id
    val id: UUID = UUID.randomUUID()

    @Version
    val version: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}