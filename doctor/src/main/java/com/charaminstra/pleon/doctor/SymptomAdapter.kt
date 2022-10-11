package com.charaminstra.pleon.doctor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charaminstra.pleon.doctor.databinding.ItemSymptomBinding
import com.charaminstra.pleon.foundation.model.ActionData
import com.charaminstra.pleon.foundation.model.SymptomObject

class SymptomAdapter(): RecyclerView.Adapter<SymptomViewHolder>() {

    var viewItemList: List<SymptomObject> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        return SymptomViewHolder((ItemSymptomBinding.inflate(LayoutInflater.from(parent.context), parent, false)))
    }
    override fun getItemCount(): Int {
        return viewItemList.size
    }
    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        holder.bind(viewItemList[position], position)
    }

    fun setItemList(viewItemList : List<SymptomObject>) {
        this.viewItemList = viewItemList
    }

}
