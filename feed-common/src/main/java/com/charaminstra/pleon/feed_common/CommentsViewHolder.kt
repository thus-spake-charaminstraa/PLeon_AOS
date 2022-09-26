package com.charaminstra.pleon.feed_common

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charaminstra.pleon.feed_common.databinding.ItemCommentBinding
import com.charaminstra.pleon.foundation.model.CommentObject

class CommentViewHolder(
    private val binding: ItemCommentBinding
)  : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CommentObject){
        binding.commentText.text = item.content

        if(item.author_kind == "user"){
            binding.commentUserName.text = item.user?.nickname
            Glide.with(binding.root)
                .load(item.user?.thumbnail)
                .into(binding.commentUserImage)
        }else{
            binding.commentUserName.text = item.plant?.name
            Glide.with(binding.root)
                .load(item.plant?.thumbnail)
                .into(binding.commentUserImage)
        }
    }
}