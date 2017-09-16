package cal.util;

import javax.swing.*;

import cal.datastructure.Schedule;

class DayBox
{
	public int dayDate; 
	public JPanel dayPanel;
	public JButton dayButton;
	
	public Schedule[] schedules;
	public JButton[] schButton;
	
	DayBox()
	{
		dayDate=0;
		dayPanel = new JPanel();
		dayButton = new JButton();
		
		//allocate later as many as schedules this day has
		schedules =null;
		schButton = null;
	}
	
		
}