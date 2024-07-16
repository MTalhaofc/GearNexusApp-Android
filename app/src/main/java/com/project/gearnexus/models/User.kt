package com.project.gearnexus.models

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val number: String = "",
    val password: String = "",
    val profileImageUrl: String = "",
    val profileimage: String = "" // if this field is required, otherwise it can be omitted
)
