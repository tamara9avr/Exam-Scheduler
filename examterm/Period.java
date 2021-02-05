package examterm;

import classrooms.ClassRoom;

import java.util.ArrayList;

public class Period implements Cloneable{
    private final int day;
    private final int number;

    private int numWithComp=0;
    private int numWithoutComp=0;

    private int numOfBadClassRooms = 0;

    private ArrayList<ClassRoom> freeClassRooms;

    public Period(int day, int number, ArrayList<ClassRoom> freeClassRooms) {
        this.day = day;
        this.number = number;
        this.freeClassRooms = freeClassRooms;

        for (ClassRoom curr : freeClassRooms) {
            if ((!curr.isOnUni()) || (curr.getNumOfTeachers() > 1)) numOfBadClassRooms++;
            if (curr.isComp()) {
                numWithComp += curr.getCap();
            } else numWithoutComp += curr.getCap();
        }
    }

    public double getPercentageOfBadClassRooms() {
        double ret = 1;
        if(freeClassRooms.size()>0)
            ret = (double)numOfBadClassRooms/freeClassRooms.size();
        return ret;
    }

    @Override
    public Period clone() {
        Period ret = null;
        try {
            ret = (Period) super.clone();
            ArrayList<ClassRoom> help = new ArrayList<>();

            for(ClassRoom cr : freeClassRooms){
                help.add(cr.clone());
            }
            ret.freeClassRooms = help;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int getDay() {
        return day;
    }

    public int getNumber() {
        return number;
    }

    public int getNumWithComp() {
        return numWithComp;
    }

    public int getNumWithoutComp() {
        return numWithoutComp;
    }


    public ArrayList<ClassRoom> getFreeClassRooms() {
        return freeClassRooms;
    }

    public boolean removeFreeClassRooms(ArrayList<ClassRoom> classRooms) {                                              //Vraca flag da li ima jos mesta u ovom terminu
        for(ClassRoom cr : classRooms) {
            freeClassRooms.removeIf(help -> cr.getName().equals(help.getName()));
            if(cr.isComp()) numWithComp-=cr.getCap();
            else numWithoutComp-=cr.getCap();
            if(cr.notOnUni() || cr.getNumOfTeachers()>1){
                numOfBadClassRooms--;
            }
        }

        return (numWithoutComp + numWithComp) != 0;
    }
}
