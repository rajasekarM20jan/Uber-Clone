package model;

public class RideList {
    String rideID;
    String City;
    String Fare;

    public RideList(String rideID, String city, String fare) {
        this.rideID = rideID;
        City = city;
        Fare = fare;
    }

    public String getRideID() {
        return rideID;
    }

    public String getCity() {
        return City;
    }

    public String getFare() {
        return Fare;
    }
}
