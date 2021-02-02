package telebot;
/*this is just a return class to hold the converting x and y coord after converting from lat long*/
public class SVY21Coordinate {
	private double easting;
	private double northing;
	
	public SVY21Coordinate(double northing, double easting) {
		super();
		this.northing = northing;
		this.easting = easting;
	}
	public SVY21Coordinate() {
		// TODO Auto-generated constructor stub
	}
	public double getEasting(){
		return easting;
	}
	public double getNorthing() {
		return northing;
	}
	
}

