package com.test.doctorapplication.Interface;

import com.test.doctorapplication.Model.AppointmentInformation;

import java.util.List;

public interface IDataSlotLoadListener {
    void onDataSlotLoadSuccess(List<AppointmentInformation> dataSlot);
    void onDataSlotLoadFailed(String message);
    void onDataSlotLoadEmpty();

}