package com.test.doctorapplication.Interface;

import com.test.doctorapplication.Model.Clinic;

import java.util.List;

public interface IClinicLoadListener {
    void onClinicLoadSuccess(List<Clinic> clinicList);
    void onClinicLoadFailed(String message);
}