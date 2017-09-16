package cal.datastructure;

import java.util.StringTokenizer;

import cal.util.Frame;
import cal.util.LogWriter;

import java.io.*;

//schedule data for a month
public class ScheduleManager 
{	
	//All schedules
	public static Schedule[] allMonthSchedules;
	
	static 
	{
		updateAllSchedules();
	}
		
	public static void updateAllSchedules()
	{
		BufferedReader inputSchedule; 
		Schedule[] result;
		
		//read schedules File
		try 
		{
			//read number of line == number of schedule
			inputSchedule = new BufferedReader(new FileReader("schedules.scheduledata"));
			
			inputSchedule.read();
			
			int lineCount=0;
			while (inputSchedule.readLine() != null)
				lineCount++;
			
			inputSchedule.close();
			
			
			//allocate allSchedule
			int numAllSchedule = lineCount;  
			
			result = new Schedule[numAllSchedule];
						
			
			//TODO - PEFORMANCE find better way to rewind
			//rewind & save data in allocated allSchedule
			inputSchedule = new BufferedReader(new FileReader("schedules.scheduledata"));
			
			String oneLine;
			int year,month,date;
			String subject, text;
			
			for(int i=0; i<numAllSchedule; i++)
			{
				oneLine = inputSchedule.readLine();
				StringTokenizer sTok = new StringTokenizer(oneLine);
				
				
				year = Integer.parseInt(sTok.nextToken("/"));				
				month =  Integer.parseInt(sTok.nextToken("/"));				
				date =  Integer.parseInt(sTok.nextToken("/"));				
				subject = sTok.nextToken("/");
				text = sTok.nextToken("/");
				
				result[i] = new Schedule(year, month, date, subject, text);
				
			}
			
			inputSchedule.close();
			
			allMonthSchedules = result;
		}
		catch (Exception Ex)
		{
			allMonthSchedules =null;
			LogWriter.LogEx(Ex);
		}
		
	}

	//get Schedules for requested month(arg)
	public static Schedule[] getMonthSchedule(int year, int month)
	{
		int numMonthSchedule = getNumMonthSchedule(year,month);
		
		if (numMonthSchedule == 0)
			return null;
		
		Schedule[] monthSchedule = new Schedule[numMonthSchedule];
		
		for(int i=0, idx=0; i<allMonthSchedules.length; i++)
			if(allMonthSchedules[i].year == year && allMonthSchedules[i].month == month)
				monthSchedule[idx++] = allMonthSchedules[i];
		
		return monthSchedule;
	}
	//get Schedules for requested date(arg)
	public static Schedule[] getDaySchedule(int year, int month, int date)
	{	
		int numDaySchedule = getNumDaySchedule(year,month,date);
		
		if (numDaySchedule == 0)
			return null;
		
		Schedule[] daySchedule = new Schedule[numDaySchedule];
	
		for(int i=0,idx=0; i<allMonthSchedules.length; i++)
			if(allMonthSchedules[i].year == year && allMonthSchedules[i].month == month
					&& allMonthSchedules[i].date == date)
				daySchedule[idx++] = allMonthSchedules[i];
	
		return daySchedule;
		
	}
	//get number Schedules for requested month(arg)
	public static int getNumMonthSchedule(int year, int month)
	{		
		int count=0; 
		
		for(int i=0; i<allMonthSchedules.length; i++)
			if(allMonthSchedules[i].year == year && allMonthSchedules[i].month == month)
				count++;
		return count;
	}
	//get number Schedules for requested date(arg)
	public static int getNumDaySchedule(int year, int month, int date)
	{
		int count=0; 
		
		for(int i=0; i<allMonthSchedules.length; i++)
			if(allMonthSchedules[i].year == year && allMonthSchedules[i].month == month
				&&allMonthSchedules[i].date == date)
				count++;
		
		return count;
	}

	
	public static int getScheduleNum(Schedule sch)
	{
		for(int i =0; i<allMonthSchedules.length; i++)
			if(sch == allMonthSchedules[i])
				return i;			
		return -1;
	}
	
	
	public static void addSchedule(Schedule newSchedule)
	{
		//Add to file
		try
		{
			FileWriter fw = new FileWriter("schedules.scheduledata",true);
			
			fw.append(newSchedule.year
				+"/"+ newSchedule.month
				+"/"+ newSchedule.date
				+"/"+ newSchedule.subject
				+"/"+ newSchedule.text + "\r\n");
			
			fw.close();
			
			updateAllSchedules();
			Frame.Update();
		} 
		catch (Exception e)
		{
			LogWriter.LogEx(e);
		}
	}
	public static void removeSchedule(Schedule originalSchedule)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("schedules.scheduledata"));
			
			String interString = "";
			
			for(int i=0; i<allMonthSchedules.length; i++)
				//if i is not the one want to remove
				if( allMonthSchedules[i] != originalSchedule)
					interString += reader.readLine() + "\r\n";
				else
					reader.readLine();
			
			reader.close();
			
			
			FileWriter writer = new FileWriter("schedules.scheduledata");
			
			writer.write(interString);
				
			writer.close();
			
			updateAllSchedules();
			Frame.Update();
		}
		
		catch (Exception e)
		{
			LogWriter.LogEx(e);
		}
	} 
	public static void editSchedule(Schedule originalSchedule, Schedule newSchedule)
	{
		removeSchedule(originalSchedule);
		addSchedule(newSchedule);
	}
}