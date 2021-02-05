package formater;

import classrooms.ClassRoom;
import examterm.ExamTerm;
import exams.Exam;
import examterm.Period;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import scheduler.Scheduler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Formater {

    ArrayList<Exam> exams = new ArrayList<>();
    ArrayList<ClassRoom> classRooms = new ArrayList<>();
    long days;

    int num = 2;

    public Formater(){}

    public ExamTerm readPeriod() throws Exception {

        Object obj = new JSONParser().parse(new FileReader(".\\javni_testovi\\rok"+num+".json"));

        JSONObject jo = (JSONObject) obj;

        days = (long) jo.get("trajanje_u_danima");

        JSONArray ex = (JSONArray) jo.get("ispiti");

        Iterator itr1 = ex.iterator();

        Iterator<Map.Entry> itr2;

        String code="";
        long numOfStudents=0;
        boolean comp=false;
        ArrayList<String> sections = null;

        while(itr1.hasNext()){

           itr2 = ((Map) itr1.next()).entrySet().iterator();
            while(itr2.hasNext()) {
                Map.Entry pair = itr2.next();

                if(pair.getKey().equals("sifra")){
                    code = (String) pair.getValue();
                }
                else{
                    if(pair.getKey().equals("prijavljeni")){
                        numOfStudents = (long) pair.getValue();
                    }
                    else{
                        if(pair.getKey().equals("racunari")){
                            comp = ((long)pair.getValue()==1);
                        }
                        else{
                            sections = new ArrayList<>();
                            JSONArray ja = (JSONArray) pair.getValue();
                            for (Object o : ja) {
                                sections.add((String) o);
                            }
                        }
                    }
                }

            }


            Exam e = new Exam(code,(int)numOfStudents,comp,sections);
            exams.add(e);


        }


        return new ExamTerm(exams,readClassRooms(),(int)days);

    }

    public ArrayList<ClassRoom> readClassRooms() throws Exception{

        Object obj = new JSONParser().parse(new FileReader(".\\javni_testovi\\sale"+num+".json"));

        JSONArray jo = (JSONArray) obj;

        String name="";
        long cap=0;
        boolean comp=false;
        long numOfTeachers=0;
        boolean onUni=false;

        Iterator itr1 = jo.iterator();

        Iterator<Map.Entry> itr2;

        while(itr1.hasNext()){
            itr2 = ((Map)itr1.next()).entrySet().iterator();
            while(itr2.hasNext()){
                Map.Entry pair = itr2.next();

                if(pair.getKey().equals("naziv")){
                    name = (String) pair.getValue();
                }
                else{
                    if(pair.getKey().equals("kapacitet")){
                        cap = (long) pair.getValue();
                    }
                    else{
                        if(pair.getKey().equals("racunari")){
                            comp = ((long)pair.getValue()==1);
                        }
                        else{
                            if(pair.getKey().equals("dezurni")){
                                numOfTeachers = (long) pair.getValue();
                            }
                            else{
                                onUni = ((long)pair.getValue()==1);
                            }
                        }
                    }
                }
            }

            ClassRoom cr = new ClassRoom(name,(int)cap,comp, (int)numOfTeachers,onUni );
            classRooms.add(cr);
        }
            return classRooms;
    }

    public void write(ArrayList<Scheduler.Solution> solutions){
        String fileName = ".\\src.\\solution"+num+".csv";
        try {

            OutputStream os = new FileOutputStream(fileName);
            os.write(239);
            os.write(187);
            os.write(191);

            PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

          //  Writer fileWriter = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_16);


            ArrayList<String> das = new ArrayList<>();

            ArrayList<ArrayList<ArrayList<String>>> writing = new ArrayList<>();

            ArrayList<String> classRoomIndexes = new ArrayList<>();

            for(int i = 1 ; i<days+1; i++){

                ArrayList<ArrayList<String>> pom = new ArrayList<>();

                for(int k = 0; k<4; k++){
                    ArrayList<String> periods = new ArrayList<>();
                    periods.add("T"+(k+1));
                    for(ClassRoom cr : classRooms) {
                        periods.add("X");
                    }
                    pom.add(periods);
                }

                StringBuilder str = new StringBuilder();

                str.append("Dan").append(i);

                for(ClassRoom cr : classRooms){
                    str.append(";").append("Sala").append(cr.getName());
                    if(!classRoomIndexes.contains(cr.getName())){
                        classRoomIndexes.add(cr.getName());
                    }
                }


                das.add(str.toString());

                writing.add(pom);

            }

            for(Scheduler.Solution sol : solutions){
                int ind1 = sol.getDay(), ind2 = sol.getTerm();
                for(ClassRoom cr : sol.getClassRooms()){
                    int ind3 = classRoomIndexes.indexOf(cr.getName())+1;
                    writing.get(ind1).get(ind2).set(ind3,sol.getEx().getCode());
                }
            }

            for(int x = 0; x<days; x++){
                fileWriter.append(das.get(x)).append(String.valueOf('\n'));
                for(ArrayList<String> terms : writing.get(x)){
                    for(String str : terms){
                        fileWriter.append(str).append(";");
                    }
                    fileWriter.append('\n');
                }
                fileWriter.append('\n');
            }


            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
