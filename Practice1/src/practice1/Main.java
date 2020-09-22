package practice1;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.Buffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.zip.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//TODO Если не url то return
//TODO Разобраться почему не создаёт папки

public class Main {

    public static void download(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
    public static void getWhl(String pathF, String urlF, String pkgNameF, SAXParser parserF) throws IOException, SAXException {
        download(urlF, pathF+pkgNameF+".html"); //path of download

        XMLHandler handler = new XMLHandler();
        parserF.parse(new File(pathF+pkgNameF+".html"), handler);//parse html with versions of package
        //Download archive with package
        download(handler.urlToPackage, pathF+pkgNameF+".whl");
    }
    //build graph
    public static void getGraph(String pkgNameF, String strF){
        System.out.println("\"" + pkgNameF + "\"" + "->" + "\"" + strF+ "\"");
    }
    //add addittionalDepends
    public static void addittionalDepends(String pkgNameF, ArrayList<String> dependF, String pathF, String urlF, SAXParser parserF) throws IOException, SAXException {
            for(int i = 0; i < dependF.size(); i++) {
                getWhl(pathF, urlF + dependF.get(i), dependF.get(i), parserF);

                File unpackedZipDirF = new File(pathF+dependF.get(i));//create new directory to unpacked Zip
                unpackedZipDirF.mkdir();
                File zipArchiveF = new File(pathF+dependF.get(i)+".whl");
                ZipUtils.extract(zipArchiveF, unpackedZipDirF);

                String metaPath = null;
                for(String j : unpackedZipDirF.list()){
                    if(j.contains("dist-info"))
                        metaPath = j + "/METADATA";
                }
                metaPath = pathF + dependF.get(i)+'/'+ metaPath;

                File metaData = new File (metaPath);
                String str = null;
                ArrayList <String> dependTmp = new ArrayList<String >(); // for stroage depends of root package
                Scanner scanner = new Scanner(metaData);
                while (scanner.hasNextLine()){
                    str = scanner.nextLine();
                    if(str.contains("Requires-Dist") && !str.contains("extra")){
                        str = str.substring(str.indexOf(' ')+1, str.length());
                        if(str.indexOf(' ')!=-1)
                            str = str.substring(0,str.indexOf(' '));
                        getGraph(dependF.get(i), str);
                        addittionalDepends(str, dependTmp, pathF, urlF, parserF);
                        dependTmp.add(str);
                        //System.out.println(str);
                    }
                }

            }
    }
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        Scanner sc = new Scanner(System.in);
        System.out.print("Please input name of package: ");
        String pkgName = sc.nextLine();
        String path = "/home/danila/MEGAsync/Institute/3-rd_semestr/ConfiguralManagement/Practice1_v2/depends/";
        String url = "https://pypi.org/simple/";
        //Download xml with versions and download archive with METADATA
        getWhl(path, url + pkgName,pkgName, parser);
        //Unpacking archive
        File unpackedZipDir = new File(path+pkgName);//create new directory to unpacked Zip
        unpackedZipDir.mkdir();
        File zipArchive = new File(path+pkgName+".whl");
        ZipUtils.extract(zipArchive, unpackedZipDir);

        //write path to METADATA
        String metaPath = null;
        for(String i : unpackedZipDir.list()){
            if(i.contains("dist-info"))
                metaPath = i + "/METADATA";
        }
        metaPath = path + pkgName+'/'+ metaPath;

        //read dependences from METADATA
        System.out.println("digraph G{");
        File metaData = new File (metaPath);
        String str = null;
        ArrayList <String> depend = new ArrayList<String >(); // for stroage depends of root package
        Scanner scanner = new Scanner(metaData);
        while (scanner.hasNextLine()){
            str = scanner.nextLine();
            if(str.contains("Requires-Dist") && !str.contains("extra")){
                str = str.substring(str.indexOf(' ')+1, str.length());
                if(str.indexOf(' ')!=-1)
                    str = str.substring(0,str.indexOf(' '));
                getGraph(pkgName, str);
                depend.add(str);
               //System.out.println(str);
            }
        }

        addittionalDepends(pkgName, depend, path, url, parser);
        System.out.print('}');
    }

}
