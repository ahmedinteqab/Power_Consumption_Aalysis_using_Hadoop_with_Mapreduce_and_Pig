import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxPower {

    public static class MaxPowerMapper
            extends Mapper<Object, Text, Text, DoubleWritable> {

        private final Text outputKey = new Text("Maximum_Power");
        private final DoubleWritable outputValue = new DoubleWritable();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            // Skip header
            if (line.startsWith("Date,Time")) {
                return;
            }

            String[] fields = line.split(",");

            try {
                if (fields.length >= 9) {
                    double power = Double.parseDouble(fields[2]);
                    outputValue.set(power);
                    context.write(outputKey, outputValue);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid records
            }
        }
    }

    public static class MaxPowerReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private double maxPower = Double.MIN_VALUE;

        public void reduce(Text key, Iterable<DoubleWritable> values,
                           Context context)
                throws IOException, InterruptedException {

            for (DoubleWritable value : values) {
                if (value.get() > maxPower) {
                    maxPower = value.get();
                }
            }

            context.write(key, new DoubleWritable(maxPower));
        }
    }

    public static void main(String[] args)
            throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Usage: MaxPower <input path> <output path>");
            System.exit(2);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Maximum Power Consumption");

        job.setJarByClass(MaxPower.class);

        job.setMapperClass(MaxPowerMapper.class);
        job.setReducerClass(MaxPowerReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}