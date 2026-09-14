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

public class TotalEnergy {

    public static class EnergyMapper
            extends Mapper<LongWritable, Text, Text, DoubleWritable> {

        private static final Text KEY = new Text("Total_Energy");
        private DoubleWritable power = new DoubleWritable();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            // Skip header
            if (line.startsWith("Date,")) {
                return;
            }

            String[] fields = line.split(",");

            // Global_active_power is the 3rd column
            if (fields.length >= 3) {
                try {
                    double activePower = Double.parseDouble(fields[2]);
                    power.set(activePower);
                    context.write(KEY, power);
                } catch (NumberFormatException e) {
                    // Ignore invalid records
                }
            }
        }
    }

    public static class EnergyReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();

        public void reduce(Text key, Iterable<DoubleWritable> values,
                           Context context)
                throws IOException, InterruptedException {

            double total = 0.0;

            for (DoubleWritable value : values) {
                total += value.get();
            }

            result.set(total);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                "Usage: TotalEnergy <input path> <output path>"
            );
            System.exit(-1);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Total Energy Consumption");

        job.setJarByClass(TotalEnergy.class);

        job.setMapperClass(EnergyMapper.class);
        job.setReducerClass(EnergyReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}