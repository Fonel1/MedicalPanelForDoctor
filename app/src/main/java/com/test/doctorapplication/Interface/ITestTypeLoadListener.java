package com.test.doctorapplication.Interface;

import java.util.List;

public interface ITestTypeLoadListener {
    void onTestTypeLoadSuccess(List<String> areaNameList);
    void onTestTypeLoadFailed(String message);
}
