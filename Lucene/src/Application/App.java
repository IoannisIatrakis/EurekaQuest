package Application;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;

import model.Article;


public class App {
	
	
    String [] splitted = new String[6];
    BufferedReader bufferreader;
    IndexWriter writer;
	public App(String PathToStoreIndex) throws IOException {
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
            		
            		if(values.contains("Introduction") || values.contains("INTRODUCTION") || values.contains("INTRODUCflON")) {
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
            document.add(new TextField("year", Integer.toString(arthro.getYear()), Field.Store.YES));
            document.add(new TextField("title", arthro.getTitle(), Field.Store.YES));
            document.add(new TextField("abstract", arthro.getAbstractText(), Field.Store.YES));
            document.add(new TextField("fullText", arthro.getFullText(), Field.Store.YES));
            
            // Index the document using Lucene IndexWriter
            // Assuming you have an IndexWriter initialized elsewhere
            writer.addDocument(document);

            System.out.println("Indexed document: " + document);
            //System.out.println("Indexed Writer: " + writer);
            
            
           // System.out.println("firstLine : " + firstLine);
            //System.out.println("values : " + values);
            //System.out.println("abstractText : " + abstractText);
           // System.out.println("abstractText : " + arthro.getAbstractText());
           // System.out.println("fullText : " + arthro.getFullText());
            
            //bufferreader.close(); // Close the reader after use
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
		
		for (int i=0 ; i<3;i++) {
        	initializeAnArticle() ;
        }
		
	}
	
	
	
    public static void main(String[] args) throws IOException, ParseException {
    	App app = new App("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index");
    	app.initializeMultipleArticle();
    	app.closeIndexWriter();
    	Search search = new Search("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index","ALL");
    	
    	
    
    	TopDocs topDocs = search.search("A*","ALL");
    	System.out.println("Total hits: " + topDocs.totalHits.value);
        
        // Iterate over the ScoreDoc array within TopDocs
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            float score = scoreDoc.score;
            
            // Print the document ID and score
            System.out.println("Document ID: " + docId + ", Score: " + score);
        }
        
    }
    
}


