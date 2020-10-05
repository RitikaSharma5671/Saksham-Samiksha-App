package com.example.student_details.ui

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import com.example.student_details.Utilities.convert
import com.example.student_details.Utilities.findFirstLetterPosition
import com.example.student_details.databinding.StudentItemLayoutBinding
import com.example.student_details.models.realm.StudentInfo
import com.samagra.grove.logging.Grove
import kotlinx.android.synthetic.main.student_item_layout.view.*

class StudentAdapter(
        private var studentDetailsView: LifecycleOwner,
        private var studentDetailsViewModel: StudentDetailsViewModel
) : ListAdapter<StudentInfo, StudentAdapter.StudentViewHolder>(
        OnStudentDataDiffCallback()) {

    private lateinit var binding: StudentItemLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.student_item_layout, parent, false
        )
        return StudentViewHolder(
                binding,
                parent.context.applicationContext as Application,
                studentDetailsViewModel
        )
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.clearAnimation()
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val studentInfo = getItem(position)
        initializeAnimationView(holder)
        holder.bind(studentInfo)
    }

    private fun initializeAnimationView(holder: StudentAdapter.StudentViewHolder) {
        holder.itemView.findViewById<ImageView>(R.id.profile_pic).setImageDrawable(null)
        holder.itemView.findViewById<TextView>(R.id.name_srn).text = ""
        holder.itemView.findViewById<TextView>(R.id.father_details).text = ""
        holder.itemView.findViewById<TextView>(R.id.grade_section).text = ""
        holder.itemView.findViewById<TextView>(R.id.mother_details).text = ""
    }

    inner class StudentViewHolder(private val itemAttendanceRowBinding: StudentItemLayoutBinding,
                                  val application: Application,
                                  val markAttendanceViewModel: StudentDetailsViewModel) :
            RecyclerView.ViewHolder(itemAttendanceRowBinding.root) {

        fun bind(studentData: StudentInfo) {
            val context = itemView.context
            itemView.name_srn.text = convert(studentData.name) + " (" + studentData.srn + ")"
            if (studentData.grade > 10) {
                val content = SpannableString(studentData.grade.toString() + " - " + studentData.section + " (" + convert(studentData.stream) + ")")
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                itemAttendanceRowBinding.gradeSection.text = content
            } else {
                val content = SpannableString(studentData.grade.toString() + " - " + studentData.section)
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                itemAttendanceRowBinding.gradeSection.text = content
            }
            if (studentData.fatherContactNumber != null && studentData.fatherContactNumber.length > 2) {
                itemView.father_details.text = """${convert(studentData.fatherName)} (${studentData.fatherContactNumber})"""
            } else {
                itemView.father_details.text = convert(studentData.fatherName)
            }
            if (studentData.motherName != null)
                itemView.mother_details.text = convert(studentData.motherName)
            else
                itemView.mother_details.text = ""

            val gg = when (studentData.section) {
                "A" -> 0
                "B" -> 1
                "C" -> 2
                "D" -> 3
                else -> 4
            }
            var changedValue: String = studentData.section
            val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(studentData.name), getColor(context, R.color.color_primary))
            itemView.profile_pic.setImageDrawable(drawable)
            itemAttendanceRowBinding.gradeSection.setOnClickListener {
                val listItems = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")

                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("Choose Section")
                builder.setSingleChoiceItems(listItems, gg, fun(_: DialogInterface, which: Int) {
                    changedValue = listItems[which]
                    Grove.d("Position: " + which + " Value: " + listItems[which])
                })
                builder.setPositiveButton(
                        "Done",
                        fun(dialog: DialogInterface, _: Int) {
                            dialog.dismiss()
                            if (changedValue != studentData.section) {
                                markAttendanceViewModel.onSectionEdited(studentData, changedValue)
                            }
                        }
                )

                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }
            itemAttendanceRowBinding.editSection.setOnClickListener {
                val listItems = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("Choose Section")
                builder.setSingleChoiceItems(listItems, gg, fun(dialog: DialogInterface, which: Int) {
                    changedValue = listItems[which]
                   Grove.d("Student section change: >>>> Position: $which Value: ")
                })
                builder.setPositiveButton(
                        "Done",
                        fun(dialog: DialogInterface, which: Int) {
                            dialog.dismiss()
                            if (changedValue != studentData.section) {
                                markAttendanceViewModel.onSectionEdited(studentData, changedValue)
                            }
                        }
                )
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }
        }
    }
}

private class OnStudentDataDiffCallback :
        DiffUtil.ItemCallback<StudentInfo>() {
    override fun areContentsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.section == newItem.section)
    }

    override fun areItemsTheSame(
            oldItem: StudentInfo,
            newItem: StudentInfo
    ): Boolean {
        return (oldItem.srn == newItem.srn) && (oldItem.section == newItem.section)
    }
}
