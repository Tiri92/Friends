package thierry.friends.model

data class User(
    val uid: String,
    var username: String,
    val userEmail: String,
    var userPicture: String,
    var userFcmToken: String,
    var listOfFriends: MutableList<String>
) {
    constructor() : this("", "", "", "", "", ArrayList<String>())
}