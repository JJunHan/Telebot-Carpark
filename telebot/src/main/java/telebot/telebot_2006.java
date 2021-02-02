package telebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class telebot_2006 extends TelegramLongPollingBot  {
	private static location_controller locate = new location_controller();
	private static Calculation_controller cal =  new Calculation_controller();
	private static Carpark_controller nearestcarpark = new Carpark_controller();
	private static Availability_controller refreshObj = new Availability_controller();
	public static Display_controller displayObj = new Display_controller();
	private static boolean tf = true;
	private static boolean address = false;
	private static boolean getpricein = false;
	private static boolean show = false;
	private static String carparknum = null;

    public void onUpdateReceived(Update update)  { //when bot gets something
    	/* common output message obj */
    	SendMessage output_msg = new SendMessage();
    	output_msg.setChatId(update.getMessage().getChatId());
    	
    	/*show received message on on console*/
    	System.out.println(update.getMessage().getText());
    	
    	/*check if /send_location is typed & if location is given, set and lat long into usercontroller*/
        locate.sendlocation(update);

        /* call for data and read csv file once only*/
        try {
        	while(tf == true) { //read once only.
        		nearestcarpark.readcsv(); //read csv file
        		refreshObj.refresh_Data(); //call for data
        		tf = false;
        	}
		} catch (IOException e1) {
			e1.printStackTrace();}

        /* since upon every use input, on update is called, make sure that lat long is filled up before executing anything.
         * show checker is to display the specific carpark information only when the boolean show is activated after address
         * or suggested cp is called and completed successfully*/
        if(((locate.get_x_long() != 0 && locate.get_y_lat() != 0))) { 
        	if(show == true) {
        		if((update.getMessage().getText().equals("/" + nearestcarpark.get_nearestlist()[0]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestlist()[1]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestlist()[2]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestlist()[3]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestlist()[4]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestaddress()[0][1]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestaddress()[1][1]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestaddress()[2][1]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestaddress()[3][1]))
        			|| (update.getMessage().getText().equals("/" + nearestcarpark.get_nearestaddress()[4][1]))) {
            		System.out.println("selected a carpark");
            		String carpark_Number = update.getMessage().getText().substring(1); //remove / from input
            		try {
    					displayObj.retrieve_CarparkCsv(carpark_Number); //fetch carpark data
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
            		try {
            			output_msg.setText("Information about Carpark number " + carpark_Number + ": "
                			+ "\nAddress: " + displayObj.get_Address(carpark_Number)
                			+ "\nx_coord: " + displayObj.get_x_coord(carpark_Number)
                			+ "\ny_coord: " + displayObj.get_y_coord(carpark_Number)
                			+ "\nCar_park_type: " + displayObj.get_car_park_type(carpark_Number)
                			+ "\nType_of_parking_system: " + displayObj.get_type_of_parking_system(carpark_Number)
                			+ "\nShort_term_parking: " + displayObj.get_short_term_parking(carpark_Number)
                			+ "\nFree_parking: " + displayObj.get_free_parking(carpark_Number)
                			+ "\nNight_parking: " + displayObj.get_night_parking(carpark_Number)
                			+ "\nNo_of_Car_park_decks: " + displayObj.get_car_park_decks(carpark_Number)
                			+ "\nGantry_height: " + displayObj.get_gantry_height(carpark_Number)
                			+ "\ncar_park_basement: " + displayObj.get_car_park_basement(carpark_Number)
                			);
            			output_msg.setChatId(update.getMessage().getChatId());
            		try {
    					execute(output_msg);
    				} catch (TelegramApiException e) {
    					e.printStackTrace();
    				}
            		
            		/*passes the lat long of current location and xy coord of destination carpark
            		 * getdistance will convert latlong into xy and apply xy plane formula to find distance 
            		 * this distance is direct measurement (possibly diagonal). The map function calculates the
            		 * best and shortest route to take depending on the mode of transport.*/
            		double distance = Math.round((cal.getDistance_fromlocation(locate.get_x_long(), 
    						locate.get_y_lat(),
    						displayObj.get_x_coord(carpark_Number), 
    						displayObj.get_y_coord(carpark_Number)))
        					*10000)/10000; //round off then /1000 to convert meter to km
            		output_msg.setText("Distance from your current location to your destination is: "
        					+ distance/1000  + "km");
        			System.out.println("Distance from your current location to your destination is: "
        					+ distance/1000  + "km");

            		try {
        	            execute(output_msg); // Call method to send the message
        	        } catch (TelegramApiException e) {
        	             e.printStackTrace();
        	        }		
            		
            		/*used to output a map with the destination location. But map only take in Lat and long so conversion is done first*/
            		SendLocation test = new SendLocation();
            		LatLonCoordinate xystore = new LatLonCoordinate();
            		xystore = Conversion_controller.executecomputeLatLon(displayObj.get_y_coord(carpark_Number),displayObj.get_x_coord(carpark_Number)); //convert tolat long
        			test.setChatId(update.getMessage().getChatId());
        			test.setLongitude((float)(xystore.getLongitude()));
        			test.setLatitude((float)(xystore.getLatitude()));
        			try {
    					execute(test);
    					carparknum = carpark_Number;
    				} catch (TelegramApiException e1) {
    					e1.printStackTrace();
    				}
        			
        			output_msg.setText("Click to /get_price to get rough estimate of parking fees from start date till end date");
        			System.out.println("Click to /get_price to get rough estimate of parking fees from start date till end date");
        			try {
    					execute(output_msg);
    				} catch (TelegramApiException e) {
    					e.printStackTrace();
    				}
            		
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
            		show = false;
            	} //end if(show)
            	else {
            		output_msg.setText("Please choose a carpark number from the list mentioned above!");
            		try {
    					execute(output_msg);
    				} catch (TelegramApiException e) {
    					e.printStackTrace();
    				}
            	}
            	//show = false;
        	}
            	

        	if(address == true) {
        		String input_string = update.getMessage().getText();
        		if(nearestcarpark.find_closest_address(input_string.toUpperCase())){ //all in upper case
    			//if true means found.
    			//if false means none found
    			System.out.println("Enter string input area: " + input_string.toUpperCase()); 
    			try {
    				if(!nearestcarpark.get_nearestaddress()[1][0].isEmpty()) { //if not empty (no direct name is found)
    					
    					output_msg.setText("Did you mean: "
    				+ "\n1) " +nearestcarpark.get_nearestaddress()[0][0] + "\n/" +  nearestcarpark.get_nearestaddress()[0][1]
    						+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[0][1]) 
    						+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[0][1])
    				+ "\n2) " +nearestcarpark.get_nearestaddress()[1][0] + "\n/" +  nearestcarpark.get_nearestaddress()[1][1]
    						+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[1][1]) 
    						+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[1][1])
    				+ "\n3) " +nearestcarpark.get_nearestaddress()[2][0] + "\n/" +  nearestcarpark.get_nearestaddress()[2][1]
    						+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[2][1]) 
    						+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[2][1])
    				+ "\n4) " +nearestcarpark.get_nearestaddress()[3][0] + "\n/" +  nearestcarpark.get_nearestaddress()[3][1]
    						+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[3][1]) 
    						+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[3][1])
    				+ "\n5) " +nearestcarpark.get_nearestaddress()[4][0] + "\n/" +  nearestcarpark.get_nearestaddress()[4][1]
    						+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[4][1]) 
    						+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[4][1]));
    			}
    				else { 
    					output_msg.setText("Did you mean: "
    		    				+ "\n1) " +nearestcarpark.get_nearestaddress()[0][0] + "\n/" +  nearestcarpark.get_nearestaddress()[0][1]
    		    				+"\nTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestaddress()[0][1])
    		    				+"\nTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestaddress()[0][1])); 
    				}
    		}
    			catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		try {
				execute(output_msg);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
    		address= false;
    		show = true;
    		}
    		else {
    			try {
    				output_msg.setText("Address not found, please enter another keyword");
					execute(output_msg);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
    		}
        }
    		
        	/*since suggested cps need not wait for user input, we can execute it immediate upon receiving user's command
        	 * passes the current lat long into the carpark controller to solve for the nearest carparks by distance*/
        	if(update.getMessage().getText().equals("/show_suggested_cp")) {
        		System.out.println("hello im in locate");
        		nearestcarpark.find_nearest(locate.get_x_long(), locate.get_y_lat()); //calc nearest and store in array
        		System.out.println("end");
        	for(int i = 0; i<5; i++) {
        	System.out.println("nearest carpark"+nearestcarpark.get_nearestlist()[i]);
        	}  	
            try {
            	output_msg.setText("Top 5 nearest carparks to you are: \n"
						+ "1: "   + "/"+nearestcarpark.get_nearestlist()[0] +
						"\tTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestlist()[0]) +
						"\tTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestlist()[0])
						+ "\n2: " + "/"+nearestcarpark.get_nearestlist()[1] + 
						"\tTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestlist()[1]) +
						"\tTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestlist()[1])
						+ "\n3: " + "/"+nearestcarpark.get_nearestlist()[2] + 
						"\tTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestlist()[2]) +
						"\tTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestlist()[2])
						+ "\n4: " + "/"+nearestcarpark.get_nearestlist()[3] + 
						"\tTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestlist()[3]) +
						"\tTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestlist()[3])
						+ "\n5: " + "/"+nearestcarpark.get_nearestlist()[4] + 
						"\tTotal lots: " + refreshObj.get_TotalLots(nearestcarpark.get_nearestlist()[4]) +
						"\tTotal available lots: " + refreshObj.get_lotsAvailable(nearestcarpark.get_nearestlist()[4])
							);		
            } 	
            catch (IOException e1) {
            	e1.printStackTrace();
            }
            try {
	           execute(output_msg);
	        } catch (TelegramApiException e) {
	            e.printStackTrace();
	        }
            show = true;
        	}	
        	
        	/* respond to user message if address is sent. set address = true to enter another loop to calculate nearest and display*/
        	if(update.getMessage().getText().equals("/address")) {
        		output_msg.setText("Please enter the address of your destination: ");        		
        		try {
					execute(output_msg);
					address = true;
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
        	}
        }//end main if
        /*get price checker goes on here. only calls getprice from calculation controll if user string matches a certain format.
         *more checking for the test cases are done within the controller */
            if(getpricein){
            	if(Pattern.compile("\\d\\d\\/\\d\\d\\,\\d\\d\\/\\d\\d").matcher(update.getMessage().getText().toString()).matches() &&  
            			(update.getMessage().getText().toString().length()==11) ) {
            	    	if( cal.getprice(update) == "Invalid datetime format") {
            	    		output_msg.setText("Invalid dateframe, please enter datetime again");
            			}
            	    	else { output_msg.setText("Calculated price = $"+cal.getprice(update));
            				getpricein=false;
            			}
            		}
            	else {
            		output_msg.setText("Please enter proper datetime");
            		}
            		try {
						execute(output_msg);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
            	
        	}   
            /*this is placed behind the main logic to call the calc controller because we have to wait for a user input
             * similar to that of the above logic*/
            if(update.getMessage().getText().equals("/get_price")) {
        		if(carparknum == null) {
        			output_msg.setText("Please select a carpark first!");
        			output_msg.setChatId(update.getMessage().getChatId());
        			try {
						execute(output_msg);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
        		}
        		else { 
        			output_msg.setText("Please input your start datetime and end datetime respectively, separated by / \nFor example dd/hh,dd/hh");
	            try {
	    			execute(output_msg);
	    			getpricein = true;
	    		} catch (TelegramApiException e) {
	    			e.printStackTrace();
	    		}
            }
        }
            
        /*gives the typing... message when the user sends a message.*/
        if (update.hasMessage() && update.getMessage().hasText()) {
        	SendChatAction chataction = new SendChatAction(); 
        	chataction.setChatId(update.getMessage().getChatId());
        	chataction.setAction(ActionType.TYPING);
        	try {
        		execute(chataction);
        	} catch (TelegramApiException e) {
        		e.printStackTrace();
        	}
        }

    } //end main update method

    @Override
    public String getBotUsername() {
        return "Group12005";
    }

    @Override
    public String getBotToken() {
        return "NIL";
    }
}
