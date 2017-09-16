package cal.datastructure;

import java.util.Calendar;

public class CalData
{
	final static public String days[] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	
	public int year;
	public int month;					//1~12
	public int daysInMonth;			
	public int startDay;				//1~7 -> days
	
	public int selectedYear;
	public int selectedMonth;
	public int selectedDay;
	
	public CalData(int year, int month)
	{
		Calendar beginOfMonth = Calendar.getInstance();
		beginOfMonth.set(year, month-1,1);
		
		this.year = year;
		this.month = month;
		this.daysInMonth = beginOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.startDay = beginOfMonth.get(Calendar.DAY_OF_WEEK);
		this.selectedYear = 0;
		this.selectedMonth = 0;
		this.selectedDay = 0;
	}
	public void UpdateCalData(int year,int month)
	{
		Calendar beginOfMonth = Calendar.getInstance();
		beginOfMonth.set(year, month-1,1);
		
		this.year = year;
		this.month = month;
		this.daysInMonth = beginOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.startDay = beginOfMonth.get(Calendar.DAY_OF_WEEK);
	}
	
	
}
