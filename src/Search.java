public class Search {
    private String[] columnNames = {"Doc ID", "Doc Folder", "Doc Name", "Frequencies"};
    private String[][] data = new String[5][];
    private String searchTerm;
    private long startTime;

    public Search(String x){
        startTime = System.currentTimeMillis();
        searchTerm = x;
        for(int i = 0; i < data.length; i++){
            data[i] = new String[4];
            for(int j = 0; j < data[i].length; j++)
                data[i][j] = Integer.toString(i) + ", " + Integer.toString(j);
        }
    }

    public String getTerm(){
        return searchTerm;
    }

    public long getElapsedTime(){
        return System.currentTimeMillis() - startTime;
    }

    public String[] getColumnNames(){
        return columnNames;
    }

    public String[][] getData(){
        return data;
    }
}
