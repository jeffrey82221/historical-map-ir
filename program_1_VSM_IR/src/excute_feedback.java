import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xugh on 2016/4/9.
 */
public class excute_feedback {
//    static int num_of_query;
//    static int num_of_back =2;
//    static int Vectorlength=9;
//    static int num_of_doc =2;//46972
//    static String addrBase="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\";
//    static String addr_of_query ="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\query_xu.xml";
//    static String addr_of_back = "E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\Qanswer.txt";
//    static String addrFilelist="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\file_list_xu";
//    static String addrVectorQ="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\q_xu_v\\";
//    static String addrVectorD="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\q_xu_d\\";
//    static String addrStopList="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\stoplist.zh_TW.u8";
//    static String addr_of_feedback="E:\\2016.3-present NTU\\Web Retrieval and Mining 【正选】\\Q_xu\\QFanswer.txt";

    static int num_of_query ;
    static int num_of_back =100;
    static int Vectorlength=30000;
    static int num_of_doc =46972;//46972
    static String addrBase="D:\\WORKDoc\\CIRB010\\";
    static String addr_of_query ="D:\\WORKDoc\\queries\\queries\\query-test.xml";
    static String addrVectorQ="C:\\Users\\PSCL\\IdeaProjects\\WebRetrivel\\querydata\\";
    static String addrVectorD="C:\\Users\\PSCL\\IdeaProjects\\WebRetrivel\\docdata\\";
    static String addr_of_back = "D:\\WORKDoc\\update\\answer.txt";
    static String addrFilelist="D:\\WORKDoc\\model\\model\\file-list";
    static String addrStopList="D:\\WORKDoc\\stoplist\\stoplist.zh_TW.u8";
    static String addr_of_feedback = "D:\\WORKDoc\\update\\answer_with_feedback.txt";

    public static void main(String[] args) throws IOException {
        ArrayList idList;
        idList=docID();
//        Iterator<String> it3 = idList.iterator();
//        for (int m = 1;m<=num_of_query;m++) { //set num of query
//            System.out.println("=======Number of query checking=======::::::"+m);
//            String filePath = addr_of_back;
//            FileWriter fw = new FileWriter(filePath, true);
//            BufferedWriter bw = new BufferedWriter(fw);
//            String output=null;
//            output= it3.next()+" ";
//            System.out.println(output);
//            OutputRanking(idList,queryDisMap(m),fw,output);
//            bw.close();
//            fw.close();
//        }

        Feedback();

        Iterator<String> it4 = idList.iterator();
        for (int m = 1;m<=num_of_query;m++) { //set num of query
            System.out.println("=======Number of query checking=======::::::"+m);
            String filePath = addr_of_feedback;
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            String output=null;
            output= it4.next()+" ";
            System.out.println(output);
            OutputRanking(idList,queryDisMap(m),fw,output);
            bw.close();
            fw.close();
        }
    }

