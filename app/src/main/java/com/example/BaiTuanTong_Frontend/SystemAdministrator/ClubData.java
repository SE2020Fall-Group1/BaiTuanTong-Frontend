package com.example.BaiTuanTong_Frontend.SystemAdministrator;

public class ClubData {
    private String ClubName;
    private String AdminName;

    public ClubData(String clubName, String adminName) {
        this.ClubName = clubName;
        this.AdminName = adminName;
    }

    public String getClubName() {
        return ClubName;
    }
    public String getAdminName() {
        return  AdminName;
    }
    public void setAdminName(String adminName) {
        AdminName = adminName;
    }
}
