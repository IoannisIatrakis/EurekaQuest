package Application;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {
    
	    
	    private IndexSearcher indexSearcher;
	    private QueryParser queryParser;
	    private MultiFieldQueryParser multiFieldQueryParser;
	    
	    public Search(String indexDirectoryPath,String fieldForSearch) throws IOException {
	        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
	        IndexReader indexReader = DirectoryReader.open(indexDirectory);
	        indexSearcher = new IndexSearcher(indexReader);
	        System.out.println("fieldForSearch: " +fieldForSearch);
	        if (fieldForSearch.equals("ALL")) {
	        	String [] fields = {"source_id", "year", "title", "abstract", "fullText"};
	        	multiFieldQueryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
	        
	        }else {
	        	queryParser = new QueryParser(fieldForSearch, new StandardAnalyzer());
	        }
   
	    }
	    
	    public TopDocs search(String queryString,String fieldForSearch) throws ParseException, IOException {
	    	if (fieldForSearch.equals("ALL")) {
	    		Query query = multiFieldQueryParser.parse(queryString);
		        return indexSearcher.search(query, 10); 
		        
	        }else {
	        	Query query = queryParser.parse(queryString);
		        return indexSearcher.search(query, 10); 
	        }
	        
	    }
	    
	    public TopDocs BooleanSearch(String queryString,String fieldForSearch) {
	    	
	    	if (!queryString.contains("AND") || !queryString.contains("OR") || !queryString.contains("NOT" )) {
	    		
	    		return null;
	    	}
			return null;
	    	
	    }
	}