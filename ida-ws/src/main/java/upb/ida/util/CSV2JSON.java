package upb.ida.util;

import java.io.*;
import java.util.*;
import java.io.File;
import java.io.InputStream;


public class CSV2JSON {


    public static void main(String[] args) throws Exception {

        File output = new File("C:\\Users\\Faisal Mahmood\\Desktop\\dice-ida\\ida-ws\\src\\main\\java\\upb\\ida\\util\\input.csv");
        
        InputStream in = new FileInputStream(output);
        //InputStream in = new ByteArrayInputStream(output.getBytes("UTF-8"));

        jsonMaker lst= new jsonMaker(in);
        List <Map< String, String >> lstt = lst.jsonObject(in);

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("What is the x-axis name: \n ");
        String x = reader.next();
        System.out.println("What is the y-axis name: \n ");
        String y = reader.next();
        reader.close();

        getAxisJson jsn= new getAxisJson(x,y);

        Object p[];
        p= jsn.NewJsonObjct(x,y,lstt);
        System.out.println("x-axis:"+x);
        System.out.println("y-axis:"+y);
        System.out.println(p[0]);
        System.out.println(p[1]);




    }

}
