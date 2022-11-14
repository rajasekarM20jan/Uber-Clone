package model;

public class PreviousRide {

        String rideID;
        String City;
        String Fare;

        public PreviousRide(String rideID, String city, String fare) {
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

