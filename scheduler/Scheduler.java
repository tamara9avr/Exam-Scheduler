package scheduler;

import classrooms.ClassRoom;
import exams.Exam;
import examterm.ExamTerm;
import examterm.Period;
import formater.Formater;

import java.util.ArrayList;
import java.util.Comparator;


public class Scheduler {

    public static class Solution{
        Exam ex;
        int day;
        int term;
        ArrayList<ClassRoom> classRooms;

        public Exam getEx() {
            return ex;
        }

        public int getDay() {
            return day;
        }

        public int getTerm() {
            return term;
        }

        public ArrayList<ClassRoom> getClassRooms() {
            return classRooms;
        }

        public Solution(Exam ex, int day, int term, ArrayList<ClassRoom> classRooms) {
            this.ex = ex;
            this.day = day;
            this.term = term;
            this.classRooms = classRooms;
        }

        public String toString(){
            StringBuilder str = new StringBuilder("Sifra ispita: " + ex.getCode() + " dan: " + (day+1) + " termin: " + (term+1) + " sale:");
            for(ClassRoom cr : classRooms){
                str.append(" ").append(cr.getName());
            }
            return str.toString();
        }
    }

    ArrayList<Exam> nodes;
    ArrayList<ArrayList<Period>> domains = new ArrayList<>();
    ArrayList<Solution> solutions = new ArrayList<>();

    ArrayList<ArrayList<Exam>> sameYearExams = new ArrayList<>();

    public Scheduler(ExamTerm ex){

        for(int i = 0; i<4; i++){
            ArrayList<Exam> help0 = new ArrayList<>();
            sameYearExams.add(help0);
        }

        nodes = ex.getExams();

        for (Exam exam : nodes) {

            sameYearExams.get(exam.getYear() - 1).add(exam);

            ArrayList<Period> help = new ArrayList<>();

            ArrayList<ClassRoom> cr;

            for (int i = 0; i < ex.getDays(); i++) {
                for (int j = 0; j < 4; j++) {
                    if (exam.isComp()) {
                        cr = ex.getCompClassRooms();
                    } else {
                        cr = ex.getClassRooms();
                    }
                    Period p = new Period(i, j, cr);
                    help.add(p);
                }
            }
            domains.add(help);
        }
    }

    public boolean backtracking(int length, ArrayList<Exam> nodes, ArrayList<ArrayList<Period>> domains,ArrayList<Solution>solutions, int lvl) {
        if (lvl == length) return true;

        int v = get_most_constrained_node(nodes, domains);
        sort_domains(v, domains);

        for(int val = 0; val<domains.get(v).size(); val++){
            if(is_consistent_assignment(v, val, nodes, domains)){
                ArrayList<ClassRoom> takenClassRooms = takeClassRooms(v,val,nodes,domains);
                solutions.add(new Solution(nodes.get(v), domains.get(v).get(val).getDay(),domains.get(v).get(val).getNumber(), takenClassRooms));
                ArrayList<ArrayList<Period>> new_domains = deepCopy(domains);
                ArrayList<Exam> new_nodes = copy(nodes);
                    update_domain(v, val, new_domains, nodes);
                    update_seats(v,val, new_domains, takenClassRooms);
                    new_nodes.remove(v);
                    new_domains.remove(v);
                if (backtracking(length,new_nodes,new_domains,solutions,lvl+1)){
                    return true;
                }
                solutions.remove(solutions.size()-1);
            }
        }

        return false;
    }

