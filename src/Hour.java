import java.util.ArrayList;

package src;


public class Hour {

    private int time;
    private boolean availability;
    private double prefrence;

    public Hour(int time, boolean availability, double prefrence) {
        this.time = time;
        this.availability = availability;
        this.prefrence = prefrence;
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

}