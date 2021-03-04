package com.example.student_details.ui.shikshamitr.ui

import android.app.Application
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities
import com.example.student_details.databinding.SamikshaItemShikshaMitraRowBinding
import com.example.student_details.models.realm.StudentInfo
import com.example.student_details.ui.TextDrawable
import com.google.android.gms.common.api.internal.ActivityLifecycleObserver
import kotlinx.android.synthetic.main.samiksha_item_shiksha_mitra_row.view.*

class ShikshaMitrAdapter(private  val  lifecycleObserver: ISMEditIconClickListener,private var shikshaMitrDetailsViewModel: ShikshaMitrDetailsViewModel
) : ListAdapter<StudentInfo, ShikshaMitrAdapter.StudentSMViewHolder>(
        OnStudentSMDataDiffCallback()) {

    inner class StudentSMViewHolder(private val samikshaItemShikshaMitraRowBinding: SamikshaItemShikshaMitraRowBinding,
                                    val application: Application,
                                    val shikshaMitrDetailsViewModel: ShikshaMitrDetailsViewModel) :
            RecyclerView.ViewHolder(samikshaItemShikshaMitraRowBinding.root) {
        fun bind(studentData: StudentInfo) {
            val drawable: Drawable = TextDrawable.builder().round().build(Utilities.findFirstLetterPosition(studentData.name),
                    ContextCompat.getColor(itemView.context, R.color.color_primary))
            itemView.sm_student_item_icon.setImageDrawable(drawable)
            itemView.sm_item_student_name.text = Utilities.convert(studentData.name)
            itemView.sm_item_student_srn.text = "(" + studentData.srn + ")"
            if (!studentData.isSMRegistered) {
                itemView.sm_details_row_layout.visibility = View.GONE
                itemView.sm_item_sm_no_registered_message.visibility = View.VISIBLE
            } else {
                itemView.sm_details_row_layout.visibility = View.VISIBLE
                itemView.sm_item_sm_no_registered_message.visibility = View.GONE
                itemView.sm_item_sm_contact.text = "(" + studentData.shikshaMitrContact + ")"
                itemView.sm_item_sm_name.text = studentData.shikshaMitrName
                if(studentData.shikshaMitrRelation.contains("Others::"))
                    itemView.shiksha_mitr_relation_value.text = "Others"+ " (" +studentData.shikshaMitrRelation.split("::")[1] +")"

                else
                itemView.shiksha_mitr_relation_value.text = studentData.shikshaMitrRelation
            }
            itemView.edit_sm_details_icon.setOnClickListener {
                lifecycleObserver.onEditSMClicked(studentData)
            }

        }
    }

    private lateinit var binding: SamikshaItemShikshaMitraRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentSMViewHolder {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.samiksha_item_shiksha_mitra_row, parent, false
        )
        return StudentSMViewHolder(
                binding,
                parent.context.applicationContext as Application,
                shikshaMitrDetailsViewModel
        )
    }

    override fun onBindViewHolder(holder: StudentSMViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo)
    }

    private fun initializeAnimationView(holder: StudentSMViewHolder) {
        holder.itemView.findViewById<ImageView>(R.id.sm_student_item_icon).setImageDrawable(null)
        holder.itemView.findViewById<TextView>(R.id.sm_item_student_name).text = ""
        holder.itemView.findViewById<TextView>(R.id.sm_item_student_srn).text = ""
        holder.itemView.findViewById<TextView>(R.id.sm_item_sm_name).text = ""
        holder.itemView.findViewById<TextView>(R.id.sm_item_sm_contact).text = ""
         holder.itemView.findViewById<TextView>(R.id.shiksha_mitr_relation_value).text = ""
    }
}

private class OnStudentSMDataDiffCallback :
        DiffUtil.ItemCallback<StudentInfo>() {
    override fun areContentsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.shikshaMitrName == newItem.shikshaMitrName)
                && (oldItem.shikshaMitrContact == newItem.shikshaMitrContact) && (oldItem.shikshaMitrRelation == newItem.shikshaMitrRelation)
                && (oldItem.isSMRegistered == newItem.isSMRegistered)
    }

    override fun areItemsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.shikshaMitrName == newItem.shikshaMitrName)
                && (oldItem.isSMRegistered == newItem.isSMRegistered)
                && (oldItem.shikshaMitrContact == newItem.shikshaMitrContact) && (oldItem.shikshaMitrRelation == newItem.shikshaMitrRelation)
    }

}

