import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PSCL on 2016/4/2.
 */
public class Main {
    static Map<Object,Object> vocabmap = new HashMap<Object, Object>();
    static int numOfVector ;
    static String stoplist ;

//    static int Vectorlength=9;
//    static String addrvocab="D:\\WORKDoc\\Q_xu\\vocab_xu";
//    static String addr_of_query = "D:\\WORKDoc\\Q_xu\\query_xu.xml";
//    static String addrVectorQ="D:\\WORKDoc\\Q_xu\\q_xu_v\\";
//    static String addrVectorD="D:\\WORKDoc\\Q_xu\\q_xu_d\\";
//    static String addrBase="D:\\WORKDoc\\Q_xu\\";
//    static String addrFilelist="D:\\WORKDoc\\Q_xu\\file_list_xu";
//    static String addrStopList="D:\\WORKDoc\\stoplist\\stoplist.zh_TW.u8";
//    static String addrInvertFile="D:\\WORKDoc\\Q_xu\\invertfile_xu";


    static int Vectorlength=30000;
    static String addrvocab="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\vocab.all";
    static String addr_of_query ="C:\\Users\\Xugh\\IdeaProjects\\WebRetrivalqueries\\queries\\query-test.xml";
    static String addrVectorQ="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\querydata\\";
    static String addrVectorD="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\docdata\\";
    static String addrBase="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\CIRB010\\";
    static String addrFilelist="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\file-list";
    static String addrStopList="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\stoplist.zh_TW.u8";
    static String addrInvertFile="C:\\Users\\Xugh\\IdeaProjects\\WebRetrival\\inverted-file";
    public static void main(String[] args) throws IOException {
        vocabReadIn();
        stopWordsReadIn();
        HashMap<String,HashMap<String, Float>> all_tf = Tfidf.tfAllFiles("D:\\WORKDoc\\CIRB010\\CIRB010\\cdn");
//        System.out.println(all_tf.get(""));
        HashMap<String, Float> idfs = Tfidf.idf(all_tf);
//        System.out.println("idfs"+idfs.get("你"));
        ArrayList docList = docFind();
        Iterator<String> it = docList.iterator();
        numOfVector=0;
//        System.out.println(it.hasNext());
        while (it.hasNext()) {
            numOfVector++;
            String next =it.next();
            HashMap<String, Float> ttf=Tfidf.tf(Tfidf.cutWords(next));
            System.out.println("Start converting documents to vector:"+numOfVector);
            convertVector(docContext(next),addrVectorD,numOfVector,idfs,ttf);
        }
        numOfVector=0;
        String addr2 = addr_of_query;
        ArrayList docList2 = queryContext(addr2);
        Iterator<String> it2 = docList2.iterator();
        while (it2.hasNext()) {
            numOfVector++;
            System.out.println("Converting queries to vector:"+numOfVector);
            convertVector(it2.next().toString(),addrVectorQ);
        }
    }
    public static void vocabReadIn() throws IOException {
        BufferedReader bufr = new BufferedReader(new InputStreamReader(new FileInputStream(addrvocab)));
        LineNumberReader lnr = new LineNumberReader(bufr);
        String line = null;
        while ((line = lnr.readLine()) != null) {
            vocabmap.put(line,lnr.getLineNumber());
            System.out.println("loading vocabulary:"+lnr.getLineNumber());
        }
//        System.out.println("vocabulary Treemap:"+vocabmap);
        lnr.close();
    }

    public static void stopWordsReadIn() throws IOException {
        BufferedReader bufr = new BufferedReader(new InputStreamReader(new FileInputStream(addrStopList)));
        LineNumberReader lnr = new LineNumberReader(bufr);
        String line = null;
        while ((line = lnr.readLine()) != null) {
                stoplist=stoplist+line;
        }
        System.out.println("Stopwords:"+stoplist);
        lnr.close();
    }

    public static ArrayList docFind() throws IOException {
        String addr_file=addrBase;
        ArrayList docList = new ArrayList();
        BufferedReader bufr5 = new BufferedReader(new InputStreamReader(new FileInputStream(addrFilelist)));
        LineNumberReader lnr5 = new LineNumberReader(bufr5);
        String line5= null;
        while ((line5 = lnr5.readLine()) != null) {
            docList.add(addr_file+line5);
        }
        return docList;
    }
//    public static  List<String> print(File file,List<String> resultFileName) {
//        File[] files = file.listFiles();
//        if(files==null)return resultFileName;
//        for (File f : files) {
//            if(f.isDirectory()){
//                print(f,resultFileName);
//            }else
//                resultFileName.add(f.getPath());
//        }
//        return resultFileName;
//    }
    public static String docContext(String addr) throws IOException {
        BufferedReader bufr = new BufferedReader(new InputStreamReader(new FileInputStream(addr)));
        LineNumberReader lnr = new LineNumberReader(bufr);
        String line;
        String doc= null;
        String pattern = "<text>.*</text>";
        Pattern r = Pattern.compile(pattern);
        while ((line = lnr.readLine()) != null) {
            doc=doc+line;
        }
        Matcher m = r.matcher(doc);
        if (m.find()) {
        } else {
            System.out.println("NO MATCH");
        }
        lnr.close();
        return m.group(0);
    }


