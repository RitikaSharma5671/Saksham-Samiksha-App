package com.example.student_details.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R

class SizeFilterAdapter(private val filterCollectionViewModel: ClassFilterViewModel) :
        RecyclerView.Adapter<SizeFilterAdapter.SizeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val sizeItemView = LayoutInflater.from(parent.context).inflate(R.layout.filter_size_item, parent, false)
        return SizeViewHolder(sizeItemView, filterCollectionViewModel)
    }

    override fun getItemCount(): Int {
        val totalSize = filterCollectionViewModel.dataFilterAttributes.value?.allGrades!!.size
        totalSize?.let { return totalSize }
        return 0
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class SizeViewHolder(itemView: View, private val filterCollectionViewModel: ClassFilterViewModel) :
            RecyclerView.ViewHolder(itemView) {

        fun bindItem(position: Int) {

            val sizeView = itemView.findViewById<Button>(R.id.individual_filter)
            val size = filterCollectionViewModel.dataFilterAttributes.value!!.allGrades!![position]
//            val availableSizes = filterCollectionViewModel.collectionNodeResponse.value!!.availableSizes
            sizeView.text = size.toString()
            sizeView.isEnabled = true


            val selectedGrades = filterCollectionViewModel.selectedGrades.value
            sizeView.isSelected = selectedGrades != null && selectedGrades.contains(size)

            if (sizeView.isSelected) {
                sizeView.setTextColor(itemView.context.resources.getColor(R.color.color4))
                sizeView.background = ContextCompat.getDrawable(itemView.context,
                        R.drawable.buttonstyle4_background_selected)

            } else {
                sizeView.setTextColor(itemView.context.resources.getColor(R.color.color1))
                sizeView.background = ContextCompat.getDrawable(itemView.context,R.drawable.buttonstyle4_background)
            }
//
            sizeView.setOnClickListener {
                sizeView.isSelected = !sizeView.isSelected
                if (sizeView.isSelected) {
                    sizeView.setTextColor(itemView.context.resources.getColor(R.color.color4))
                    sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background_selected)

                } else {
                    sizeView.setTextColor(itemView.context.resources.getColor(R.color.color1))
                    sizeView.background = itemView.context.resources.getDrawable(R.drawable.buttonstyle4_background)

                filterCollectionViewModel.onGradeSelected(size, sizeView.isSelected)
                }
            }
        }
    }
}
