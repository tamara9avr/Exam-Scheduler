package examterm;

import classrooms.ClassRoom;
import exams.Exam;

import java.util.ArrayList;
import java.util.Comparator;

public class ExamTerm {

    private final ArrayList<Exam> exams;
    private final ArrayList<ClassRoom> classRooms;

    private final int days;

    public ExamTerm(ArrayList<Exam> exams, ArrayList<ClassRoom> classRooms, int days) {

        this.exams = exams;
        this.classRooms = classRooms;
        this.classRooms.sort(Comparator.comparing(ClassRoom::notOnUni).thenComparing(ClassRoom::getCap));

        this.days = days;
    }

    public ArrayList<Exam> getExams() {
        return exams;
    }

    public ArrayList<ClassRoom> getClassRooms() {
        return classRooms;
    }

    public ArrayList<ClassRoom> getCompClassRooms(){
        ArrayList<ClassRoom> ret = new ArrayList<>();
        for (ClassRoom classRoom : classRooms) {
            if (classRoom.isComp()) {
                ret.add(classRoom);
            }
        }
        return ret;
    }

    public int getDays() {
        return days;
    }
}