    public static ArrayList convertVector(String doc_context,String addr,int numOfVectoradd,HashMap<String, Float> idfs,HashMap<String, Float> ttf) throws IOException {
        ArrayList docVector=new ArrayList(Vectorlength-1) ;
        for (int z =0;z<Vectorlength;z++){
            docVector.add(0);}
        BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(addr+numOfVectoradd+".txt")));
        for (int i =0;i<doc_context.length();i++) {
            String charactor=String.valueOf(doc_context.charAt(i));
            if(!stoplist.contains(charactor)){
                try {
                    int x = Integer.parseInt(vocabmap.get(charactor).toString());
//                    System.out.println(x);
                    double dfidf=calDfIdf(charactor,idfs,ttf);
//                    System.out.println(x+":"+dfidf);
                    docVector.set(x,dfidf);
//                    docVector.set(x,1);
                } catch (Exception e) {
//                    System.out.println(e);
                    continue;
                }
            }
        }
//        System.out.println("Vector:"+docVector);
        bufw.write(String.valueOf(docVector));
        bufw.newLine();
        bufw.close();
        return docVector;
    }


    public static ArrayList convertVector(String doc_context,String addr) throws IOException {
        ArrayList docVector=new ArrayList(Vectorlength-1) ;
        for (int z =0;z<Vectorlength;z++){
            docVector.add(0.0);}
        BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(addr+numOfVector+".txt")));
        for (int i =0;i<doc_context.length();i++) {
            String charactor=String.valueOf(doc_context.charAt(i));
            if(!stoplist.contains(charactor)){
                try {
                    int x = Integer.parseInt(vocabmap.get(charactor).toString());
                    docVector.set(x,1.0);
                } catch (Exception e) {
                    continue;
                }
            }
        }
//        System.out.println("Vector:"+docVector);
        bufw.write(String.valueOf(docVector));
        bufw.newLine();
        bufw.close();
        return docVector;
        }

    public static double calDfIdf(String charactor,HashMap<String, Float> idfs,HashMap<String, Float> ttf) throws IOException {
        double idf = idfs.get(charactor);
//        System.out.println(idfs);
//        System.out.println(1);
        double tf = ttf.get(charactor);
//        System.out.println(ttf);
//        System.out.println(2);
        double tfidf=idf*tf;
//        double tfidf=0.0;
        return tfidf;
    }
//        BufferedReader bufr8 = new BufferedReader(new InputStreamReader(new FileInputStream(addrInvertFile)));
//        LineNumberReader lnr8 = new LineNumberReader(bufr8);
//        int N = 2;
//        String line;
//        String doc = null;
//        String pattern ="^"+vocabmap.get(charactor).toString().toString()+" -1"+".*?";
//        Pattern r = Pattern.compile(pattern);
//        int k=0;
//        int tf=0;
//        int lineNumOftf = 0;
//        int k1=0;
//        int k2=0;
//        ArrayList numofk = new ArrayList(0);
//        while ((line = lnr8.readLine())!= null) {
//            Matcher m = r.matcher(line);
//            if (m.find()) {
////                System.out.println("linenum"+lnr8.getLineNumber());
////                System.out.println(charactor);
////                System.out.println(line);
//                lineNumOftf=lnr8.getLineNumber()+1;
////                k =Integer.parseInt(line.substring(vocabmap.get(charactor).toString().length()+4));
////                doc=null;
//                k1++;
////                System.out.println(k1);
//
//            }
//            if(!line.contains("-1")&&(k1==1)){k2++;}
//            if ((k1==1)&&(k2<1)){
////
//                String numlist[]=line.split(" ");
//                if (Integer.parseInt(numlist[0])==numOfVector){
////                    System.out.println(line);
//                    tf=Integer.parseInt(numlist[1]);
//                }
//            }
//
//            if((k2<1)&&(k1==1)){k++;}
//        }
//        double idf= Math.log(N/k);
////        System.out.println("tf:"+tf);
//        System.out.println("idf"+idf);
//        return idf;

    public static ArrayList queryContext(String addr2) throws IOException {
        BufferedReader bufr2 = new BufferedReader(new InputStreamReader(new FileInputStream(addr2)));
        LineNumberReader lnr2 = new LineNumberReader(bufr2);
        String line;
        String doc = null;
        ArrayList q = new ArrayList();
        String pattern = "<narrative>.*?</narrative>";
        Pattern r = Pattern.compile(pattern);
        while ((line = lnr2.readLine())   != null) {
            doc = doc + line;
        }
        Matcher m = r.matcher(doc);
        for (; m.find();) {
            q.add(m.group(0));
        }
        lnr2.close();
        return q;
    }
}