package com.project.gearnexus.models

data class Post(
    var postId: String? = null,
    var email: String? = null,  // Email of the user posting the ad
    var imageUrl: String? = null,  // URL of the image for the post
    var name: String? = null,  // Name of the post
    var price: String? = null,  // Price of the item in the post
    var details: String? = null,  // Details of the item in the post
    var location: String? = null,  // Location where the item is available
    var contactNumber: String? = null  // Contact number for the item in the post
)
