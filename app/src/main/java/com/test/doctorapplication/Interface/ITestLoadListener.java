package com.test.doctorapplication.Interface;

import com.test.doctorapplication.Model.SentTestInformation;

import java.util.List;

public interface ITestLoadListener {
    void onTestLoadSuccess(List<SentTestInformation> testInfoList);
    void onTestLoadFailed(String message);
}
