import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;

import java.util.HashMap;

public class PostingList {
    private String[] directoryList;
    private String storageBucket = "dataproc-staging-us-west1-773512836564-eyjwwq8c";
    private String projectId = "hadoopmapreduceproject-273503";
    private HashMap<String, HashMap<String, Integer>> postingMap = new HashMap<>();
    private Storage storage;

    public PostingList(String[] f){
        directoryList = f;
        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        setOutputData();
    }

    public HashMap<String, HashMap<String, Integer>> getPostingMap() {
        return postingMap;
    }

    private void setOutputData(){
        //Get Bucket with data files and output
        Bucket bucket = storage.get(storageBucket);
        Page<Blob> blobs = bucket.list();

        for(String directory: directoryList) {
            StringBuilder sb = new StringBuilder();
            for (Blob blob : blobs.iterateAll()) {
                //Get all output files info and consolidate them to a stringbuilder
                if(blob.getName().contains("output" + directory.hashCode() + "/part")){
                    byte[] invertedIndexing = blob.getContent();
                    String stringComp = new String(invertedIndexing);
                    sb.append(stringComp);
                }
            }
            //Split string by new line
            String directoryCounts = new String(sb);
            String[] wordPostings = directoryCounts.split("\n");

            for(String posting: wordPostings){
                String[] entry = posting.split("\t");
                String word = entry[0];
                entry[1] = entry[1].substring(1, entry[1].length() - 1);
                String[] docCounts = entry[1].split(", ");
                HashMap<String, Integer> doc_map;
                //If word doesn't exist on hashmap yet
                if(postingMap.get(word) == null){
                    //Create new entry
                    doc_map = new HashMap<>();
                    for(String docCount : docCounts){
                        //For each indexing, add entry
                        String[] docCountComp = docCount.split("=");
                        doc_map.put(directory + "/" + docCountComp[0], Integer.parseInt(docCountComp[1]));
                    }
                }
                else{
                    //If it already exists, just get the hashmap
                    doc_map = postingMap.get(word);
                    for(String docCount: docCounts){
                        String[] docCountComp = docCount.split("=");
                        Integer count = doc_map.get(docCountComp[0]);
                        //If that file already exists, have to add the counts
                        if(count != null) {
                            count += Integer.parseInt(docCountComp[1]);
                            doc_map.put(directory + "/" + docCountComp[0], count);
                        }
                        else
                            doc_map.put(directory + "/" + docCountComp[0], Integer.parseInt(docCountComp[1]));
                    }
                }
                postingMap.put(word, doc_map);
            }
        }
        System.out.println();
    }


}
