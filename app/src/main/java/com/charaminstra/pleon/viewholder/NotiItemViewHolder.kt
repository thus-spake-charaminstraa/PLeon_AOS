package com.charaminstra.pleon.viewholder

import com.charaminstra.pleon.NOTI_COMPLETE
import com.charaminstra.pleon.NOTI_LATER
import com.charaminstra.pleon.databinding.ItemNotiBinding
import com.charaminstra.pleon.foundation.model.ViewObject

class NotiItemViewHolder (
    private val binding: ItemNotiBinding): FeedCommonViewHolder(binding){
    override fun bind(item: ViewObject,
                      onClickFeed: (String) -> Unit,
                      onClickNoti: (String, String) -> Unit) {
        binding.notiTv.text= item.content
        binding.laterBtn.setOnClickListener {
            onClickNoti(item.id, NOTI_LATER)
        }
        binding.completeBtn.setOnClickListener {
            onClickNoti(item.id, NOTI_LATER)
        }
    }
}