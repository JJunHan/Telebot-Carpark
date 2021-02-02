package telebot;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import java.io.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
/*uses the data from govsg api to get carpark availibity in real time.
 * used to get the lots information*/
public class Availability_controller {
	// Part 1 - API Data
    protected String[] array_totalLots;
    protected String[] array_lotType;
    protected String[] array_lotsAvailable;
    protected String[] array_carparkNumber;
    protected String[] array_updateDatetime;
    protected int number_of_carparks = 0;
    protected boolean data_Exist = false;
    protected StopWatch watch = new StopWatch();
    
	public void refresh_Data() throws IOException{
    	// Retrieve Big Data from Web
		OffsetDateTime dateTimeOri = OffsetDateTime.now();
		DateTimeFormatter formatObject = DateTimeFormatter.ISO_DATE_TIME;
		String dateTimeFormatted = formatObject.format(dateTimeOri); 
		String[] dateTimeArray = null;
		dateTimeArray = dateTimeFormatted.split(":|\\+");		
		String data_URL = "https://api.data.gov.sg/v1/transport/carpark-availability?date_time=" + 
				dateTimeArray[0] + "%3A" + dateTimeArray[1] + "%3A" + dateTimeArray[2] + "%2B" + dateTimeArray[3] + "%3A" + dateTimeArray[4];
		
        String big_data = Jsoup.connect(data_URL)
        		.ignoreContentType(true)
        		.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
                .referrer("http://www.google.com") 
        		.get()
        		.wholeText();
        //System.out.println("(Before)Length of Data: " + big_data.length());
        
        // Formatting Big Data
        big_data = big_data.substring(67);
        big_data = big_data.substring(0, big_data.length() - 4);
        //System.out.println("(After)Length of Data: " + big_data.length());
        big_data = big_data.replaceAll("\\[|\\]|\"|\\{|\\}", ""); //Remove [ ] " { }
        
        // Find total number of carparks and create arrays
        String findStr = "total_lots";
        int lastIndex = 0;
        while(lastIndex != -1){
            lastIndex = big_data.indexOf(findStr,lastIndex);
            if(lastIndex != -1){
            	number_of_carparks ++;
                lastIndex += findStr.length();
            }
        }
        System.out.println("Total number of carparks: " + number_of_carparks);
        big_data = big_data.replaceAll("carpark_info:", "");
        String[] array_bigData = big_data.split(",");
        //System.out.println("Target URL: " + data_URL);
        array_totalLots = new String[number_of_carparks+1];
        array_lotType = new String[number_of_carparks+1];
        array_lotsAvailable = new String[number_of_carparks+1];
        array_carparkNumber = new String[number_of_carparks+1];
        array_updateDatetime = new String[number_of_carparks+1];
        
        // Count total number of elements in Big Data
        int counter = 0;
        for (int i = 0; i < array_bigData.length; i ++)
            if (array_bigData[i] != null)
                counter ++;
        //System.out.println("Number of elements is: " + counter);
        
        // Insert Big Data into separate arrays
        int current_Index = -1;
        for(int i=0; i<counter-1; i++) {
        	if(StringUtils.contains(array_bigData[i], "total_lots")) {
        		current_Index += 1;
        		array_totalLots[current_Index] = array_bigData[i].replaceAll("total_lots:", "");
        	}
        	else if(StringUtils.contains(array_bigData[i], "lot_type")) {
        		array_lotType[current_Index] = array_bigData[i].replaceAll("lot_type:", "");
        	}
        	else if(StringUtils.contains(array_bigData[i], "lots_available")) {
        		array_lotsAvailable[current_Index] = array_bigData[i].replaceAll("lots_available:", "");
        	}
        	else if(StringUtils.contains(array_bigData[i], "carpark_number")) {
        		array_carparkNumber[current_Index] = array_bigData[i].replaceAll("carpark_number:", "");
        	}
        	else if(StringUtils.contains(array_bigData[i], "update_datetime")) {
        		array_updateDatetime[current_Index] = array_bigData[i].replaceAll("update_datetime:", "");
        	}
        }
        data_Exist = true;
        watch.start();
        System.out.println("out system ref");
	}
	
	public boolean bigdata_Check() throws IOException {
		if(data_Exist) {
			if(watch.getTime(TimeUnit.SECONDS) > 60) {
				System.out.println("Last update was more than a minute ago! Refreshing data now . . .");
				watch.reset();
				refresh_Data();
			}
			return true;
		}
		refresh_Data();
		return true;
	}
		
	public int get_TotalLots(String carpark_Number) throws IOException {
		int result = 0;
		if(bigdata_Check()) {
			for(int i=0; i<number_of_carparks; i++) {
				if(StringUtils.equals(array_carparkNumber[i], carpark_Number)){
					result = Integer.parseInt(array_totalLots[i]);
					return result;
				}
			}
		}
		return result;
	}
	
	public String get_LotType(String carpark_Number) throws IOException {
		String result = null;
		if(bigdata_Check()) {
			for(int i=0; i<number_of_carparks; i++) {
				if(StringUtils.equals(array_carparkNumber[i], carpark_Number)){
					result = array_lotType[i];
					return result;
				}
			}
		}
		return result;
	}
	
	public int get_lotsAvailable(String carpark_Number) throws IOException {
		int result = 0;
		if(bigdata_Check()) {
			for(int i=0; i<number_of_carparks; i++) {
				if(StringUtils.equals(array_carparkNumber[i], carpark_Number)){
					result = Integer.parseInt(array_lotsAvailable[i]);
					return result;
				}
			}
		}
		return result;
	}
	
	public String get_UpdateDateTime(String carpark_Number) throws IOException {
		String result = null;
		if(bigdata_Check()) {
			for(int i=0; i<number_of_carparks; i++) {
				if(StringUtils.equals(array_carparkNumber[i], carpark_Number)){
					result = array_updateDatetime[i];
					return result;
				}
			}
		}
		return result;
	}
}