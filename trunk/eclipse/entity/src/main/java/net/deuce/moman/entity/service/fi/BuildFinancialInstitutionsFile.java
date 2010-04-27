package net.deuce.moman.entity.service.fi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.deuce.moman.entity.model.impl.EntityFactoryImpl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class BuildFinancialInstitutionsFile {
	
	private File file;
	
	public BuildFinancialInstitutionsFile(String filename) {
		file = new File(filename);
		if (!file.exists()) {
			System.err.println("File does not exist: " + filename);
			System.exit(-1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			String name;
			String url;
			String fid=null;
			String org;
			
			Document doc = DocumentHelper.createDocument();
			doc.addElement("moman");
			Element root = doc.getRootElement().addElement("financialInstitutions");
			Element el;
			
			while ( (line = reader.readLine()) != null ) {
				String[] split = line.split(",");
				name = split[0];
				url = split[1];
				try {
				fid = split[2];
				} catch (Exception e) {
					e.printStackTrace();
				}
				org = split[3];
				
				el = root.addElement("financialInstitution");
				el.addAttribute("id", new EntityFactoryImpl().createUuid());
				el.addElement("name").setText(name.replaceAll("%COMMA%", ","));
				el.addElement("url").setText(url.replaceAll("%COMMA%", ","));
				el.addElement("fid").setText(fid.replaceAll("%COMMA%", ","));
				el.addElement("org").setText(org.replaceAll("%COMMA%", ","));
				
			}
			
			
			OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = null;
			writer = new XMLWriter(System.out, format);
			writer.write(doc);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: BuildFinancialInstitutionsFile url-info-file.csv");
			System.exit(-1);
		}
		try {
			new BuildFinancialInstitutionsFile(args[0]).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
