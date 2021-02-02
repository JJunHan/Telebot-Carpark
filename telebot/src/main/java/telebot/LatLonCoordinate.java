package telebot;
/*this is just a return class to hold the converting lat and long after converting from xy*/
public class LatLonCoordinate {
	private double latitude;
	private double longitude;
	
	public LatLonCoordinate(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public LatLonCoordinate() {
		// TODO Auto-generated constructor stub
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
}

