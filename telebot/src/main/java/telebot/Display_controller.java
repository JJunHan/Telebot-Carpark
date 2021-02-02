package telebot;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.objects.Update;
/*retrieve data from the gov.sg api that stores all the respective details of the carpark.
 * this data is subsequently used after the user has chosen a carpark*/
public class Display_controller {
	protected String current_carparkNumber;
	protected String current_Address;
	protected double current_x_coord;
	protected double current_y_coord;
	protected String current_car_park_type;
	protected String current_type_of_parking_system;
	protected String current_short_term_parking;
	protected String current_free_parking;
	protected boolean current_night_parking;
	protected int current_car_park_decks;
	protected double current_gantry_height;
	protected boolean current_car_park_basement;
	
	public void retrieve_CarparkCsv(String carpark_Number) throws IOException {
    	String carpark_url = carpark_Number;
		String csv_URL = "https://data.gov.sg/api/action/datastore_search?resource_id=139a3035-e624-4f56-b63f-89ae28d4ae4c&q=" + carpark_url;
        String csvrawData = Jsoup.connect(csv_URL)
        		.ignoreContentType(true)
        		.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
                .referrer("http://www.google.com") 
        		.get()
        		.wholeText();
        String[] csvrawData1 = csvrawData.split("\\[|\\]");
        String[] csvrawData2 = csvrawData1[3].split("\\{|\\}");
        String[] array_csvData = csvrawData2[1].split("\"");

        current_carparkNumber = carpark_Number;
        current_Address = array_csvData[41]; // Address formatting
        current_x_coord = Double.parseDouble(array_csvData[19]); // x_coord formatting
        current_y_coord = Double.parseDouble(array_csvData[15]); // y_coord formatting
        current_car_park_type = array_csvData[11]; // car_park_type formatting
        current_type_of_parking_system = array_csvData[55]; // type_of_parking_system formatting
        current_short_term_parking = array_csvData[7]; // current_short_term_parking formatting
        current_free_parking = array_csvData[25]; // free_parking formatting
        
        if(StringUtils.equals(array_csvData[37], "YES")) { // night_parking formatting
        	current_night_parking = true;
        }else { current_night_parking = false; }
        
        try { // car_park_decks formatting
        	current_car_park_decks = Integer.parseInt(array_csvData[45]);
        } catch(Exception e) {
        	current_car_park_decks = 0;
        }
        
        try { // car_park_decks formatting
        	current_gantry_height = Double.parseDouble(array_csvData[29]);
        } catch(Exception e) {
        	current_gantry_height = 0;
        }
        
        if(StringUtils.equals(array_csvData[33], "Y")) { // car_park_basement formatting
        	current_car_park_basement = true;
        }else { current_car_park_basement = false; }
        
	}
	
	public boolean csvdata_Check(String carpark_Number) throws IOException {
		if(StringUtils.equals(current_carparkNumber, carpark_Number)) {
			return true;
		}
		else {
			retrieve_CarparkCsv(carpark_Number);
			return true;
		}
	}
	
	public String get_Address(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_Address;
		}
		return null;
	}
	
	public double get_x_coord(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_x_coord;
		}
		return 0;
	}
	
	public double get_y_coord(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_y_coord;
		}
		return 0;
	}
	
	public String get_car_park_type(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_car_park_type;
		}
		return null;
	}
	
	public String get_type_of_parking_system(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_type_of_parking_system;
		}
		return null;
	}
	
	public String get_short_term_parking(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_short_term_parking;
		}
		return null;
	}
	
	public String get_free_parking(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_free_parking;
		}
		return null;
	}
	
	public boolean get_night_parking(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_night_parking;
		}
		return false;
	}
	
	public int get_car_park_decks(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_car_park_decks;
		}
		return 0;
	}
	
	public double get_gantry_height(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_gantry_height;
		}
		return 0;
	}
	
	public boolean get_car_park_basement(String carpark_Number) throws IOException {
		if(csvdata_Check(carpark_Number)) {
			return this.current_car_park_basement;
		}
		return false;
	}

}
