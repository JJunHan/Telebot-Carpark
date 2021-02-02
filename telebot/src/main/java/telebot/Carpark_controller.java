package telebot;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
/*uses the gov.sg csv file to determine the nearest carparks to the given location*/
public class Carpark_controller { //extract data
	private ArrayList<String> carpark_no = new ArrayList<String>();
	private ArrayList<String> carpark_address = new ArrayList<String>();
	private ArrayList<Double> x_coord = new ArrayList<Double>();
	private ArrayList<Double> y_coord = new ArrayList<Double>();
	private ArrayList<Double> sorted_coords = new ArrayList<Double>();
	private double[] nearestaddress_xy = new double[5];
	private String[][] nearestaddress = new String[5][2]; //column 1 is all address, column 2 is carpark name
	private String[] nearestlist= new String[5]; //top 5 carpark name
	
	public void readcsv() throws IOException
	{
		System.out.println("in read");
		String csvFile = "hdb-carpark-information.csv";
	    String line = "";
	    
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(csvFile));
	    	line = br.readLine(); //skip header
	        while ((line = br.readLine()) != null) {
	            // use " as seperator
	        	String[] carparks = line.split("\""); //read one whole entry.
	            carpark_no.add(carparks[1]);
	            x_coord.add(Double.parseDouble(carparks[5]));
	            y_coord.add(Double.parseDouble(carparks[7]));
	            carpark_address.add(carparks[3]);
	            //System.out.println("carpark [address= " + carparks[3]);
	        }
	        //br.close();
	
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    System.out.println("out read");
	}
	
	/*calculates the distance between current location after converting to xy coordinates
	 * compares with all xy coordinates in the csv file and generates the distance between them in a arraylist
	 * sort the array list by order and take the first 5 nearest and store in a list*/
	public void find_nearest(double x_long, double y_lat) {
		SVY21Coordinate xystore = new SVY21Coordinate();
		xystore = Conversion_controller.executecomputeSVY21(y_lat,x_long); //lat long
		int[] indexoftoplist = new int[5]; // top 5 carpark index
		sorted_coords = new ArrayList<Double>();
		nearestlist= new String[5];
		Iterator<Double> iter_x = x_coord.iterator();
		Iterator<Double> iter_y = y_coord.iterator();
		double placeholder_x;
		double placeholder_y;
		
		while(iter_x.hasNext() && iter_y.hasNext()) {
			placeholder_x = iter_x.next() - xystore.getEasting(); 
			placeholder_y = iter_y.next() - xystore.getNorthing();
			sorted_coords.add(Math.sqrt(placeholder_x*placeholder_x+placeholder_y*placeholder_y)); //store all the differences
		}
		//look for index of 5 smallest sorted_coords
		//Collections.sort(sorted_coords);
		//sorted_coords.indexOf(Collections.min(sorted_coords));
		for(int i = 0; i < 5 ; i ++) {
			indexoftoplist[i] = sorted_coords.indexOf(Collections.min(sorted_coords)); //get index of top closest carpark
			//System.out.println("from inside: " + indexoftoplist[i]);
			sorted_coords.set(indexoftoplist[i], sorted_coords.get(indexoftoplist[i])*4); //replace the minimum by *4 so can take others
		}
		
		for(int i = 0; i < 5; i ++) {
			nearestlist[i] = carpark_no.get(indexoftoplist[i]); //get back name of top 5 nearest carpark number
			//System.out.println("from inside: " + nearestlist[i]);
		}
	}
	/*logic to find for either FULL address input, PARTIAL address input and NO valid address*/
	public boolean find_closest_address(String address) {
		Iterator<String> iter_address = carpark_address.iterator();
		StringTokenizer st = new StringTokenizer(address);
		int no_of_token = st.countTokens();
		String tokenholder = new String();
		tokenholder = st.nextToken();
		int count = 0;
		//for(int j = 0; j< no_of_token;j++) {
			iter_address = carpark_address.iterator(); //reset iteration for no of token int times to iterate thru whole list.
			for(int i = 0; iter_address.hasNext(); i ++) { //loop thru whole address array
				String x = iter_address.next();
				//System.out.println("in second for loop" + x);
				//System.out.println("printing x and address" + x +"<<" + address);
				if(x.toString().matches(address)) {
					//System.out.println("sucessful enter: ");
					nearestaddress[0][0] = x;
					nearestaddress[0][1] = carpark_no.get(i);
					nearestaddress[1][0] = ""; //set to null
					//System.out.println("sucessful enter: " + nearestaddress[1][0]);
					return true;
				}
			}
		for(int j = 0; j< no_of_token;j++) {
			iter_address = carpark_address.iterator(); //reset iteration
			for(int i = 0; iter_address.hasNext(); i ++) {
				String x = iter_address.next();
				if(x.contains(tokenholder)) { //if contain address returns true, means address is found inside.
					System.out.println("found address match " + x);
					nearestaddress[count][0] = x; //store address string inside
					//System.out.println(" store address string inside" + nearestaddress[count][0]);
					nearestaddress[count][1] = carpark_no.get(i); //store carpark number inside
					//System.out.println("store carpark number inside" + nearestaddress[count][1]);
					nearestaddress_xy[count] = x_coord.get(i);
					nearestaddress_xy[count] = y_coord.get(i);
					count ++;
					if(count == 5) {return true;}
				}

				
			}
			if(st.hasMoreTokens())
				{tokenholder = st.nextToken();}
			else break;
				//if(count == 5) {return true;}
		}
		if(count > 0) return true;
		else return false;
		
	}
	
	public String getCarpark_no(int index)	{
		return carpark_no.get(index);
	}
	public double get_x_coord(int index)	{
		return x_coord.get(index);
	}
	public double get_y_coord(int index)	{
		return y_coord.get(index);
	}
	
	public ArrayList<String> getCarpark_list()	{
		return carpark_no;
	}
	public ArrayList<Double> get_x_coord_list()	{
		return x_coord;
	}
	public ArrayList<Double> get_y_coord_list()	{
		return y_coord;
	}
	public ArrayList<Double> get_sorted_coords() {
		return sorted_coords;
	}
	public String[] get_nearestlist() {
		return nearestlist;
	}
	public String[][] get_nearestaddress() {
		return nearestaddress;
	}
	public double[] get_nearestaddres_xy() {
		return nearestaddress_xy;
	}
	
}
