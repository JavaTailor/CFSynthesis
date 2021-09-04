package util;

import dtjvms.DTPlatform;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class RuntimeConfigParser {

    public static ArrayList<String> parse(File file){

        ArrayList<String> config = new ArrayList<>();
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList nl = doc.getElementsByTagName("RuntimeConfig");
            for (int i = 0; i < nl.getLength(); i++) {

                NodeList list = doc.getElementsByTagName("vmoption");
                for (int j = 0; j < list.getLength(); j++){

                    String value = list.item(j).getFirstChild().getNodeValue();
                    if (value.contains("#")){
                        value = value.replace("#", DTPlatform.FILE_SEPARATOR);
                    }
                    value = value.trim();
                    config.add(value);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return config;
    }
}
