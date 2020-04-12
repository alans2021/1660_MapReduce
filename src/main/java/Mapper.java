import com.google.cloud.dataproc.v1.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.*;
import java.util.Random;

public class Mapper {
    private File[] documents;

    private String directory = "comedies";
    private String storageBucket = "gs://dataproc-staging-us-west1-773512836564-eyjwwq8c";
    final int OUTPUT_STRING_LENGTH = 8;

    public Mapper(File[] f){
        documents = f;

        String projectId = "hadoopmapreduceproject-273503"; // project-id of project to create the cluster in
        String region = "us-west1"; // region to create the cluster
        String clusterName = "cluster-fd46"; // name of the cluster
        String jobFilePath = String.format("%s/JAR/invertedindex.jar", storageBucket); // location in GCS of the Hadoop job
        try {
            quickstart(projectId, region, clusterName, jobFilePath);
        }
        catch(IOException e){System.out.println("Something went wrong");}
        catch(InterruptedException e){System.out.println("Execution went wrong");}
    }

    public void quickstart(
            String projectId, String region, String clusterName, String jobFilePath)
            throws IOException, InterruptedException {
        String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);

        try {
            // Configure the settings for the cluster controller client.
            ClusterControllerSettings clusterControllerSettings =
                    ClusterControllerSettings.newBuilder().setEndpoint(myEndpoint).build();

            // Configure the settings for the job controller client.
            JobControllerSettings jobControllerSettings =
                    JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();

            // Create both a cluster controller client and job controller client with the
            // configured settings. The client only needs to be created once and can be reused for
            // multiple requests. Using a try-with-resources closes the client, but this can also be done
            // manually with the .close() method.
            ClusterControllerClient clusterControllerClient =
                    ClusterControllerClient.create(clusterControllerSettings);
            JobControllerClient jobControllerClient =
                    JobControllerClient.create(jobControllerSettings);

            Cluster cluster = clusterControllerClient.getCluster(projectId, region, clusterName);

            // Configure the settings for our job.
            JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(clusterName).build();

            String randomString = getNextRandomString(new Random());
            String input = String.format("%s/%s", storageBucket, directory);
            String output = String.format("%s/output%s", storageBucket, randomString);

            HadoopJob.Builder build = HadoopJob.newBuilder();

            build.addJarFileUris(jobFilePath);
            build.setMainClass("InvertedIndexJob");
            build.addArgs(input);
            build.addArgs(output);
            HadoopJob hadoopJob = build.build();
            Job job = Job.newBuilder().setPlacement(jobPlacement).setHadoopJob(hadoopJob).build();

            // Submit an asynchronous request to execute the job.
            Job request = jobControllerClient.submitJob(projectId, region, job);
            String jobId = request.getReference().getJobId();
            System.out.println(String.format("Submitted job \"%s\"", jobId));

            // Wait for the job to finish.
            CompletableFuture<Job> finishedJobFuture =
                    CompletableFuture.supplyAsync(
                            () -> waitForJobCompletion(jobControllerClient, projectId, region, jobId));
            int timeout = 10;
            try {
                Job jobInfo = finishedJobFuture.get(timeout, TimeUnit.MINUTES);
                System.out.println(String.format("Job %s finished successfully.", jobId));

                System.out.println(
                        String.format("Job \"%s\" finished with state %s:", jobId, jobInfo.getStatus().getState()));
            } catch (TimeoutException e) {
                System.err.println(
                        String.format("Job timed out after %d minutes: %s", timeout, e.getMessage()));
            }


        } catch (ExecutionException e) {
            System.err.println(String.format("Error executing quickstart: %s ", e.getMessage()));
        }
    }


    public Job waitForJobCompletion(
            JobControllerClient jobControllerClient, String projectId, String region, String jobId) {
        while (true) {
            // Poll the service periodically until the Job is in a finished state.
            Job jobInfo = jobControllerClient.getJob(projectId, region, jobId);
            switch (jobInfo.getStatus().getState()) {
                case DONE:
                case CANCELLED:
                case ERROR:
                    return jobInfo;
                default:
                    try {
                        // Wait a second in between polling attempts.
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    private String getNextRandomString(Random random) {

        String strAllowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sbRandomString = new StringBuilder(OUTPUT_STRING_LENGTH);
        for(int i = 0 ; i < OUTPUT_STRING_LENGTH; i++){
            //get random integer between 0 and string length
            int randomInt = random.nextInt(strAllowedCharacters.length());
            //get char from randomInt index from string and append in StringBuilder
            sbRandomString.append( strAllowedCharacters.charAt(randomInt) );
        }

        return sbRandomString.toString();

    }
}
