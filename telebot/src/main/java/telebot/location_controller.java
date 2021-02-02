package telebot;

import java.util.ArrayList;
import java.util.List;

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

/*location controller to manage the user's location via the button
 * the details are stored in this controller's private variables and retrieved using get methods*/
public class location_controller extends telebot_2006{
	private double coord_lat =0;
	private double coord_long =0;

	public double get_x_long() {
		return coord_long;
	}
	public double get_y_lat() {
		return coord_lat;
	}
	
/*mirror message method can be used for future dev*/
/*
    public void returnmessage(Update update) //send message back to console
    {
    	System.out.println(update.getMessage().getText());
    	
    	//copy message sent by user//
    	
    	if (update.hasMessage() && update.getMessage().hasText()) {
    	SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(update.getMessage().getChatId()).setText(update.getMessage().getText());
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        	}
    	}
    	
    }
*/    
	/*method to constantly check if message is of type location, if it is, will prompt user to enter decision fork*/
    public void sendlocation(Update update) 
    {
    	
        String text = update.getMessage().getText(); 
        Message message = new Message();
        message = update.getMessage(); //get user's input details here
        
        if(message.hasLocation() == true)
        {
        	Location location = message.getLocation();
	    	SendMessage latlong_out = new SendMessage();
	    	latlong_out.setText("Your lat: "+ location.getLatitude() + "\nYour long: " + location.getLongitude() + 
	    			"\nClick to /show_suggested_cp to display Top 5 Nearest Carparks & Availability"
	    			+ "\nOR"
	    			+ "\nClick /address to enter search for Carparks near your destination");
	    	latlong_out.setChatId(update.getMessage().getChatId());
	    	coord_lat = location.getLatitude();
    		coord_long = location.getLongitude();
    		System.out.println("User's Lat: " + coord_lat + "User's Long: "+ coord_long);
	    	try {
	            execute(latlong_out); // Call method to send the message
	         } catch (TelegramApiException e) {
	             e.printStackTrace();
	         }
        }
       
        /*creats a button in a replykeyboardmarkup for the user to click and send location
         * button is configured to retrieve location*/
        if(text.equals("/send_location")) //if user types location
        	{
        	//initialise panel objs
	        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(); //new markup
	        KeyboardButton keyboardbutton1 = new KeyboardButton(); //new button1
	        List<KeyboardRow> keyboardrowlist = new ArrayList<>(); //new rowlist
	        KeyboardRow keyboardrow1 = new KeyboardRow(); //new row
	        markup.setResizeKeyboard(true); //fit user screen
	        markup.setOneTimeKeyboard(true); //panel goes away after using
	        
	        //initialise showkeyb objs
	        
	        SendMessage showkeyb = new SendMessage(); // Create a SendMessage object with mandatory fields
	        showkeyb.setChatId(update.getMessage().getChatId());
	        showkeyb.setParseMode(ParseMode.MARKDOWN);
	        showkeyb.setText("Click on the button below to send your current location");
        
	        //configure row, put in button1
	        keyboardrow1.add(keyboardbutton1); // add button into row
	        keyboardrowlist.add(keyboardrow1); //add row into keyboard
	        
	        //configure button
	        keyboardbutton1.setText("Send Location");
	        keyboardbutton1.setRequestLocation(true);
	    	
	        //setup markup
	        markup.setKeyboard(keyboardrowlist);
	    	showkeyb.setReplyMarkup(markup);
	    	
	    	try {
	            execute(showkeyb); // Call method to send the message
	            
	        	} catch (TelegramApiException e) {
	            e.printStackTrace();
	        	}
        	}
        
        /*help and start commands are set here*/
        if(text.equals("/help") || text.equals("/start")) //if user types help
    	{
        	SendMessage help_out = new SendMessage();
        	help_out.setText("Welcome to Group1's telebot!\n" + 
        			"To begin, type /send_location!");
        	help_out.setChatId(update.getMessage().getChatId());
        	try {
	            execute(help_out); // Call method to send the message
	         } catch (TelegramApiException e) {
	             e.printStackTrace();
	         }
        	
    	}
    }
}
