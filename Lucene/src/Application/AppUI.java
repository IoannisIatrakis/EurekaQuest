package Application;


import java.awt.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


public class AppUI extends JFrame {
    String query = "";
    int resultNum = 1;
    String temp = "";
    int listIndex = 0;
    private static JTextField searchField;
    private static JTextPane resultsArea;
    private String selectedSearchType = "title";
    JMenuBar menubar;
    JMenu history;
    JMenuItem searchHistoryItem;
    ArrayList<String> searchHistory = new ArrayList<>();
    ArrayList<String[]> TenResults = new ArrayList<>();

    public AppUI() {
        setTitle("Search Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        JLabel searchLabel = new JLabel();
        searchField = new JTextField(20);
        JComboBox searchType = new JComboBox<>(new String[]{"title", "abstract", "fullText", "ALL"});
        JButton searchButton = new JButton("Search");
        resultsArea = new JTextPane();
        resultsArea.setContentType("text/html");
        resultsArea.setEditable(false);
        menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        history = new JMenu("History");
        menubar.add(history);

        searchHistoryItem = new JMenuItem();

        searchField.setPreferredSize(new Dimension(200, 30));
        searchButton.setPreferredSize(new Dimension(80, 30));

        Container container = getContentPane();
        container.setLayout(new BorderLayout(10, 10));
        container.add(searchLabel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.add(searchType,BorderLayout.WEST);

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton backwardButton = new JButton("Backward");
        JButton forwardButton = new JButton("Forward");
        JButton sortButton = new JButton("Sort by Year");
        buttonPanel.add(sortButton);
        buttonPanel.add(backwardButton);
        buttonPanel.add(forwardButton);
        container.add(buttonPanel, BorderLayout.SOUTH);
        

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TenResults.clear();
                listIndex = 0;
                resultNum = 1;
                String query = "";
                try {
                    query = performSearch(selectedSearchType);
                    if (!query.equals("Enter your Query")) {
                        searchHistory.add(query);
                        
                        searchHistoryItem = new JMenuItem(query);
                        history.insert(searchHistoryItem, 0);

                        if (searchHistory.size() > 5) {
                            history.remove(5);
                            searchHistory.remove(5);
                        }
                        searchHistoryItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                searchField.setText(e.getActionCommand());
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listIndex < TenResults.size() - 1) {
                    listIndex++;
                    forwards_backwards(listIndex);
                }
            }
        });

        backwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listIndex > 0) {
                    listIndex--;
                    forwards_backwards(listIndex);
                }
            }
        });
        
     // Sort by year
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TenResults.clear();
                listIndex=0;
                resultNum = 1;
                try {
                    //String query = searchField.getText();
                    sortedPerformSearch(selectedSearchType);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        searchType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedSearchType = (String) searchType.getSelectedItem();
            }
        });
        
        
    }
    
    public String sortedPerformSearch(String selectedSearchType) throws IOException, ParseException {
        int counter = 0;
        if (!searchField.getText().equals("")) {
            query = searchField.getText();

            String result = "";
            String[] res = new String[10];
            Search search = new Search("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index", selectedSearchType);

            TopDocs topDocs = search.searchAndSortByYear(query, selectedSearchType);
            System.out.println("Total hits: " + topDocs.totalHits.value);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docId = scoreDoc.doc;
                float score = scoreDoc.score;
                result = search.getResults(docId) + "\n";

                if (counter < 10) {
                    res[counter] = result;
                    counter++;
                } else {
                    TenResults.add(res.clone());
                    Arrays.fill(res, null);
                    res[0] = result;
                    counter = 1;
                }

                System.out.println("Document ID: " + docId + ", Score: " + score);
            }

            if (counter != 0) {
                TenResults.add(Arrays.copyOf(res, counter));
            }

            displayResults();
            return query;
        }
        resultsArea.setText("Please do not enter empty query");
        return "Enter your Query";
    }

    public String performSearch(String selectedSearchType) throws IOException, ParseException {
        int counter = 0;
        if (!searchField.getText().equals("")) {
            query = searchField.getText();

            String result = "";
            String[] res = new String[10];
            Search search = new Search("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index", selectedSearchType);

            TopDocs topDocs = search.search(query, selectedSearchType);
            System.out.println("Total hits: " + topDocs.totalHits.value);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docId = scoreDoc.doc;
                float score = scoreDoc.score;
                result = search.getResults(docId) + "\n";

                if (counter < 10) {
                    res[counter] = result;
                    counter++;
                } else {
                    TenResults.add(res.clone());
                    Arrays.fill(res, null);
                    res[0] = result; 
                    counter = 1;
                }

                System.out.println("Document ID: " + docId + ", Score: " + score);
            }

            if (counter != 0) {
                TenResults.add(Arrays.copyOf(res, counter));
            }

            displayResults();
            return query;
        }
        resultsArea.setText("Please do not enter empty query");
        return "Enter your Query";
    }

    public void displayResults() {
        temp = "Search results for: " + query + "<br>";
        if (!TenResults.isEmpty()) {
            for (String results : TenResults.get(listIndex)) {
                if (results != null) {
                    temp = temp + "\n" + resultNum + ". " + results + "\n";
                    resultNum++;
                }
            }
            resultsArea.setText(temp);
        } else {
        	temp = temp + "\nThere are no results for this search. \n";
            resultsArea.setText(temp);
        }
    }

    public void forwards_backwards(int listIndex) {
        temp = "Search results for: " + query + "<br>";
        resultNum = listIndex * 10 + 1;
        if (listIndex < TenResults.size()) {
            for (String results : TenResults.get(listIndex)) {
                if (results != null) {
                    temp = temp + "\n" + resultNum + ". " + results + "\n";
                    resultNum++;
                }
            }
            resultsArea.setText(temp);
        } else {
            resultsArea.setText(temp); 
        }
    }

}
