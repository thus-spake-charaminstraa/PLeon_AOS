package com.charaminstra.pleon.common.data

data class UserRequestBody (
    val nickname : String,
    val thumbnail: String?
)

data class UserPushSettingRequestBody (
    val comment_push_noti : Boolean,
    val guide_push_noti: Boolean
)