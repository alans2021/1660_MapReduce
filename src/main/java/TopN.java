import java.util.*;

public class TopN {

    private int limit;
    private String[] columns = {"Term", "Total Frequencies"};
    private String[][] data;
    private HashMap<String, Integer> totalCounts = new HashMap<>();

    public TopN(int x, String[] dataFiles){
        limit = x;
        data = new String[limit][x];
        PostingList postingList = new PostingList(dataFiles);

        Iterator iterator = postingList.getPostingMap().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            String word = (String) pair.getKey();
            HashMap<String, Integer> counts = (HashMap<String, Integer>) pair.getValue();
            Iterator countIterator = counts.entrySet().iterator();
            int count = 0;

            while(countIterator.hasNext()){
                Map.Entry countPair = (Map.Entry) countIterator.next();
                count += (int) countPair.getValue();
            }
            totalCounts.put(word, count);
        }

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
        HashMap<String, Integer> sortedTotalCounts = sortByValue(totalCounts);
        Iterator iterator = sortedTotalCounts.entrySet().iterator();

        for(int row = 0; row < data.length; row++){
            Map.Entry pair = (Map.Entry) iterator.next();
            data[row][0] = (String) pair.getKey();
            data[row][1] = Integer.toString((int) pair.getValue());
        }
    }

    public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
