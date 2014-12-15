package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {

	public static void Log(String text, boolean n) {
		try {
			File filedir = new File("res/Log");
			filedir.mkdirs();
			File file = null;
			
			if(n){
				int num = filedir.listFiles().length+1;
				if(num == 1)
					file = new File("res/Log/Log.txt");
				else
					file = new File("res/Log/Log "+num+".txt");
			}else{
				int num = filedir.listFiles().length;
				if(num < 2)
					file = new File("res/Log/Log.txt");
				else
					file = new File("res/Log/Log "+num+".txt");	
			}
		    FileWriter fw = new FileWriter(file,true);
		    fw.write(text+"\n");
		    fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		while(true){
			Log("java.lang.ArrayIndexOutOfBoundsExceptionjava.lang.ArrayIndexOutOfBoundsExceptionjava.lang.ArrayIndexOutOfBoundsExceptionjava.lang.ArrayIndexOutOfBoundsExceptionjava.lang.ArrayIndexOutOfBoundsExceptionjava.lang.ArrayIndexOutOfBoundsException", false);
		}
	}
}
