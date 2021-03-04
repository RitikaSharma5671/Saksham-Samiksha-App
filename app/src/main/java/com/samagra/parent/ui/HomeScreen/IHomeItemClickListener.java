package com.samagra.parent.ui.HomeScreen;

public interface IHomeItemClickListener {
    void onFillFormsClicked();
    void onViewHelplineClicked();
    void onSubmitOfflineFormsClicked();
    void onViewODKSubmissionsClicked();
    void onMarkStudentAttendanceClicked();
    void onViewStudentAttendanceClicked();
    void onShikshaMitrRegnClicked();
    void onViewSchoolAttendanceClicked();
    void onViewTeacherAttendanceClicked();
    void onMarkTeacherAttendanceClicked();
    void onReportCOVIDCaseClicked();
    void onEditStudentDataClicked();
}

/**
 switch (v.getId()) {
 case R.id.fill_forms:
 break;
 case R.id.view_submitted_forms:
 break;
 case R.id.submit_forms:

 break;
 case R.id.need_help:

 break;
 */