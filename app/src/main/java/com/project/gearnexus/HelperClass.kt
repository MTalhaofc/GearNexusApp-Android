package com.project.gearnexus




    data class HelperClass(
        var name: String = "",
        var email: String = "",
        var number: String= "",
        var password: String = ""
    ) {
        // Empty constructor for Firebase
        constructor() : this("", "", "", "")
    }

