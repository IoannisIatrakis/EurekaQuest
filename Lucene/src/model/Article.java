package model;

public class Article {
	
	
	private int source_id;
	private int year;
	private String title;
	private String abstractText;
	private String fullText;





	public Article(int source_id, int year, String title, String abstractText, String fullText) {
		this.source_id = source_id;
		this.year = year;
		this.title = title;
		this.abstractText = abstractText;
		this.fullText = fullText;
	}



	public int getSource_id() {
		return source_id;
	}





	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}





	public int getYear() {
		return year;
	}





	public void setYear(int year) {
		this.year = year;
	}





	public String getTitle() {
		return title;
	}





	public void setTitle(String title) {
		this.title = title;
	}





	public String getAbstractText() {
		return abstractText;
	}





	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}





	public String getFullText() {
		return fullText;
	}





	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	
	


}
