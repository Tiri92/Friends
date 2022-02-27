package thierry.friends.model

data class User(
    val uid: String,
    var username: String,
    val userEmail: String,
    var userPicture: String,
    var userFcmToken: String,
    var FriendsRequestsSent: MutableList<String>,
    var FriendsRequestsReceived: MutableList<String>
) {
    constructor() : this("", "", "", "", "", ArrayList<String>(), ArrayList<String>())
}