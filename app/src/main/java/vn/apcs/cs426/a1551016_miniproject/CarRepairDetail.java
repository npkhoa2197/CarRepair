package vn.apcs.cs426.a1551016_miniproject;

/**
 * Created by khoanguyen on 6/21/17.
 */

public class CarRepairDetail {
    private String title;
    private String openingStatus;
    private String address;

    public CarRepairDetail(String title, String openingStatus, String address) {
        this.title = title;
        this.openingStatus = openingStatus;
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public String getOpeningStatus() {
        return openingStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOpeningStatus(String openingStatus) {
        this.openingStatus = openingStatus;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
