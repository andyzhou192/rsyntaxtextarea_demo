package com.geekcap.swingx.treetable;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jdesktop.swingx.JXTreeTable;

import com.geekcap.swingx.treetable.json.JsonTreeTableModel;

public class MainFrame {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		MyTreeTableModel treeTableModel = new MyTreeTableModel();
		String jsonStr = "{\"reqHeader\":{\"Host\":\"172.23.29.178:9013\",\"Connection\":\"keep-alive\",\"Upgrade-Insecure-Requests\":\"1\",\"User-Agent\":\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\",\"Accept\":\"text/html,application/xhtml xml,application/xml;q=0.9,image/webp,*/*;q=0.8\",\"Referer\":\"http://172.23.29.183:9014/\",\"Accept-Encoding\":\"gzip, deflate, sdch\",\"Accept-Language\":\"zh-CN,zh;q=0.8\",\"Cookie\":\"JSESSIONID=2A780FA1722E32FFA7FC09E7B516EE69; TGC=eyJhbGciOiJIUzUxMiJ9\"},\"method\":\"GET\",\"reqParams\":{\"service\":\"http://172.23.29.183:9014\"},\"reasonPhrase\":\"Found\",\"rspHeader\":{\"Server\":\"Apache-Coyote/1.1\",\"Cache-Control\":\"no-store\",\"Set-Cookie\":\"CASPRIVACY=\\\"\\\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure\",\"Pragma\":\"no-cache\",\"Expires\":\"Thu, 01 Jan 1970 00:00:00 GMT\",\"Location\":\"http://172.23.29.183:9014\",\"Content-Length\":\"0\",\"Date\":\"Tue, 14 Mar 2017 08:58:53 GMT\"},\"rspBody\":{\"flag\":\"1\",\"msg\":{\"deptName\":\"测试1级部门\",\"namePath\":\"重庆接口测试企业-测试1级部门\",\"id\":\"88445664584798208\",\"usersSize\":4,\"priority\":0,\"idPath\":\"88092735285760000/88445664584798208/\",\"children\":[{\"deptName\":\"测试2级部门a\",\"namePath\":\"重庆接口测试企业-测试1级部门-测试2级部门a\",\"id\":\"88454882066960384\",\"usersSize\":0,\"priority\":0,\"idPath\":\"88092735285760000/88445664584798208/88454882066960384/\",\"children\":[],\"pid\":\"88445664584798208\",\"pinyin\":\"CeShi2JiBuMen1\",\"corpId\":\"88092735210262528\"},{\"deptName\":\"测试2级部门c\",\"namePath\":\"重庆接口测试企业-测试1级部门-测试2级部门c\",\"id\":\"88455725050761216\",\"usersSize\":0,\"priority\":0,\"idPath\":\"88092735285760000/88445664584798208/88455725050761216/\",\"children\":[],\"pid\":\"88445664584798208\",\"pinyin\":\"CeShi2JiBuMenc\",\"corpId\":\"88092735210262528\"},{\"deptName\":\"测试2级部门b\",\"namePath\":\"重庆接口测试企业-测试1级部门-测试2级部门b\",\"id\":\"88452468383420416\",\"usersSize\":2,\"priority\":0,\"idPath\":\"88092735285760000/88445664584798208/88452468383420416/\",\"children\":[],\"pid\":\"88445664584798208\",\"pinyin\":\"CeShi2JiBuMen\",\"corpId\":\"88092735210262528\"}],\"pid\":\"88092735285760000\",\"pinyin\":\"CeShiBuMen\",\"corpId\":\"88092735210262528\"}},\"url\":\"172.23.29.178:9013/logout\",\"statusCode\":300}";
		JsonTreeTableModel treeTableModel = new JsonTreeTableModel(jsonStr);
		JXTreeTable treeTable = new JXTreeTable( treeTableModel );
		int rowIndex = treeTable.getSelectedRow();
		treeTable.addTreeSelectionListener(new TreeSelectionListener(){
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println(e.getPath());
			}
			
		});
		System.out.println("Row Count : " + treeTable.getRowCount() + "; Selected Row Num is : " + rowIndex);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(treeTable);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setSize(800, 600);
		//frame.pack();
	}
}
