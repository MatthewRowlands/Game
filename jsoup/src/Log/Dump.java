package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Dump {
	public static void Dump(String text){
		try {
			File filedir = new File("res/Err");
			filedir.mkdirs();
			int num = 23957+filedir.listFiles().length+1;
			File file = new File("res/Err/has_err_pid "+num+".txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
