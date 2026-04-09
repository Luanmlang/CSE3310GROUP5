package com.example.eflashshop.entities

data class User (
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val addresses: List<Address> = emptyList(),
    val createdAt: String
)

