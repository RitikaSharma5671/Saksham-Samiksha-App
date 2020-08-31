package com.example.student_details.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.student_details.R
import java.util.*

class AttendanceAdapter(context: Context?, private val filteredFormList: ArrayList<StudentInfo>) :
        ArrayAdapter<StudentInfo>(context!!, R.layout.item_attendance_row, filteredFormList) {


    private class ViewHolder {
        var formTitle: TextView? = null
        var formSubtitle: TextView? = null
        var formUpdateAlert: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       var row = convertView
        val holder: ViewHolder
//        if (row == null) {
//            holder = ViewHolder()
//            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            row = inflater.inflate(R.layout.form_chooser_list_item_multiple_choice, parent, false)
//            holder.formTitle = row.findViewById(R.id.form_title)
//            holder.formSubtitle = row.findViewById(R.id.form_subtitle)
//            holder.formUpdateAlert = row.findViewById(R.id.form_update_alert)
//            row.tag = holder
//        } else {
//            holder = row.tag as ViewHolder
//        }
//        val formAtPosition = filteredFormList[position]
//        val formIDAtPosition = formAtPosition[FormDownloadList.FORM_ID_KEY]
//        holder.formTitle!!.text = formAtPosition[FormDownloadList.FORMNAME]
//        holder.formSubtitle!!.text = formAtPosition[FormDownloadList.FORMID_DISPLAY]
//        if (formIdsToDetails[formIDAtPosition] != null
//                && (formIdsToDetails[formIDAtPosition].isNewerFormVersionAvailable()
//                        || formIdsToDetails[formIDAtPosition].areNewerMediaFilesAvailable())) {
//            holder.formUpdateAlert!!.visibility = View.VISIBLE
//        } else {
//            holder.formUpdateAlert!!.visibility = View.GONE
//        }
        return row!!
    }

}
