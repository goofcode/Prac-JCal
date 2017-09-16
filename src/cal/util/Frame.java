package cal.util;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.util.Calendar;
import cal.datastructure.*;


public class Frame
{
	// GUI Components
	static JFrame frame = new JFrame("JCALENDAR");

	static JPanel controlPanel = new JPanel();
	static JButton monthButton = new JButton();
	static JButton yearButton = new JButton();
	static JButton todayButton = new JButton("Today");
	static JButton leftButton = new JButton("<");
	static JButton rightButton = new JButton(">");

	static JButton addButton = new JButton("+");

	static JPanel daysPanel = new JPanel();
	static JPanel showPanel = new JPanel();

	static DayBox[][] dayBox = new DayBox[6][];

	static JPanel timePanel = new JPanel();
	static JLabel nowTime = new JLabel();

	// calData for a month - initialized by now
	public static CalData calData = new CalData(Calendar.getInstance().get(Calendar.YEAR),
			Calendar.getInstance().get(Calendar.MONTH) + 1);

	static
	{
		frame.setPreferredSize(new Dimension(1100, 700));
		frame.setLocation(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// frame.setResizable(false);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.addWindowListener(
				new java.awt.event.WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent wEvent)
					{
						LogWriter.LogEndProg();
					}					
				});
		
		
		// allocate components
		SetControlPanel();
		SetShowPanel();
		SetTimePanel();

		// initialize components as now
		Update();

		// Add Panels
		frame.getContentPane().add(controlPanel);
		frame.getContentPane().add(Box.createVerticalStrut(30));
		frame.getContentPane().add(daysPanel);
		frame.getContentPane().add(showPanel);
		frame.getContentPane().add(Box.createVerticalGlue());
		frame.getContentPane().add(timePanel);


	}
	
	public static void StartWindow()
	{
		frame.pack();
		frame.setVisible(true);

	}
	
	private static void SetControlPanel()
	{
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

		ChangeButtonColor(monthButton, Color.blue, Color.white);
		controlPanel.add(monthButton);

		controlPanel.add(Box.createHorizontalStrut(10));

		ChangeButtonColor(yearButton, Color.blue, Color.white);
		controlPanel.add(yearButton);

		controlPanel.add(Box.createHorizontalGlue());

		controlPanel.add(addButton);

		controlPanel.add(Box.createHorizontalStrut(20));

		// ChangeButtonColor(todayButton, Color.blue, Color.white);
		controlPanel.add(todayButton);

		controlPanel.add(Box.createHorizontalStrut(20));
		controlPanel.add(leftButton);
		controlPanel.add(rightButton);

		addButton.addActionListener(new AddButtonListener());
		todayButton.addActionListener(new TodayButtonListener());
		leftButton.addActionListener(new LeftButtonListener());
		rightButton.addActionListener(new RightButtonListener());

	}
	private static void SetShowPanel()
	{
		daysPanel.setPreferredSize(new Dimension(950, 10));
		daysPanel.setLayout(new GridLayout(1, 7));

		// Mon, Tue, ... buttons
		for (int i = 0; i < 7; i++)
		{
			JButton daybutton = new JButton(CalData.days[i]);
			ChangeButtonColor(daybutton, Color.blue, Color.white);
			daybutton.setBorder(new BevelBorder(BevelBorder.LOWERED));
			daysPanel.add(daybutton);
		}

		showPanel.setPreferredSize(new Dimension(950, 520));
		showPanel.setLayout(new GridLayout(6, 7));

		// allocate dayBox es
		for (int i = 0; i < 6; i++)
		{
			dayBox[i] = new DayBox[7];

			for (int j = 0; j < 7; j++)
			{
				dayBox[i][j] = new DayBox();

				dayBox[i][j].dayPanel.setLayout(new GridLayout(6, 1));
				dayBox[i][j].dayPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

				dayBox[i][j].dayButton.setOpaque(true);
				dayBox[i][j].dayButton.addActionListener(new DayButtonListener());

				showPanel.add(dayBox[i][j].dayPanel);

				// add dayButton & scheduleButton to dayPanel later in
				// UpdateTo()
			}
		}

	}
	private static void SetTimePanel()
	{
		timePanel.setPreferredSize(new Dimension(950, 20));
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
		timePanel.add(Box.createHorizontalGlue());
		timePanel.add(nowTime);
		
		(new UpdateTimeThread()).start();
	}

	public static void Update()
	{

		////////////////////////// year, month button//////////////////////////

		// set year&month Button text
		yearButton.setText(String.valueOf(calData.year));
		monthButton.setText(String.valueOf(calData.month));

		////////////////////////// year, month button
		////////////////////////// end//////////////////////////

		////////////////////////// day button, panel,
		////////////////////////// schedule..//////////////////////////

		// reset dayBox text
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++)
			{
				dayBox[i][j].dayDate = 0;
				dayBox[i][j].dayPanel.remove(dayBox[i][j].dayButton);

				if (dayBox[i][j].schedules != null)
				{
					for (int k = 0; k < dayBox[i][j].schedules.length; k++)
						dayBox[i][j].dayPanel.remove(dayBox[i][j].schButton[k]);
					dayBox[i][j].schButton = null;
					dayBox[i][j].schedules = null;
				}

				dayBox[i][j].dayPanel.revalidate();

				dayBox[i][j].dayButton.setText(null);

				// normal day's color
				ChangeButtonColor(dayBox[i][j].dayButton, Color.black, Color.white);

			}

		// set&apply dayPanel, dayButton
		// apply calcontent
		loop: for (int i = 0, date = 0; i < 6; i++)
			for (int j = 0; j < 7; j++)
			{
				// begin of month
				if (i == 0 && j == 0 && date == 0)
				{
					// (to prevent Sunday start from 2nd week)
					date = 1;
					j = calData.startDay - 2;
				}
				// end of month
				else if (date == calData.daysInMonth + 1)
					break loop;

				// middle of month
				else
				{
					// set Date of DayBox
					dayBox[i][j].dayDate = date;

					// set dayButton
					dayBox[i][j].dayButton.setText(String.valueOf(date));
					ChangeButtonColor(dayBox[i][j].dayButton, Color.black, Color.white);

					dayBox[i][j].dayPanel.add(dayBox[i][j].dayButton);

					date++;
				}
			}

		// set schedules
		// if month has schedule
		if (ScheduleManager.getNumMonthSchedule(calData.year, calData.month) != 0)
		{
			// start search
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 7; j++)
				{
					int dayNumSchedule = ScheduleManager.getNumDaySchedule(calData.year, calData.month,
							dayBox[i][j].dayDate);
					// if day has schedule
					if (dayNumSchedule != 0)
					{
						dayBox[i][j].schedules = ScheduleManager.getDaySchedule(calData.year, calData.month,
								dayBox[i][j].dayDate);

						dayBox[i][j].schButton = new JButton[dayNumSchedule];

						for (int k = 0; k < dayNumSchedule; k++)
						{
							dayBox[i][j].schButton[k] = new JButton();
							dayBox[i][j].schButton[k].setText(dayBox[i][j].schedules[k].subject);
							dayBox[i][j].schButton[k].addActionListener(new ScheduleButtonListener());
							dayBox[i][j].dayPanel.add(dayBox[i][j].schButton[k]);
						}
					}
				}
		}

		////////////////////////// day button, panel, schedule..
		////////////////////////// end//////////////////////////

		////////////////////////// today, selected day//////////////////////////

		// apply today & selected day

		// get this year & month & today
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		int thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int today = Calendar.getInstance().get(Calendar.DATE);

		// if requested data is including today == requested month is this month
		if (calData.year == thisYear && calData.month == thisMonth)
		{
			todayloop: for (int i = 0; i < 6; i++)
				for (int j = 0; j < 7; j++)
					if (dayBox[i][j].dayDate == today)
					{
						// today's color
						ChangeButtonColor(dayBox[i][j].dayButton, dayBox[i][j].dayButton.getForeground(),
								Color.lightGray);
						break todayloop;
					}
		}

		// if requested data is including selected date
		if (calData.year == calData.selectedYear && calData.month == calData.selectedMonth)
			todayloop: for (int i = 0; i < 6; i++)
				for (int j = 0; j < 7; j++)
					if (dayBox[i][j].dayDate == calData.selectedDay)
					{
						// today's color
						ChangeButtonColor(dayBox[i][j].dayButton, dayBox[i][j].dayButton.getForeground(), Color.cyan);
						break todayloop;
					}

		////////////////////////// today, selected day
		////////////////////////// end//////////////////////////

		frame.repaint();

	}
	public static void UpdateTo(int year, int month)
	{
		// update calData
		calData.UpdateCalData(year, month);

		Update();
	}

	public static void ChangeButtonColor(JButton button, Color textColor, Color bgColor)
	{
		button.setForeground(textColor);
		button.setBackground(bgColor);
		button.setOpaque(true);
	}
}


