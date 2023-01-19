package com.test.doctorapplication.Model.Event;

import com.test.doctorapplication.Adapter.TestInfoAdapter;
import com.test.doctorapplication.Model.SentTestInformation;

import java.util.List;

public class DoctorReceivedTestLoadEvent {
    private boolean success;
    private String message;
    private List<SentTestInformation> sentTestInformationList;

    public DoctorReceivedTestLoadEvent(boolean success, List<SentTestInformation> sentTestInformationList) {
        this.success = success;
        this.sentTestInformationList = sentTestInformationList;
    }

    public DoctorReceivedTestLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SentTestInformation> getsentTestInformationList() {
        return sentTestInformationList;
    }

    public void setsentTestInformationList(List<SentTestInformation> sentTestInformationList) {
        this.sentTestInformationList = sentTestInformationList;
    }
}
