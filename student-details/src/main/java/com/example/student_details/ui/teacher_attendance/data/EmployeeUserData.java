package com.example.student_details.ui.teacher_attendance.data;

import java.io.Serializable;

public class EmployeeUserData  implements Serializable {
    private String accountName;
    private RoleData roleData;
    private String phone;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public RoleData getRoleData() {
        return roleData;
    }

    public void setRoleData(RoleData roleData) {
        this.roleData = roleData;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
