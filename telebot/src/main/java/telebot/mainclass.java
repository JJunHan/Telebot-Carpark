package telebot;
/*main class that creates the telebotapi object that controls the individual controllers*/
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class mainclass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        
        try {
        	telegramBotsApi.registerBot(new telebot_2006());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
	}

}
