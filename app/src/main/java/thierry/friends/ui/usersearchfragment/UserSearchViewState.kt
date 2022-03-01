package thierry.friends.ui.usersearchfragment

import thierry.friends.model.User

data class UserSearchViewState(
    var currentUser: User? = null,
    var userSearchResult: List<User>? = null,
    var listOfFriendsRequestsReceived: List<User>? = null,
    var listOfFriendsRequestsReceivedById: List<String>? = null,
    var listOfFriendsRequestsSentById: List<String>? = null
)