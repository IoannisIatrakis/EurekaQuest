package Application;

import java.io.*;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.NumericDocValuesField;

import model.Article;


public class Index {
	
	
    String [] splitted = new String[6];
    BufferedReader bufferreader;
    IndexWriter writer;
	public Index(String PathToStoreIndex) throws IOException {
		File csvReader = new File("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\papers.csv");
	    try {
			 bufferreader = new BufferedReader(new FileReader(csvReader));
			 
			 Directory indexDirectory = FSDirectory.open(Paths.get(PathToStoreIndex));
			 StandardAnalyzer analyzer = new StandardAnalyzer();
			 IndexWriterConfig config = new IndexWriterConfig(analyzer);
	         writer = new IndexWriter(indexDirectory, config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
    
    public  void initializeAnArticle() {
    	String values = "";
        String abstractText="";
        String fullText="";
        String firstLine="";
        int counter=1;
        Document document = new Document();
		
		try {
            
           
            
            while (!(values =  bufferreader.readLine()).equals("\f\"") ) {
            	if (counter ==1 && !values.equals("source_id,year,title,abstract,full_text")) {
            		firstLine= values;
            		splitted =firstLine.split(",");
            		counter++;
            		abstractText+=splitted[4];
            	}else if(counter==2){
            		
            		if(values.contains("Introduction") || values.contains("INTRODUCTION") || values.contains("INTRODUCflON") || values.contains("INTROduCTION")) {
            			fullText+=values;
            			counter++;
            		}else {
            			abstractText +=values;
            		}
            	}else if (!values.equals("source_id,year,title,abstract,full_text")){
            		fullText+=values;		
            	}	
            }
            Article arthro = new Article(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), splitted[2],abstractText,fullText);
            
            document.add(new TextField("source_id", Integer.toString(arthro.getSource_id()), Field.Store.YES));
            document.add(new TextField("yearString", Integer.toString(arthro.getYear()), Field.Store.YES));
            
            // Index as both INT type and NumericDocValuesField for sorting
            document.add(new IntPoint("yearS", Integer.valueOf(arthro.getYear())));
            document.add(new NumericDocValuesField("year", Integer.valueOf(arthro.getYear()))); // for sorting
            
            document.add(new TextField("title", arthro.getTitle(), Field.Store.YES));
            document.add(new TextField("abstract", arthro.getAbstractText(), Field.Store.YES));
            document.add(new TextField("fullText", arthro.getFullText(), Field.Store.YES));

            writer.addDocument(document);

            System.out.println("Indexed document: " + document);

        } catch (IOException e) {
            // Handle the IOException here
            e.printStackTrace();
        }
		
	}
    
    
    public void closeIndexWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	

	public  void initializeMultipleArticle() {
		
		for (int i=0 ; i<1000;i++) {  // how many articles do you want to parse
        	initializeAnArticle() ;
        }
		
	}
	
	
}