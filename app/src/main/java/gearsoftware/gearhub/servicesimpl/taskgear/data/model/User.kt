package gearsoftware.gearhub.servicesimpl.taskgear.data.model

import com.google.gson.annotations.SerializedName

/**
 * User returned from server
 */
data class User(
        @SerializedName("id")
        val id: Long = 0,

        @SerializedName("token")
        val token: String = "",

        @SerializedName("email")
        val email: String = "",

        @SerializedName("full_name")
        val fullName: String = "",

        @SerializedName("inbox_project")
        val inboxProject: Long = 0,

        @SerializedName("start_page")
        val startPage: String = "",

        @SerializedName("start_day")
        val startDay: Int = 0,

        @SerializedName("next_week")
        val nextWeek: Int = 0,

        @SerializedName("time_format")
        val timeFormat: Int = 0,

        @SerializedName("date_format")
        val dateFormat: Int = 0,

        @SerializedName("sort_order")
        val sortOrder: Int = 0,

        @SerializedName("default_reminder")
        val defaultReminder: String = "",

        @SerializedName("auto_reminder")
        val autoReminder: Int = 0,

        @SerializedName("mobile_reminder")
        val mobileReminder: String = "",

        @SerializedName("mobile_host")
        val mobileHost: String = "",

        @SerializedName("completed_count")
        val completedCount: Int = 0,

        @SerializedName("completed_today")
        val completedToday: Int = 0,

        @SerializedName("karma")
        val karma: Int = 0,

        @SerializedName("karma_trend")
        val karmaTrend: String = "",

        @SerializedName("is_premium")
        val isPremium: Boolean = false,

        @SerializedName("premium_until")
        val premiumUntil: String = "",

        @SerializedName("is_biz_admin")
        val is_biz_admin: Boolean = false,

        @SerializedName("business_account_id")
        val business_account_id: Long = 0,

        @SerializedName("image_id")
        val image_id: String = "",

        @SerializedName("avatar_small")
        val avatarSmall: String = "",

        @SerializedName("avatar_medium")
        val avatar_medium: String = "",

        @SerializedName("avatar_big")
        val avatarBig: String = "",

        @SerializedName("avatar_s640")
        val avatarS640: String = "",

        @SerializedName("theme")
        val theme: Int = 0,

        @SerializedName("join_date")
        val join_date: String = ""
)
