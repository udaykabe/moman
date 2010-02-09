package net.deuce.moman.command.account;

import java.util.Calendar;
import java.util.Date;


public class FullImport extends Import {
	
	public static final String ID = "net.deuce.moman.command.account.fullImport";

	@Override
	protected Date getLastDownloadedDate() {
		Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.set(Calendar.HOUR_OF_DAY, 17);
        return c.getTime();
	}

}
