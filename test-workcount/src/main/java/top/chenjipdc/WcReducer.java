package top.chenjipdc;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/31 2:46 下午
 */
public class WcReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private final IntWritable intWritable = new IntWritable(0);

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int total = 0;
        for (IntWritable value : values) {
            total += value.get();
        }
        intWritable.set(total);
        context.write(key, intWritable);
    }
}
