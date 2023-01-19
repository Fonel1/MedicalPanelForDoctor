package com.test.doctorapplication.Interface;

import com.google.firebase.firestore.DocumentSnapshot;
import com.test.doctorapplication.Model.MyNotification;

import java.util.List;

public interface INotificationLoadListener {
    void onNotificationLoadSuccess(List<MyNotification> notificationList, DocumentSnapshot lastDocument);
    void onNotificationLoadFailed(String message);
}
