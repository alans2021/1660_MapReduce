import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Search {
    private String[] columnNames = {"Doc ID", "Doc Folder", "Doc Name", "Frequencies"};
    private String[][] data;
    private String searchTerm;
    private long startTime;

    public Search(String x, HashMap<String, HashMap<String, Integer>> map){
        startTime = System.currentTimeMillis();
        searchTerm = x;
        HashMap<String, Integer> list = map.get(x);
        Iterator iterator = list.entrySet().iterator();
        data = new String[list.size()][];

        int i = 0;
        while(iterator.hasNext()){
            data[i] = new String[4];
            Map.Entry pair = (Map.Entry) iterator.next();
            String key = (String) pair.getKey();
            data[i][0] = Integer.toString(pair.getKey().hashCode());
            data[i][1] = key.split("/")[0];
            data[i][2] = key.split("/")[1];
            data[i][3] = Integer.toString((int) pair.getValue());
            i++;
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
