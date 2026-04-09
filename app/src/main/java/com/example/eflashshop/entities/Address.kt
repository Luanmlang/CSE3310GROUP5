package com.example.eflashshop.entities

data class Address(
    val id: Long,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val userId: Long,
)
