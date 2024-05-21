package Application;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;


public class Search {
	    public IndexSearcher indexSearcher;
	    private QueryParser queryParser;
	    private MultiFieldQueryParser multiFieldQueryParser;
	    private StandardAnalyzer analyzer = new StandardAnalyzer();
	    private Query query ;
	    
	    
	    
	    public Search(String indexDirectoryPath,String fieldForSearch) throws IOException {
	        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
	        IndexReader indexReader = DirectoryReader.open(indexDirectory);
	        indexSearcher = new IndexSearcher(indexReader);
	        if (fieldForSearch.equals("ALL")) {
	        	String [] fields = {"source_id", "year", "title", "abstract", "fullText"};
	        	multiFieldQueryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
	        
	        }else {
	        	queryParser = new QueryParser(fieldForSearch, new StandardAnalyzer());
	        }   
	    }
	    
	    
	    public String getResults(int docId) throws IOException {
	    	 Document document = indexSearcher.doc(docId);
	    	 	String title = document.get("title");
	    	 	String year = (document.get("yearString"));
	    	 	
		        String abstractText = document.get("abstract");
		        String highlightedTitle = highlightText(query, "title", title);
		        String highlightedAbstract = highlightText(query, "abstract", abstractText);
	    	 
		        return "Title: " + highlightedTitle + "<br>Year: " +year+ "<br>Abstract: " + highlightedAbstract+"<br> <br>";
	         
	    }
	    
	    
	  

	    private String highlightText(Query query, String fieldName, String text) throws IOException {
	        try {
	            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style=\"background-color: yellow;\">","</span>");
	            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

	            TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
	            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);
	            StringBuilder highlightedText = new StringBuilder();
            
	            for (TextFragment textFragment : frag) {
	                if (textFragment != null) {
	                    highlightedText.append(textFragment.toString());
	                }
	            }

	            return highlightedText.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return text; 
	        }
	    }


	    
	    public TopDocs searchAndSortByYear(String queryString, String fieldForSearch) throws ParseException, IOException {
	        if (fieldForSearch.equals("ALL")) {
	            query = multiFieldQueryParser.parse(queryString);
	        } else {
	            query = queryParser.parse(queryString);
	        }

	        Sort sort = new Sort(new SortField("year", SortField.Type.INT, true));
	        TopFieldDocs topFieldDocs = indexSearcher.search(query, 1000, sort);

	        return topFieldDocs;
	    }
	    
	    
	    public TopDocs search(String queryString,String fieldForSearch) throws ParseException, IOException {
	    	if (fieldForSearch.equals("ALL")) {
	    		 query = multiFieldQueryParser.parse(queryString);
		        return indexSearcher.search(query, 1000); 
		        
	        }else {
	        	 query = queryParser.parse(queryString);
		        return indexSearcher.search(query,1000); 
	        }
	        
	    }
}