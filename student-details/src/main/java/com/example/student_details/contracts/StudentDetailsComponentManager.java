package com.example.student_details.contracts;

import com.samagra.commons.MainApplication;

public class StudentDetailsComponentManager {
    public static IStudentDetailsContract iStudentDetailsContract;

    /**
     *
     * @param profileContractImpl
     * @param application
     * @param baseURL
     * @param applicationID
     */
    public static void registerProfilePackage(IStudentDetailsContract profileContractImpl) {
        iStudentDetailsContract = profileContractImpl;
    }

}