/************ Action Listeners ***************/
class AddButtonListener implements ActionListener
{

	JFrame addFrame = new JFrame("Detail");

	JPanel detailPanel = new JPanel();
	JPanel datePanel = new JPanel();
	JLabel yearLabel = new JLabel("Year:");
	JLabel monthLabel = new JLabel("Month:");
	JLabel dateLabel = new JLabel("Date:");
	JLabel subjectLabel = new JLabel("Title:");
	JLabel textLabel = new JLabel("Detail:");

	JTextField year = new JTextField();
	JTextField month = new JTextField();
	JTextField date = new JTextField();
	JTextField subject = new JTextField();
	JTextField text = new JTextField();

	JPanel confirmPanel = new JPanel();
	JButton confirmButton = new JButton("Confirm");
	JButton cancelButton = new JButton("Cancel");

	AddButtonListener()
	{
		// frame settings
		addFrame.setPreferredSize(new Dimension(300, 200));
		addFrame.setLocation(450, 300);

		addFrame.getContentPane().setLayout(new BoxLayout(addFrame.getContentPane(), BoxLayout.Y_AXIS));

		// datePanel settings
		yearLabel.setPreferredSize(new Dimension(50, 20));
		monthLabel.setPreferredSize(new Dimension(50, 20));
		dateLabel.setPreferredSize(new Dimension(50, 20));
		year.setPreferredSize(new Dimension(50, 20));
		month.setPreferredSize(new Dimension(50, 20));
		date.setPreferredSize(new Dimension(50, 20));

		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));

		datePanel.add(yearLabel);
		datePanel.add(year);
		datePanel.add(monthLabel);
		datePanel.add(month);
		datePanel.add(dateLabel);
		datePanel.add(date);

		// label in detailPanel settings
		subjectLabel.setSize(new Dimension(30, 10));
		textLabel.setSize(new Dimension(30, 10));
		subject.setSize(new Dimension(30, 10));
		text.setSize(new Dimension(30, 10));

		yearLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		monthLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		dateLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		subjectLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		textLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		year.setBorder(new BevelBorder(BevelBorder.LOWERED));
		month.setBorder(new BevelBorder(BevelBorder.LOWERED));
		date.setBorder(new BevelBorder(BevelBorder.LOWERED));
		subject.setBorder(new BevelBorder(BevelBorder.LOWERED));
		text.setBorder(new BevelBorder(BevelBorder.LOWERED));

		// detailPanel settings
		detailPanel.setLayout(new GridLayout(3, 2));

		detailPanel.add(subjectLabel);
		detailPanel.add(subject);
		detailPanel.add(textLabel);
		detailPanel.add(text);

		// confirmButton, Panel settings
		confirmButton.addActionListener(new ConfirmButtonListener());
		cancelButton.addActionListener(new CancelButtonListener());
		confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
		confirmPanel.add(Box.createHorizontalGlue());
		confirmPanel.add(confirmButton);
		confirmPanel.add(cancelButton);

		// make plusFrame
		addFrame.add(datePanel);
		addFrame.add(detailPanel);
		addFrame.add(confirmPanel);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (Frame.calData.selectedDay != 0)
		{
			year.setText(String.valueOf(Frame.calData.selectedYear));
			month.setText(String.valueOf(Frame.calData.selectedMonth));
			date.setText(String.valueOf(Frame.calData.selectedDay));
		}

		// Show frame
		addFrame.pack();
		addFrame.setVisible(true);
	}

	class ConfirmButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// add Schedule from input data
			ScheduleManager
					.addSchedule(new Schedule(Integer.parseInt(year.getText()), Integer.parseInt(month.getText()),
							Integer.parseInt(date.getText()), subject.getText(), text.getText()));

			// reset TextFields
			year.setText(null);
			month.setText(null);
			date.setText(null);
			subject.setText(null);
			text.setText(null);

			// dispose window
			addFrame.dispose();

		}
	}

	class CancelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// reset TextFields
			year.setText(null);
			month.setText(null);
			date.setText(null);
			subject.setText(null);
			text.setText(null);

			// dispose window
			addFrame.dispose();

		}
	}

}

