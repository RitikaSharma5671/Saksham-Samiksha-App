package com.example.student_details.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.student_details.R
import kotlinx.android.synthetic.main.student_item_layout.view.*

class StudentAdapter(var studentList: List<StudentInfo>, onSelectListener: OnSelectListener, onEditListener: EditListener, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class AtHomeViewHolder(itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(studentData: StudentInfo, position: Int) {
            var studentName = studentData.studentName.substring(0, 1).toUpperCase() + studentData.studentName.substring(1).toLowerCase()
            var fatherName = studentData.fatherName.substring(0, 1).toUpperCase() + studentData.fatherName.substring(1).toLowerCase()
            var motherName = studentData.motherName.substring(0, 1).toUpperCase() + studentData.motherName.substring(1).toLowerCase()
            itemView.name.text = processString(studentData.studentName)
            itemView.fatherDetails.text = processString(studentData.fatherName)
            itemView.motherDetails.text = processString(studentData.motherName)
            itemView.srn_number.text = studentData.srn
            val drawable: Drawable = TextDrawable.builder().round().build(findFirstLetterPosition(studentData.studentName), getColor(context, R.color.color_primary))
            itemView.profile_pic.setImageDrawable(drawable)
        }


        private fun processString(details: String): String {
            val ch: CharArray = details.toCharArray()
            for (i in details.indices) {

                // If first character of a word is found
                if (i == 0 && ch[i] != ' ' ||
                        ch[i] != ' ' && ch[i - 1] == ' ') {

                    // If it is in lower-case
                    if (ch[i] in 'a'..'z') {

                        // Convert into Upper-case
                        ch[i] = (ch[i] - 'a' + 'A'.toInt()).toChar()
                    }
                } else if (ch[i] in 'A'..'Z') // Convert into Lower-Case
                    ch[i] = (ch[i] + 'a'.toInt() - 'A'.toInt())
            }

            // Convert the char array to equivalent String

            // Convert the char array to equivalent String
            val st = String(ch)
            return st
        }

        fun findFirstLetterPosition(messageType: String): String {
            return if (!TextUtils.isEmpty(messageType) && messageType.isNotEmpty()) {
                messageType.substring(0, 1).toUpperCase()
            } else "S"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val collectionCardView =
                LayoutInflater.from(parent.context).inflate(R.layout.student_item_layout, parent, false)
        return AtHomeViewHolder(collectionCardView)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AtHomeViewHolder).bind(
                studentList[position], position
        )
    }
}
