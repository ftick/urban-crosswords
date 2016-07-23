// Requires JSoup

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class list {

	public static int count = 0;
	public static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static String DIR = ""; // CHANGE TO LOCATION OF FOLDER
	
	public static void main(String[] args) throws IOException{
		
//		doStuff();
		
		// Fill array
		String[] words = new String[getCount()];
		BufferedReader in2 = new BufferedReader(new FileReader(DIR+"/words.txt"));
		count=0;
		while(in2.ready()){
			String text = in2.readLine();
			words[count] = text.toLowerCase();
			count++;
		}
		
		// Sort array
		sort(words);
	}
	
	public static void sort (String [] arrayName) throws IOException{
	    String temp;
	    // Sorting
	    for (int i = 0; i < arrayName.length-1; i++)
	    {
	        if(arrayName[i].length() < arrayName[i+1].length())
	        {
	            temp=arrayName[i];
	            arrayName[i]=arrayName[i+1];
	            arrayName[i+1]=temp;
	            i=-1;
	        }
	    }
	    // Put it back
	    PrintWriter out2 = new PrintWriter(new FileWriter(DIR+"/words.txt"));
		for(int i = 0;i<count;i++){
			out2.println(arrayName[i]);
		}
	}
	
	public static void doStuff() throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(DIR+"/initial.txt")); 
		
		for(char chr: alphabet)
			out.println(removeHTML(getList(chr + "")));
		
		removePrefixes();
		
		out.close();
	}
	
	public static void removePrefixes() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(DIR+"/initial.txt")); 
		PrintWriter out = new PrintWriter(new FileWriter(DIR+"/words.txt"));
		while (in.ready()) {
			  String text = in.readLine();
			  if(text.length()>0 && !text.contains(" ")){
				  text = text.substring(text.indexOf(">")+1);
				  out.println(text);
				  count++;
			  }
		}
		out.close();
		in.close();
	}
	
	public static int getCount() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(DIR+"/words.txt"));
		int count = 0;
		while (in.ready()) { 
			  String text = in.readLine();
			  count++;
		}
		in.close();
		return count;
	}
	
	public static String getList(String chr)throws IOException {
        Document doc= Jsoup.connect("http://www.urbandictionary.com/popular.php?character="+chr.toUpperCase()).get();
        return "\n" + doc.select("ul.no-bullet").html();
    }

	public static String removeHTML(String list){
		String ret = list.replace("<li> <a class=\"popular\" href=\"/define.php?term=", "");
		ret = ret.replace("<li> <a href=\"/tos.php\">terms of service</a> </li>","");
		ret = ret.replace("<li> <a href=\"/privacy.php\">privacy</a> </li>","");
		ret = ret.replace("<li> <a href=\"https://urbandictionary.wufoo.com/forms/how-can-we-help-you/\">feedback</a> </li>","");
		ret = ret.replace("<li> <a href=\"/remove.php\">remove</a> </li>","");
		
		ret = ret.replace("</a> </li> ","");
		
		ret = ret.substring(0,ret.length()-16);
		ret = ret.substring(0,ret.length()-1);
		
		return ret;
	}

	public static String getDef(String term)throws IOException {
        Document doc= Jsoup.connect("http://www.urbandictionary.com/define.php?term="+term).get();
        String ret = doc.select("div.meaning").first().text();
        if(ret.contains(".")){
        	if(ret.indexOf(".")>9)
        		ret = ret.substring(0, ret.indexOf(".")+1);
        }
        if(ret.contains(term))
        	ret = ret.replace(term, "----");
        String caps = term.substring(0,1).toUpperCase();
        caps = caps + term.substring(1);
        if(ret.contains(caps))
        	ret = ret.replace(caps, "----");
        return ret;
    }
	
}
