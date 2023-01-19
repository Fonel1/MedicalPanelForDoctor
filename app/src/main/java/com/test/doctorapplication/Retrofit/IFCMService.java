package com.test.doctorapplication.Retrofit;

import com.test.doctorapplication.Model.FCMResponse;
import com.test.doctorapplication.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJkizZwU:APA91bGStjvr-Po0lSMqHByuk3bkZgRuuh2dIpJpDAMCwDVY6eCBIHQ2SBHnm9Qop1OlAVAo3LP4VT9-ajwli-lzw3VIpdWuuwlpC7oEUNuOa16Fw-H8sywpC9Fsxw7Q7_3qEuroHjD6"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
