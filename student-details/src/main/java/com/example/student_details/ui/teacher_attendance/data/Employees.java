package com.example.student_details.ui.teacher_attendance.data;

import java.io.Serializable;

public class Employees implements Serializable {
        private String email;
        private String fullName;
        private String mobilePhone;
        private EmployeeUserData data;
        private String username;


        public String getFullName() {
                return fullName;
        }

        public void setFullName(String fullName) {
                this.fullName = fullName;
        }

        public String getMobilePhone() {
                return mobilePhone;
        }

        public void setMobilePhone(String mobilePhone) {
                this.mobilePhone = mobilePhone;
        }

        public EmployeeUserData getData() {
                return data;
        }

        public void setData(EmployeeUserData data) {
                this.data = data;
        }

        public String getUsername() {
                return username;
        }
}
