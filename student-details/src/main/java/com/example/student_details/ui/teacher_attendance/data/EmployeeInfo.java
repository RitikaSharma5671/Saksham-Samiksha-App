package com.example.student_details.ui.teacher_attendance.data;

import java.io.Serializable;
import java.util.List;

public class EmployeeInfo implements Serializable {
    private int total;
    private List<Employees> users;

    public List<Employees> getUserInformation() {
        return users;
    }

    public int getTotal() {
        return total;
    }
}

