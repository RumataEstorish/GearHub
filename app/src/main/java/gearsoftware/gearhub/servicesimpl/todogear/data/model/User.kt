package gearsoftware.gearhub.servicesimpl.todogear.data.model

import com.google.gson.annotations.SerializedName

/**
 * User model got from server
 */
data class User(
        @SerializedName("userid")
        val userId: String = "",

        @SerializedName("alias")
        val alias: String = "",

        @SerializedName("email")
        val email: String = "",

        @SerializedName("pro")
        val pro: Int = 0,

        @SerializedName("dateformat")
        val dateFormat: String = "",

        @SerializedName("timezone")
        val timeZone: Int = 0,

        @SerializedName("hidemonths")
        val hideMonths: Int = 0,

        @SerializedName("hotlistpriority")
        val hotlistPriority: Int = 0,

        @SerializedName("hotlistduedate")
        val hotlistDueDate: Int = 0,

        @SerializedName("showtabnums")
        val showTabNums: Int = 0,

        @SerializedName("lastedit_folder")
        val lastEditFolder: Int = 0,

        @SerializedName("lastedit_task")
        val lastEditTask: Int = 0,

        @SerializedName("lastdelete_task")
        val lastDeleteTask: Int = 0,

        @SerializedName("lastedit_context")
        val lastEditContext: Int = 0,

        @SerializedName("lastedit_goal")
        val lastEditGoal: Int = 0,

        @SerializedName("lastedit_location")
        val lastEditLocation: Int = 0,

        @SerializedName("lastedit_note")
        val lastEditNote: Int = 0,

        @SerializedName("lastdelete_note")
        val lastDeleteNote: Int = 0,

        @SerializedName("lastedit_list")
        val lastEditList: Int = 0,

        @SerializedName("lastedit_outline")
        val lastEditOutline: Int = 0
)
