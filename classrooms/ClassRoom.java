package classrooms;

public class ClassRoom implements Cloneable{

    String name;
    int cap;
    boolean comp;
    int numOfTeachers;
    boolean onUni;


    public ClassRoom(String name, int cap, boolean comp, int numOfTeachers, boolean onUni) {
        this.name = name;
        this.cap = cap;
        this.comp = comp;
        this.numOfTeachers = numOfTeachers;
        this.onUni = onUni;
    }

    public String getName() {
        return name;
    }

    public int getCap() {
        return cap;
    }

    public boolean isComp() {
        return comp;
    }

    public int getNumOfTeachers() {
        return numOfTeachers;
    }

    public boolean isOnUni() {
        return onUni;
    }

    public boolean notOnUni(){
        return !onUni;
    }

    @Override
    public ClassRoom clone() throws CloneNotSupportedException {
        return (ClassRoom) super.clone();
    }
}
