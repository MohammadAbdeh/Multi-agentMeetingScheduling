package MeetingSchedule;

import java.io.Serializable;

public class Hour implements Serializable {

    private int time;
    private boolean availability;
    private double prefrence;

    public Hour(int time, boolean availability, double prefrence) {
        this.time = time;
        this.availability = availability;
        this.prefrence = prefrence;
    }

    public Hour(int time) {
        this.time = time;
        if(Math.random() > 0.25) {
            this.availability = true;
            this.prefrence = Math.random();
        } else {
            this.availability = false;
            this.prefrence = 0;
        }
    }

    public Hour merge(Hour hour) {
        return new Hour(time, availability && hour.availability, prefrence + hour.prefrence);
    }

    public void updateAvailability(boolean availability){
        this.availability = availability;
    }

    public void updatePrefrence( double prefrence){
        this.prefrence = prefrence;
    }

    public boolean getAvailability(){
        return this.availability;
    }

    public double getPrefrence(){
        return this.prefrence;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.valueOf(time)+" : " + availability +", preference : " + String.valueOf(prefrence)+"\n";
    }

    public void replace(Hour meeting) {
        availability = false;
        prefrence = 0;
    }
}