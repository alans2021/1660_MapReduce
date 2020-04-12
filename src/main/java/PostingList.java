import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class PostingList {
    private String[] directoryList;
    private Storage storage;

    public PostingList(String[] f){
        directoryList = f;
        storage = StorageOptions.getDefaultInstance().getService();

        setOutputData();
    }

    private void setOutputData(){
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


}