class LeftButtonListener implements ActionListener 
{
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(Frame.calData.month== 1)
			Frame.UpdateTo(Frame.calData.year -1, 12);
		else
			Frame.UpdateTo(Frame.calData.year, Frame.calData.month -1);
	}
}

class RightButtonListener implements ActionListener 
{
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(Frame.calData.month == 12)
			Frame.UpdateTo(Frame.calData.year +1 , 1);
		else
			Frame.UpdateTo(Frame.calData.year, Frame.calData.month +1);
	}
}

class TodayButtonListener implements ActionListener 
{
	@Override
	public void actionPerformed(ActionEvent e) 
	{	
		
		//get this year & month & today
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		int thisMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
		int today = Calendar.getInstance().get(Calendar.DATE);
	
		Frame.calData.selectedYear = thisYear; 
		Frame.calData.selectedMonth = thisMonth;
		Frame.calData.selectedDay = today;
		
		//update to now(select apply)
		Frame.UpdateTo(thisYear, thisMonth);
	
		
	}

}

class ScheduleButtonListener implements ActionListener
{
	JFrame detailFrame = new JFrame("Detail");

	JPanel detailView = new JPanel();

	JLabel dateLabel = new JLabel("Date:");
	JLabel subjectLabel = new JLabel("Title:");
	JLabel textLabel = new JLabel("Detail:");

