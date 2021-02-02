package telebot;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

//import static java.time.temporal.ChronoUnit.SECONDS;
/*class used to calculate Distance between 2 points and get the price from a dd/hh range 
 * error checking included*/
public class Calculation_controller {
	
	private double parkingRate;
	private double distance; // distance = sqroot( (difference between x )^2 + (difference between y )^2 ) 
	
	private double getParking_rate(){ // set a fixed for now, can get from ura api
		return 0.5;
	}
	public double getDistance_fromlocation(double xuser , double yuser,double xcarpark,double ycarpark) {
		SVY21Coordinate xystore = new SVY21Coordinate();
		xystore = Conversion_controller.executecomputeSVY21(yuser,xuser); //lat then long, static method. 
		System.out.println("XY LAT LONG CONVERTED TO XY COORD"+xystore.getEasting()+">"+xystore.getNorthing());
		double xtemp = (xystore.getEasting() - xcarpark)*(xystore.getEasting() - xcarpark); //square it
		double ytemp = (xystore.getNorthing() - ycarpark)*(xystore.getNorthing() - ycarpark);
		return (Math.sqrt(xtemp+ytemp));

	}
	
	public String getprice(Update update){
		int date = 0;
		int hour = 0;
		
		String tempstring = update.getMessage().getText();
		tempstring = tempstring.replaceAll(",","/");
		String[] messagereceived = tempstring.split("/");
		date = Integer.parseInt(messagereceived[0]);
		hour = Integer.parseInt(messagereceived[1]);
		if(date > 31 || hour > 24) return "Invalid datetime format";
		System.out.println("1st hour: " + hour);
		System.out.println("1st date: " + date);
		
		date = Integer.parseInt(messagereceived[2]) - date;
		hour = Integer.parseInt(messagereceived[3])- hour;
		if(Integer.parseInt(messagereceived[2]) > 31 || Integer.parseInt(messagereceived[3]) > 24) return "Invalid datetime format";
		System.out.println("2nd hour: " + hour);
		System.out.println("2nd date: " + date);
		hour = hour + (date*24);
		
		double price = (hour * getParking_rate());
		if(price<=0)return "Invalid datetime format";
		return String.valueOf(price);

	}

}
