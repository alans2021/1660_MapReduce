import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.*;

public class InvertedIndexJob{
	public static class Map extends Mapper<LongWritable, Text, Text, Text>{
		private Text word = new Text();
		
		@Override
		public void map(LongWritable key, Text value, Context context) 
			throws IOException, InterruptedException{
			StringTokenizer itr = new StringTokenizer(value.toString());
			String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
			int hashCode = fileName.hashCode();
			Text docID = new Text(fileName);
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken().toLowerCase());
				context.write(word, docID);
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
	        
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException {
       			HashMap<String, Integer> map = new HashMap<String, Integer>();
	   		for (Text fileID : values) {
				String file = fileID.toString();				
				if(map.get(file) == null)
					map.put(file, 1);
				else
					map.put(file, map.get(file) + 1);
			}
			String mapString = map.toString();
			context.write(key, new Text(mapString));
		}
	}

	public static void main(String[] args)
       		throws IOException, ClassNotFoundException, InterruptedException{

		if (args.length != 2){
			System.err.println("Usage: Word Count <input path> <output path>");
			System.exit(-1);
		}

		Job job = new Job();
		job.setJarByClass(InvertedIndexJob.class);
		job.setJobName("Inverted Index Job");
   	
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.waitForCompletion(true);
	}
}