	JLabel date = new JLabel();
	JLabel subject = new JLabel();
	JLabel text = new JLabel();

	JPanel editPanel = new JPanel();

	JButton removeButton = new JButton("remove");
	JButton editButton = new JButton("edit");
	
	Schedule nowSchedule;
	
	ScheduleButtonListener()
	{
		detailFrame.setPreferredSize(new Dimension(300, 200));
		detailFrame.setLocation(450, 300);

		detailFrame.getContentPane().setLayout(new BoxLayout(detailFrame.getContentPane(), BoxLayout.Y_AXIS));

		dateLabel.setSize(new Dimension(40, 20));
		subjectLabel.setSize(new Dimension(30, 10));
		textLabel.setSize(new Dimension(30, 10));
		date.setSize(new Dimension(30, 10));
		subject.setSize(new Dimension(30, 10));
		text.setSize(new Dimension(30, 10));

		dateLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		subjectLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		textLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		date.setBorder(new BevelBorder(BevelBorder.LOWERED));
		subject.setBorder(new BevelBorder(BevelBorder.LOWERED));
		text.setBorder(new BevelBorder(BevelBorder.LOWERED));

		removeButton.addActionListener(new RemoveButtonListener());
		editButton.addActionListener(new EditButtonListener());


		// detailView
		detailView.setLayout(new GridLayout(3, 2));

		detailView.add(dateLabel);
		detailView.add(date);
		detailView.add(subjectLabel);
		detailView.add(subject);
		detailView.add(textLabel);
		detailView.add(text);

		// editPanel
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));

		editPanel.add(Box.createHorizontalGlue());
		editPanel.add(removeButton);
		editPanel.add(editButton);

		detailFrame.add(detailView);
		detailFrame.add(editPanel);
	}

	private void setScheduleTo(Schedule newSchedule)
	{
		date.setText(newSchedule.year + "-" + newSchedule.month + "-" + newSchedule.date);
		subject.setText(newSchedule.subject);
		text.setText(newSchedule.text);
		
		nowSchedule = newSchedule;

	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		allLoop: for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++)
				// if dayBox has schedule
				if (Frame.dayBox[i][j].schedules != null)
					for (int k = 0; k < Frame.dayBox[i][j].schedules.length; k++)
						if (e.getSource().equals(Frame.dayBox[i][j].schButton[k]))
						{
							setScheduleTo(Frame.dayBox[i][j].schedules[k]);
													
							// Show frame
							detailFrame.pack();
							detailFrame.setVisible(true);

							break allLoop;
						}
	}

	private class RemoveButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ScheduleManager.removeSchedule(nowSchedule);
			detailFrame.dispose();
		}

	}

	private class EditButtonListener implements ActionListener
	{
		
		JFrame addFrame = new JFrame("Detail");

		JPanel detailPanel = new JPanel();
		JPanel datePanel = new JPanel();
		JLabel yearLabel = new JLabel("Year:");
		JLabel monthLabel = new JLabel("Month:");
		JLabel dateLabel = new JLabel("Date:");
		JLabel subjectLabel = new JLabel("Title:");
		JLabel textLabel = new JLabel("Detail:");

		
		JTextField year = new JTextField();
		JTextField month = new JTextField();
		JTextField date = new JTextField();
		JTextField subject = new JTextField();
		JTextField text = new JTextField();
		
		
		JPanel confirmPanel = new JPanel();
		JButton confirmButton = new JButton("Confirm");
		JButton cancelButton = new JButton("Cancel");
		
		
		EditButtonListener()
		{
			//frame settings			
			addFrame.setPreferredSize(new Dimension(300, 200));
			addFrame.setLocation(450, 300);

			addFrame.getContentPane().setLayout(new BoxLayout(addFrame.getContentPane(), BoxLayout.Y_AXIS));
			
			
			//datePanel settings
			yearLabel.setPreferredSize(new Dimension(50, 20));
			monthLabel.setPreferredSize(new Dimension(50, 20));
			dateLabel.setPreferredSize(new Dimension(50, 20));
			year.setPreferredSize(new Dimension(50,20));
			month.setPreferredSize(new Dimension(50,20));
			date.setPreferredSize(new Dimension(50, 20));
					
			datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
					
			datePanel.add(yearLabel);
			datePanel.add(year);
			datePanel.add(monthLabel);
			datePanel.add(month);
			datePanel.add(dateLabel);
			datePanel.add(date);
						
			
			//label in detailPanel settings
			subjectLabel.setSize(new Dimension(30, 10));
			textLabel.setSize(new Dimension(30, 10));
			subject.setSize(new Dimension(30, 10));
			text.setSize(new Dimension(30, 10));

			yearLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			monthLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			dateLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			subjectLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			textLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			year.setBorder(new BevelBorder(BevelBorder.LOWERED));
			month.setBorder(new BevelBorder(BevelBorder.LOWERED));
			date.setBorder(new BevelBorder(BevelBorder.LOWERED));
			subject.setBorder(new BevelBorder(BevelBorder.LOWERED));
			text.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
			
			//detailPanel settings
			detailPanel.setLayout(new GridLayout(3,2));
					
			detailPanel.add(subjectLabel);
			detailPanel.add(subject);
			detailPanel.add(textLabel);
			detailPanel.add(text);		
					
			
			//confirmButton, Panel settings
			confirmButton.addActionListener(new ConfirmButtonListener());
			cancelButton.addActionListener(new CancelButtonListener());
			confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
			confirmPanel.add(Box.createHorizontalGlue());
			confirmPanel.add(confirmButton);
			confirmPanel.add(cancelButton);
			
			
			//make plusFrame
			addFrame.add(datePanel);
			addFrame.add(detailPanel);
			addFrame.add(confirmPanel);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{	
			year.setText(String.valueOf(nowSchedule.year));
			month.setText(String.valueOf(nowSchedule.month));
			date.setText(String.valueOf(nowSchedule.date));		
			subject.setText(nowSchedule.subject);
			text.setText(nowSchedule.text);
			
			
			// Show frame
			addFrame.pack();
			addFrame.setVisible(true);			
		}
			
			
		class ConfirmButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				
				Schedule newSchedule = new Schedule( Integer.parseInt(year.getText()),
													 Integer.parseInt(month.getText()),
													 Integer.parseInt(date.getText()),
													 subject.getText(),
													 text.getText());
				//add Schedule from input data
				ScheduleManager.editSchedule(	nowSchedule, newSchedule);
											
				
				//reset TextFields
				year.setText(null);
				month.setText(null);
				date.setText(null);
				subject.setText(null);
				text.setText(null);
				
				
				//set & apply datas
				ScheduleButtonListener.this.setScheduleTo(newSchedule);
				detailFrame.repaint();
				
				//dispose window
				addFrame.dispose();
			}	
		}

		class CancelButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				//reset TextFields
				year.setText(null);
				month.setText(null);
				date.setText(null);
				subject.setText(null);
				text.setText(null);
				
				//dispose window
				addFrame.dispose();

			}
		}
	}
}

