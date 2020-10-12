package com.example.student_details.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R

class StreamSizeAdapter(private val filterCollectionViewModel: ClassFilterViewModel) :
        RecyclerView.Adapter<StreamSizeAdapter.SizeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val sizeItemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_cross_chips, parent, false)
        return SizeViewHolder(sizeItemView, filterCollectionViewModel)
    }

    override fun getItemCount(): Int {
        val totalSize = filterCollectionViewModel.dataFilterAttributes.value?.allStreams!!.size
        totalSize.let { return totalSize }
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class SizeViewHolder(itemView: View, val filterCollectionViewModel: ClassFilterViewModel) :
            RecyclerView.ViewHolder(itemView) {

        fun bindItem(position: Int) {

            val sizeView = itemView.findViewById<Button>(R.id.individual_filter1)
            val size = filterCollectionViewModel.dataFilterAttributes.value!!.allStreams!![position]
            sizeView.text = size
            val selectedStreams = filterCollectionViewModel.selectedStreams.value
            sizeView.isSelected = selectedStreams != null && selectedStreams.contains(size)

            if (sizeView.isSelected) {
                sizeView.setTextColor(ContextCompat.getColor(itemView.context, R.color.color4))
                sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background_selected)

            } else {
                sizeView.setTextColor(ContextCompat.getColor(itemView.context, R.color.color1))
                sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background)
            }
//
            sizeView.setOnClickListener {
                val selectedGrades : ArrayList<Int> = filterCollectionViewModel.selectedGrades.value!!
                if(selectedGrades.contains(11) || selectedGrades.contains(12)) {
                    sizeView.isSelected = !sizeView.isSelected
                    if (sizeView.isSelected) {
                        sizeView.setTextColor(itemView.context.resources.getColor(R.color.color4))
                        sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background_selected)
                    } else {
                        sizeView.setTextColor(itemView.context.resources.getColor(R.color.color1))
                        sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background)
                    }
                    filterCollectionViewModel.onStreamSelected(size, sizeView.isSelected)
                }else{
                    Toast.makeText(itemView.context, "Streams only valid for Grade 11 and 12.",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
