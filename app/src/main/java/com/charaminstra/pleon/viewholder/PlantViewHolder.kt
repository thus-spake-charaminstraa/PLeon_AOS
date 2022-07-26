package com.charaminstra.pleon.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.charaminstra.pleon.foundation.model.PlantDataObject


abstract class PlantViewHolder(
    binding: ViewBinding
): RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(viewObject: PlantDataObject)
}


