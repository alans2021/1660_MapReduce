public class TopN {

    private int limit;
    private String[] columns = {"Term", "Total Frequencies"};
    private String[][] data;

    public TopN(int x){
        limit = x;
        data = new String[limit][x];
    }

    public int getLimit() {
        return limit;
    }

    public String[] getColumns(){
        return columns;
    }

    public String[][] getData(){
        return data;
    }

    public void setData(){
        for(int row = 0; row < data.length; row++){
            for(int col = 0; col < data[row].length; col++)
                data[row][col] = Integer.toString((int) (10 * Math.random() + 1));
        }
    }
}