    public static void Feedback() throws IOException {
        Main.vocabReadIn();
        Main.stopWordsReadIn();
        for (int m = 1;m<=num_of_query;m++) {
            ArrayList<Double> vectorq= new ArrayList();
            BufferedReader bufrq = new BufferedReader(new InputStreamReader(new FileInputStream(addrVectorQ+m+".txt")));
            LineNumberReader lnrq= new LineNumberReader(bufrq);
            String lineq = null;
            while ((lineq = lnrq.readLine()) != null) {
                String s3[]=lineq.substring(1,lineq.length()-1).split(", ");
                for(int i = 0;i<s3.length ;i++){
                    vectorq.add(Double.parseDouble(s3[i]));
                }
            }
//            ArrayList vector_new = new ArrayList();
            BufferedReader bufrd = new BufferedReader(new InputStreamReader(new FileInputStream(addr_of_back)));
            LineNumberReader lnrd = new LineNumberReader(bufrd);

            for(int n = 1;n<=num_of_back;n++) {
                String line = lnrd.readLine();
                String originalfilename = addrBase +"CIRB010\\"+line.substring(4,7)+"\\"+line.substring(8,11)+"\\"+ line.substring(4);
                System.out.println(originalfilename);
                ArrayList<Double> vectord = new ArrayList();
                vectord =Main.convertVector(Main.docContext(originalfilename),"buff.txt");
                System.out.println(n);
                for(int numelem = 0;numelem<Vectorlength;numelem++){
                    vectorq.set(numelem,((vectorq.get(numelem) * 0.9) + (0.001 * vectord.get(numelem))));
                }
//                System.out.println(vectorq);
                System.out.println(m+"num of queries feedback:"+n);
            }
//            System.out.println("M:"+m);
            lnrd.close();
            bufrd.close();
            BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(addrVectorQ+m+".txt")));
            bufw.write(String.valueOf(vectorq));
            bufw.newLine();
            bufw.close();

        }

    }



    public static void OutputRanking(ArrayList idList, TreeMap querydis, FileWriter fw, String output) throws IOException {
        for(int h =0;h<num_of_back;h++){ // set num of feedback
            Map.Entry<Double,Integer> me=querydis.pollLastEntry();
            int searchNum= me.getValue();
            String output2= output+getDocNum(searchNum);
            fw.write(output2+" "+"\r\n");
        }
    }

    public static String getDocNum(int searchNum) throws IOException {
        BufferedReader bufr7 = new BufferedReader(new InputStreamReader(new FileInputStream(addrFilelist)));
        LineNumberReader lnr7= new LineNumberReader(bufr7);
        for (int n=0;n<searchNum-1; n++){lnr7.readLine();}
        return lnr7.readLine().substring(16).toLowerCase();
//        return lnr7.readLine().toLowerCase();
    }

    public static ArrayList docID() throws IOException {
        String addr= addr_of_query;
        BufferedReader bufr6 = new BufferedReader(new InputStreamReader(new FileInputStream(addr)));
        LineNumberReader lnr6= new LineNumberReader(bufr6);
        ArrayList idList = new ArrayList();
        String line6;
        String doc6= null;
        String pattern = "<number>.*?</number>";
        Pattern r = Pattern.compile(pattern);
        while ((line6 = lnr6.readLine()) != null) {
            doc6=doc6+line6;
        }
        num_of_query=0;
        Matcher m = r.matcher(doc6);
        for (; m.find();){
            num_of_query++;
            idList.add( m.group(0).substring(22,25));
        }
        lnr6.close();
        return idList;
    }

    public static TreeMap queryDisMap(int numOfQuery) throws IOException {
        TreeMap<Double,Integer> simmap = new TreeMap<Double,Integer>();
        for(int d = 1;d<=num_of_doc;d++) {   //set num of doc
            System.out.println("Distance(s) has(ve) calculated:"+d);
            String addr_q=addrVectorQ+numOfQuery+".txt";
            String addr_d=addrVectorD+d+".txt";
            simmap.put(calDistance(addr_q,addr_d),d);
        }
        return simmap;
    }

    public static double calDistance(String addr_query,String addr_doc) throws IOException {
        BufferedReader bufr3 = new BufferedReader(new InputStreamReader(new FileInputStream(addr_doc)));
        LineNumberReader lnr3 = new LineNumberReader(bufr3);
        ArrayList vector_d= new ArrayList();
        String line = null;
        while ((line = lnr3.readLine()) != null) {
            String s1[]=line.substring(1,line.length()-1).split(", ");
            for(int i = 0;i<s1.length ;i++){
                vector_d.add(Double.parseDouble(s1[i]));
            }
        }
        BufferedReader bufr4 = new BufferedReader(new InputStreamReader(new FileInputStream(addr_query)));
        LineNumberReader lnr4 = new LineNumberReader(bufr4);
        ArrayList vector_q= new ArrayList();
        String line2 = null;
        while ((line2 = lnr4.readLine()) != null) {
            String s1[]=line2.substring(1,line2.length()-1).split(", ");
            for(int i = 0;i<s1.length ;i++){
                vector_q.add(Double.parseDouble(s1[i]));
            }
        }
        double shape_q = 0.0;
        double shape_d = 0.0;
        double dot = 0.0;
        double sim=0.0;
        Iterator<Double> it = vector_q.iterator();
        Iterator<Double> itt = vector_d.iterator();
        while (it.hasNext()) {
            double dotq = it.next();
            double dotd = itt.next();
            dot = dot + dotd * dotq;
            shape_q = shape_q + Math.pow(dotq, 2);
            shape_d = shape_d + Math.pow(dotd, 2);
        }
        shape_q=Math.sqrt(shape_q);
        shape_d=Math.sqrt(shape_d);
        sim = dot/(shape_d*shape_q);

        return sim;
    }

}
