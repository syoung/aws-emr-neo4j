import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Session;
import org.neo4j.driver.GraphDatabase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PreprocessingMapper extends Mapper<Object, Text, Text, Text> {

  private static final String CSV_DELIMITER = ",";

  private Driver driver;

  public static void main(String[] args) throws Exception {

    Configuration conf = new Configuration();

    // Get the input and output paths from the arguments
    String inputPath = args[0];
    String outputPath = args[1];

    // Create a Hadoop job
    Job job = Job.getInstance(conf, "PreprocessingJob");
    job.setJarByClass(PreprocessingMapper.class);
    job.setMapperClass(PreprocessingMapper.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(Text.class);

    // Set the input and output paths
    FileInputFormat.addInputPath(job, new Path(inputPath));
    FileOutputFormat.setOutputPath(job, new Path(outputPath));

    // Run the Hadoop job
    job.waitForCompletion(true);
  }

  @Override
  protected void setup(Context context) throws IOException {
    String uri = "bolt://localhost:7687";
    String user = "neo4j";
    String password = "password";
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

    String inputPath = context.getConfiguration().get("input");
    String outputPath = context.getConfiguration().get("output");

    // print the input and output paths
    System.out.printf( "input path: %s\n", inputPath);
    System.out.printf( "output path: %s\n", outputPath);
    printFile();
  }

  @Override
  protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    List<String> columns = Arrays.asList(value.toString().split(CSV_DELIMITER));

    // Process the data here...

    // Upload the CSV file to the Neo4j database
    String transaction_id = columns.get(0);
    String amount = columns.get(1);
    String is_fraudulent = columns.get(2);

    try (Session session = driver.session()) {

       Map<String, Object> parameters = new HashMap<>();
       parameters.put("transaction_id", transaction_id);
       parameters.put("amount", amount);
       parameters.put("is_fraudulent", is_fraudulent);

       session.run("CREATE (transaction:Transaction {transaction_id: $transaction_id, amount: $amount, is_fraudulent: $is_fraudulent})",
           parameters
       );
    }
  }

  @Override
  protected void cleanup(Context context) throws IOException {
    driver.close();
  }
  public static void printFile() throws IOException
  {
    // Open the file.
    PrintWriter out = new PrintWriter("oceans.txt"); // Step 2

    // Write the name of four oceans to the file
    out.println("Atlantic");   // Step 3
    out.println("Pacific");
    out.println("Indian");
    out.println("Arctic");

    // Close the file.
    out.close();  // Step 4
  }

}
