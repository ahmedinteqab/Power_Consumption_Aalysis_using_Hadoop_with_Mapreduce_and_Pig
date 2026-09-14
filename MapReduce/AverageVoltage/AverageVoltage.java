import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AverageVoltage {

    public static class VoltageMapper
            extends Mapper<LongWritable, Text, Text, DoubleWritable> {

        private static final Text KEY = new Text("Average_Voltage");
        private DoubleWritable voltage = new DoubleWritable();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            // Skip header
            if (line.startsWith("Date,")) {
                return;
            }

            String[] fields = line.split(",");

            // Voltage is the 5th column
            if (fields.length >= 5) {
                try {
                    double v = Double.parseDouble(fields[4]);
                    voltage.set(v);
                    context.write(KEY, voltage);
                } catch (NumberFormatException e) {
                    // Ignore invalid records
                }
            }
        }
    }

    public static class VoltageReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();

        public void reduce(Text key, Iterable<DoubleWritable> values,
                            Context context)
                throws IOException, InterruptedException {

            double sum = 0.0;
            long count = 0;

            for (DoubleWritable value : values) {
                sum += value.get();
                count++;
            }

            if (count > 0) {
                double average = sum / count;
                result.set(average);
                context.write(key, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                "Usage: AverageVoltage <input path> <output path>"
            );
            System.exit(-1);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Average Voltage");

        job.setJarByClass(AverageVoltage.class);

        job.setMapperClass(VoltageMapper.class);
        job.setReducerClass(VoltageReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}