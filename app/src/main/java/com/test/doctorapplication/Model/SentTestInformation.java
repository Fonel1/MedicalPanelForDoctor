package com.test.doctorapplication.Model;


public class SentTestInformation {
    private String patientName, patientEmail, time, doctorName, testName, testId, summaryTxt;
    private String photo;
    private boolean done;

    public SentTestInformation() {
    }

    public String getSummaryTxt() {
        return summaryTxt;
    }

    public void setSummaryTxt(String summaryTxt) {
        this.summaryTxt = summaryTxt;
    }

    public SentTestInformation(String testId) {
        this.testId = testId;
    }

    public SentTestInformation(String testName, String photo) {
        this.testName = testName;
        this.photo = photo;
    }

    public SentTestInformation(String patientName, String patientEmail, String time, String doctorName, boolean done) {
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.time = time;
        this.doctorName = doctorName;
        this.done = done;

    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

}
