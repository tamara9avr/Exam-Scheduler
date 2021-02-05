package exams;

import java.util.ArrayList;

public class Exam implements Cloneable{

    private final int year;

    private final String code;

    private final int numOfStudents;
    private final boolean comp;
    private final ArrayList<String> sections;


    public Exam(String code, int numOfStudents, boolean comp, ArrayList<String> sections) {
        this.code = code;

        year = Integer.parseInt(code.substring(5,6));

        this.numOfStudents = numOfStudents;
        this.comp = comp;
        this.sections = sections;
    }

    public int getYear() {
        return year;
    }


    public int getNumOfStudents() {
        return numOfStudents;
    }

    public boolean isComp() {
        return comp;
    }

    public String getCode(){
        return code;
    }

    public static boolean isSameYearAndProgram(Exam e1, Exam e2){
        boolean flag=false;
        for(String od : e1.sections){
            if (e2.sections.contains(od)) {
                flag = true;
                break;
            }
        }
        return (e1.getYear()==e2.getYear() && flag);
    }

    public static boolean isNextYearAndSameProgram(Exam e1, Exam e2){
        boolean flag=false;
        for(String od : e1.sections){
            if (e2.sections.contains(od)) {
                flag = true;
                break;
            }
        }
        return (Math.abs(e1.getYear()-e2.getYear())==1 && flag);
    }


    public Exam clone() throws CloneNotSupportedException {
        return (Exam) super.clone();
    }



}
