package com.endofmaster.client;

/**
 * @author ZM.Wang
 */
public class VerifyIdCardResponse extends BasicResponse {

    private String status;
    private String msg;
    private String idCard;
    private String name;
    private String sex;
    private String area;
    private String province;
    private String city;
    private String prefecture;
    private String birthday;
    private String addrCode;
    private String lastCode;


    @Override
    public String toString() {
        return "VerifyIdCardResponse{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", idCard='" + idCard + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", area='" + area + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", prefecture='" + prefecture + '\'' +
                ", birthday='" + birthday + '\'' +
                ", addrCode='" + addrCode + '\'' +
                ", lastCode='" + lastCode + '\'' +
                '}';
    }

    public boolean success() {
        return "01".equals(status);
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getArea() {
        return area;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAddrCode() {
        return addrCode;
    }

    public String getLastCode() {
        return lastCode;
    }
}