    private ArrayList<Exam> copy(ArrayList<Exam> arr){
        ArrayList<Exam> exams = new ArrayList<>();

        try {
            for (Exam ex : arr) {
                exams.add(ex.clone());
            }
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return exams;
    }


    private ArrayList<ClassRoom> takeClassRooms(int v, int val, ArrayList<Exam> nodes, ArrayList<ArrayList<Period>> domains) {

        ArrayList<ClassRoom> ret = new ArrayList<>();
        int cap = nodes.get(v).getNumOfStudents();

        ArrayList<ArrayList<Period>> copyDomains = deepCopy(domains);
        ArrayList<ClassRoom> help1 = copyDomains.get(v).get(val).getFreeClassRooms();                                   //Niz ucionica koje su na ETF-u
        help1.removeIf(ClassRoom::notOnUni);
        copyDomains = deepCopy(domains);
        ArrayList<ClassRoom> help2 = copyDomains.get(v).get(val).getFreeClassRooms();                                   //Niz ucionica koje nisu na ETF-u
        help2.removeIf(ClassRoom::isOnUni);

        int i, k;

        while(cap>0){
            if(help1.size()>0){

                i=help1.size()-1;
                while((i>0)&&(cap<help1.get(i).getCap())) i--;


                    ret.add(help1.get(i));
                    cap -= help1.get(i).getCap();
                    help1.remove(i);
            }
            else{
                k=help2.size()-1;

                while((k>0)&&(cap<help2.get(k).getCap())) k--;


                ret.add(help2.get(k));
                cap -= help2.get(k).getCap();
                help2.remove(k);
            }
            if(cap<=0) break;
        }

        return  ret;
    }

    private void update_domain(int v, int val, ArrayList<ArrayList<Period>> new_domains, ArrayList<Exam> nodes) {
        int day = new_domains.get(v).get(val).getDay();
        int number = new_domains.get(v).get(val).getNumber();

        for(int i = 0; i<nodes.size(); i++){
            if(i!=v){
                if(Exam.isSameYearAndProgram(nodes.get(i),nodes.get(v))){
                    new_domains.get(i).removeIf(p -> p.getDay() == day);
                }
                else {
                    if (Exam.isNextYearAndSameProgram(nodes.get(i), nodes.get(v))) {
                        new_domains.get(i).removeIf(period -> (period.getDay()==day) && (period.getNumber()==number));
                    }
                }
            }
        }
    }

    private int get_most_constrained_node(ArrayList<Exam> nodes, ArrayList<ArrayList<Period>> domains) {
        int sol = 0;
        int minDomainSize = domains.get(0).size();

        for (Exam node : nodes) {
            int i  = nodes.indexOf(node);
            if (domains.get(i).size() < minDomainSize) {
                minDomainSize = domains.get(i).size();
                sol = i;
            }
        }
        return sol;
    }

    private void sort_domains(int numOfNode, ArrayList<ArrayList<Period>> domains){
        domains.get(numOfNode).sort(Comparator.comparing(Period::getPercentageOfBadClassRooms));
    }

    private boolean is_consistent_assignment(int numOfNode,int numOfSolution,ArrayList<Exam> nodes, ArrayList<ArrayList<Period>> domains){

        int numOfSeats = domains.get(numOfNode).get(numOfSolution).getNumWithComp() + domains.get(numOfNode).get(numOfSolution).getNumWithoutComp() ;
    /*    if(!nodes.get(numOfNode).isComp()) {
            numOfSeats +=domains.get(numOfNode).get(numOfSolution).getNumWithoutComp();
        }

     */
        return (nodes.get(numOfNode).getNumOfStudents()<=numOfSeats);
    }

    private void update_seats(int v, int val, ArrayList<ArrayList<Period>> new_domains, ArrayList<ClassRoom> takenClassRooms) {
        Period per = new_domains.get(v).get(val);

        for(ArrayList<Period> periods : new_domains){
            int index = findInArray(periods,per);
            if(index!=-1)
                if(!periods.get(index).removeFreeClassRooms(takenClassRooms)){                                                //Oduzima broj dodeljenih mesta
                    periods.remove(index);                                                                                    // Ako nema vise mesta u terminu odmah ga brise
                }
        }
    }

    private int findInArray(ArrayList<Period> arr, Period per){
        int ret = -1;
        for(Period period : arr){
            if((period.getDay()==per.getDay())&&(period.getNumber()==per.getNumber())) {
                ret = arr.indexOf(period);
                break;
            }
        }
        return ret;
    }

    private ArrayList<ArrayList<Period>> deepCopy(ArrayList<ArrayList<Period>> domains) {
        ArrayList<ArrayList<Period>> ret = new ArrayList<>();
        for(ArrayList<Period> arr:domains){
            ArrayList<Period> p = new ArrayList<>();
            for(Period per:arr){
                p.add(per.clone());
            }
            ret.add(p);
        }
        return ret;
    }

    public static void main(String[] args) throws Exception {
        Formater f = new Formater();
        Scheduler s = new Scheduler(f.readPeriod());
        s.backtracking(s.nodes.size(),s.nodes,s.domains,s.solutions,0);

        for(Solution sol : s.solutions){
            System.out.println(sol);
        }

        f.write(s.solutions);

    }
}
