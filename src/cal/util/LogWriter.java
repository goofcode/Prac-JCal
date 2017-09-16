package cal.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogWriter
{
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd aa hh:mm:ss");
	static String time;

	static FileWriter fw;

	
	static void updateTime()
	{
		time = dateFormat.format(Calendar.getInstance().getTime());
	}
	
	static public void LogEx(Exception ex)
	{		
		try
		{
			fw = new FileWriter("Log.log", true);
	
			updateTime();
			fw.append(time + "    " + ex.getMessage() + "\r\n");

		}
		catch (Exception e)
		{
			LogEx(e);
		}
	
		finally
		{
			try
			{
				fw.close();
			} 
			catch (Exception e)
			{
				LogEx(e);
			}
		}
	}

	static public void LogStartProg()
	{
		try
		{
			fw = new FileWriter("Log.log", true);
			
			updateTime();
			time = dateFormat.format(Calendar.getInstance().getTime());

			fw.append("**start Running**" + "    " + time + "\r\n");
			} 
		catch (IOException ex)
		{
			LogWriter.LogEx(ex);
		}
		finally
		{
			try
			{
				fw.close();
			}
			catch (IOException e)
			{
				LogEx(e);
			}
				
		}
	}
	static public void LogEndProg()
	{
		try
		{
			fw = new FileWriter("Log.log", true);
			
			updateTime();
			time = dateFormat.format(Calendar.getInstance().getTime());

			fw.append("**end Running**" + "    " + time + "\r\n" + "\r\n");
			fw.close();
		}
		catch (IOException ex)
		{
			LogWriter.LogEx(ex);
		}

	}
}
