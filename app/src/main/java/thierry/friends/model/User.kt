package thierry.friends.model

data class User(
    val uid: String,
    var username: String,
    val userEmail: String,
    var userPicture: String
) {
    constructor() : this("", "", "", "")
}