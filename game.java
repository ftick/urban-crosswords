// Requires JSoup

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class game {
	
	public static int count = 0;
	public static String[] words;
	public static String DIR = ""; // CHANGE TO LOCATION OF FOLDER
	
	public static void main(String[] args) throws IOException {

		words = getList();
		String[] words2 = new20();
		
//		for(int i = 0;i<20;i++) System.out.println(words2[i]);
		
		// Save to file
	    PrintWriter out = new PrintWriter(new FileWriter(DIR+"words20.txt"));
		for(int i = 0;i<20;i++){
			String r = getDef(words2[i]);
			if(r.contains("2."))
				r=r.substring(0,r.indexOf("2."));
			out.println(words2[i]+"|"+r);
		}
		out.close();
		
		try{
            Process p = Runtime.getRuntime().exec("python "+DIR+"/run.py");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.waitFor();
            String line = "";
            while (br.ready())
                System.out.println(br.readLine());
        }catch(Exception e){
        	String cause = e.getMessage();
        	if (cause.equals("python: not found"))
        		System.out.println("No python interpreter found.");
        }
	}
	
	public static void play() throws IOException{
		// Initialize board
		words = getList();
		String[] words2 = new20();
		
		char[][] board = new char[20][20];
		
		// Add first/second word
		for(int i = 0;i<words2[0].length();i++){
			board[0][i] = words2[0].charAt(i);
		}
		for(int i = 0;i<words2[1].length();i++){
			board[6][i+3] = words2[1].charAt(i);
		}
		
		// Next words
		for(int w = 2;w<4;w++){
			for(int i = 0;i<words2[w].length();i++){
				
				ArrayList<Match> matches = new ArrayList<Match>();
				
				// Get Matches
				//// Vertical
				for(int r=0;r<(20-words2[w].length());r++){
					for(int c=0;c<20;c++){
						String onBoard = "";
						int pairs = 0;
						for(int x=0;x<words2[w].length();x++){
							if(words2[w].charAt(x)== board[r+x][c])
								pairs++;
						}
						if(pairs>0){
							Match y = new Match(r,c,pairs,'v');
							matches.add(y);
						}
					}
				}
				//// Horizontal
				for(int r=0;r<20;r++){
					for(int c=0;c<(20-words2[w].length());c++){
						String onBoard = "";
						int pairs = 0;
						for(int x=0;x<words2[w].length();x++){
							if(words2[w].charAt(x)== board[r][c+x])
								pairs++;
						}
						if(pairs>0){
							Match y = new Match(r,c,pairs,'h');
							matches.add(y);
						}
					}
				}
				if(matches.size()>0){
					Match[] matches2 = matches.toArray(new Match[matches.size()]);
					Match selected = matches2[matches2.length-1];
					if(selected.type=='v'){ // Vertical
						for(int c = 0;c<words2[w].length();c++){
							board[selected.row+c][selected.column] = words2[w].charAt(c);
						}
					}else{ // Horizontal
						for(int c = 0;c<words2[w].length();c++){
							board[selected.row][selected.column+c] = words2[w].charAt(c);
						}
					}
				}
			}
		}
		
		// Print board
		for(int r=0;r<20;r++){
			for(int c=0;c<20;c++)
				System.out.print(board[r][c] + "");
			System.out.print("\n");
		}
	}
	
	public static String[] getList() throws IOException{
		String[] ret = new String[list.getCount()];
		BufferedReader in2 = new BufferedReader(new FileReader(DIR+"/words.txt"));
		count=0;
		while(in2.ready()){
			String text = in2.readLine();
			if(text.length()<13 && !text.contains("-")){
				ret[count] = text;
				count++;
			}
		}
		return ret;
	}

	public static String[] new20(){
		String[] ret = new String[20];
		for(int i = 0;i<20;i++){
			Random rn = new Random();
			ret[i] = words[rn.nextInt(count)];
		}
		String temp = "";
		for (int i = 0; i < ret.length-1; i++)
	    {
	        if(ret[i].length() < ret[i+1].length())
	        {
	            temp=ret[i];
	            ret[i]=ret[i+1];
	            ret[i+1]=temp;
	            i=-1;
	        }
	    }
		return ret;
	}
	
	public static String getDef(String term)throws IOException {
        Document doc= Jsoup.connect("http://www.urbandictionary.com/define.php?term="+term).get();
        String ret = doc.select("div.meaning").first().text();
        if(ret.contains(".")){
        	if(ret.indexOf(".")>40)
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
