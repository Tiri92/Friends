package thierry.friends.service

import android.app.Activity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import thierry.friends.BuildConfig
import thierry.friends.R

class FcmNotificationsSender(
    private var userFcmToken: String? = null,
    private var title: String? = null,
    private var body: String? = null,
    private var mActivity: Activity? = null
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey = BuildConfig.FIREBASE_SERVER_KEY

    fun sendNotification() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notificationObject = JSONObject()
            notificationObject.put("title", title)
            notificationObject.put("body", body)
            notificationObject.put("icon", R.drawable.baseline_chat_24)
            mainObj.put("notification", notificationObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                postUrl,
                mainObj,
                Response.Listener {
                    // code run is got response
                },
                Response.ErrorListener {
                    // code run is got error
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}