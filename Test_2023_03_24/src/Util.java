import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

	public static String getDateTimeNowstr() {
		 // 현재 날짜 구하기
		LocalDateTime now = LocalDateTime.now();
 
        // 포맷 정의
        String formatter = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
 
        return formatter;
	}
	
}
