package csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSVHelper {

	public CSVHelper() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String[] PARAM_NAMES = {"CaseID", "CaseDesc", "Method", "URL","ReqHeader","ReqParams","StatusCode","ReasonPhrase","RspHeader","RspBody"};
		String inStr = "";
		String tmpStr = "";
		File inFile = new File("src/csv/in.csv");
		File outFile = new File("src/csv/out.csv");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			CsvReader creader = new CsvReader(reader, ',');
			CsvWriter cwriter = new CsvWriter(writer, ',');
			while(creader.readRecord()){
				inStr = creader.getRawRecord(); // 读取一行数据
				for(int i =0; i < PARAM_NAMES.length; i++){
					tmpStr = inStr.replace(PARAM_NAMES[i], "," + PARAM_NAMES[i] + ",");
					inStr = tmpStr;
				}
				// 第一个参数表示要写入的字符串数组，每个元素占一个单元格；
				// 第二个参数为true表示写完数据自动换行
				cwriter.writeRecord(inStr.split(","), true);
				// 注意，此时再用cwriter.write(inStr)方法写入数据将会看到只往第一个单元格写入数据，“,”没起到调到下一个单元格的作用
				// 如果用cwriter.write(String str)方法来写数据，则要用cwriter.endRecord()方法来实现换行
				//cwriter.endRecord();
				cwriter.flush(); // 刷新数据
			}
			creader.close();
			cwriter.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
