import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

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
        // Connect to GCP Storage
        Storage storage = StorageOptions.getDefaultInstance().getService();

        //Get Bucket with data files and output
        Page<Bucket> buckets = storage.list();
        Iterable<Bucket> bucks = buckets.iterateAll();
        for (Bucket bucket : bucks) {
            //Get bucket objects
            Page<Blob> blobs = bucket.list();

            //Look for object that is output.txt
            for (Blob blob : blobs.iterateAll()) {
                if(blob.getBlobId().getName().equals("output.txt")){
                    //Get contents of output.txt and print out
                    byte[] data = blob.getContent();
                    System.out.println(new String(data));
                }
            }
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
