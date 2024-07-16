package com.project.gearnexus
data class HelperClass(
    var userid: String = "",
    var name: String = "",
    var email: String = "",
    var number: String = "",
    var password: String = "",
    var profileimage: String = "",

) {
    // Empty constructor for Firebase
    constructor() : this("", "", "", "", "", "")
}

