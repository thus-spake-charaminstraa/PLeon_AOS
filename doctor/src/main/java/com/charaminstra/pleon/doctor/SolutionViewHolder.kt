package com.charaminstra.pleon.doctor

import androidx.recyclerview.widget.RecyclerView
import com.charaminstra.pleon.doctor.databinding.ItemSolutionBinding
import com.charaminstra.pleon.foundation.model.CauseObject

class SolutionViewHolder(val binding: ItemSolutionBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CauseObject, position: Int) {
        binding.solutionNum.text = (position+1).toString()
        binding.solutionTxt.text = item.guide
    }
}