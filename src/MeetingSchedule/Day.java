package MeetingSchedule;

import java.io.Serializable;
import java.util.ArrayList;

public class Day implements Serializable {

    private DayName day;
    private ArrayList<Hour> hours;

    public Day(DayName day, ArrayList<Hour> hours) {
        this.day = day;
        this.hours = hours;
    }

    public Day(DayName day) {
        this.day = day;
        hours = generateRandomHours();
    }

    public Day(Day day1, Day day2) {
        day = day1.day;
        hours = new ArrayList<>();
        for(int i = 0; i<24; i++) {
            hours.add(day1.hours.get(i).merge(day2.hours.get(i)));
        }
    }

    private ArrayList<Hour> generateRandomHours() {
        ArrayList<Hour> res = new ArrayList<>();
        for(int i = 0; i < 24; i ++) {
            res.add(new Hour(i));
        }
        return res;
    }

    public ArrayList<Hour> getHours(){

        return hours;
    }

    public Day merge(ArrayList<Day> days) {
        if(days.size() == 0) {
            return this;
        }
        if(days.size() == 1) {
            return new Day(this, days.get(0));
        }
        Day res = new Day(this, days.get(0));
        for(int i = 1; i<days.size(); i++) {
            res = new Day(res, days.get(i));
        }
        return res;
    }

    public Hour getMeetingTime(boolean ignore) {
        Hour highest = null;
        for(Hour hour : hours) {
            if(hour.getAvailability() || ignore) {
                if(highest == null) {
                    highest = hour;
                } else {
                    if(highest.getPrefrence()<hour.getPrefrence()) {
                        highest = hour;
                    }
                }
            }
        }
        return highest;
    }

    public boolean wantToRefuse() {
        int available = 0;
        double maxPref = 0;
        for(Hour hour : hours) {
            if(hour.getAvailability()) {
                maxPref = Math.max(maxPref, hour.getPrefrence());
                available++;
            }
        }

        return !(available >= 18 && maxPref >=0.95);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(day).append(":\n");
        for(Hour hour : hours) {
            res.append(hour);
        }
        return res.toString();
    }

    public boolean isAvailable(Hour meeting) {
        return (hours.get(meeting.getTime()).getAvailability());
    }

    public Hour lock(Hour meeting) {
        Hour old = hours.get(meeting.getTime());
        hours.get(meeting.getTime()).replace(meeting);
        return old;
    }
}