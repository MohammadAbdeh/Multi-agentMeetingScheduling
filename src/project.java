import java.util.ArrayList;


package src;

class project{
public static void main( String[] args) {

    final ArrayList<Hour> hours = new ArrayList<Hour>(24);
    final Hour hour = new Hour(0, true, 0.6);

    hours.set(0,hour);

    final ArrayList<Day> calendar = new ArrayList<>(1);
    final Day day = new Day(hours);

    calendar.set(0, day);

    final ArrayList<MeetingAgent> contactList = new ArrayList<MeetingAgent>(2); 

    final MeetingAgent agent = new MeetingAgent(calendar, contactList);
    
    System.out.println(agent.calendar.get(0).hours.get(0));

  }
}


