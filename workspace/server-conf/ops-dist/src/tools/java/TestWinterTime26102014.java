import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestWinterTime26102014 {

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();

		cal.set(2014, Calendar.OCTOBER, 26, 0, 0, 0);
		Date date = cal.getTime();
		System.out.println(padRight("Local time", 30) + "Instant (hours since 01.01.1970 GMT)");
		for (int i = 0; i < 5; ++i) {
			String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (Z)").format(date);
			System.out.println(padRight(formatted, 30) + date.getTime() / 1000 / 60 / 60);
			date.setTime(date.getTime() + 1000 * 60 * 60);
		}
	}

	private static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

}