class DayButtonListener implements ActionListener 
{
	@Override
	public void actionPerformed(ActionEvent e) 
	{	
		if (e.getSource() instanceof JButton)
		{
			int selectedYear =  Frame.calData.year; 
			int selectedMonth = Frame.calData.month; 
			int selectedDay = Integer.parseInt(((JButton)e.getSource()).getText());
			
			if( Frame.calData.selectedYear == selectedYear 
					&&	Frame.calData.selectedMonth == selectedMonth
					&&	Frame.calData.selectedDay == selectedDay)
			{
				Frame.calData.selectedYear = 0; 
				Frame.calData.selectedMonth = 0;
				Frame.calData.selectedDay = 0;
			}		
			
			else
			{
				Frame.calData.selectedYear = selectedYear; 
				Frame.calData.selectedMonth = selectedMonth;
				Frame.calData.selectedDay = selectedDay;
			}
			//update to now(select apply)
			Frame.Update();
		}
		
	}

}


class UpdateTimeThread extends Thread 
{

	public void run()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 aa hh시 mm분 ss초");
		String time;
		
		while(true)
		{
			//update time
			time = dateFormat.format(Calendar.getInstance().getTime());
			Frame.nowTime.setText(time);
	
			try 
			{
				Thread.sleep(50);
			} 
			catch (Exception ex) 
			{
				LogWriter.LogEx(ex);
			}
			
		}
	}
}

