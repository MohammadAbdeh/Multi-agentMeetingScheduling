import java.util.ArrayList;

package src;

public class MeetingAgent {

    public ArrayList<Day> calendar ;
    public ArrayList<MeetingAgent> contactList ;


    public MeetingAgent(ArrayList<Day> calendar, ArrayList<MeetingAgent> contactList){

        this.calendar = calendar;
        this.contactList = contactList;
    }

}