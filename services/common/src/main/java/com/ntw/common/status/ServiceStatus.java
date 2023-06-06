package com.ntw.common.status;

import com.google.gson.Gson;

import java.io.Serializable;

public class ServiceStatus implements Serializable {
    private String serviceId;
    private String serviceHost;
    private int servicePort;
    private String serviceTime;

    public ServiceStatus() {
        this.serviceId = "Uninitialized";
        this.serviceHost = "Not Attempted";
        this.servicePort = 0;
        this.serviceTime = "Not Attempted";
    }

    public ServiceStatus(String serviceId) {
        this();
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "{" +
                "\"serviceId\":" + (serviceId == null ? "null" : "\"" + serviceId + "\"") + ", " +
                "\"serviceHost\":" + (serviceHost == null ? "null" : "\"" + serviceHost + "\"") + ", " +
                "\"servicePort\":" + servicePort + ", " +
                "\"serviceTime\":" + (serviceTime == null ? "null" : "\"" + serviceTime + "\"") +
                "}";
    }
}
