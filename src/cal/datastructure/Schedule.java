package cal.datastructure;

public class Schedule 
{
	public int year;
	public int month;
	public int date;
	public String subject;
	public String text;
	
	Schedule()
	{
		year = month = date= 0;
		subject = null;
		text = null;
	}
	public Schedule(int year, int month, int date, String subject, String text)
	{
		setSchedule(year, month, date, subject, text);
	}
	
	public void setSchedule(int year, int month, int date, String subject, String text)
	{
		this.year = year;
		this.month = month;
		this.date = date;
		this.subject = subject;
		this.text = text;
	}
	
	public void setSchedule(Schedule newSchedule)
	{
		this.year = newSchedule.year;
		this.month = newSchedule.month;
		this.date = newSchedule.date;
		this.subject = newSchedule.subject;
		this.text = newSchedule.text;
	}
		
}
