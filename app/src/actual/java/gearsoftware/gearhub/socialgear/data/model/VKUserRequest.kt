package gearsoftware.gearhub.socialgear.data.model

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKUserRequest(uids: IntArray = intArrayOf()) : VKRequest<VKUser>("users.get") {
    init {
        if (uids.isNotEmpty()) {
            addParam("user_ids", uids.joinToString(","))
        }
        addParam("fields", "photo_200")
    }


    override fun parse(responseJson: JSONObject): VKUser =
            VKUser.parse(responseJson.getJSONArray("response").getJSONObject(0))

}