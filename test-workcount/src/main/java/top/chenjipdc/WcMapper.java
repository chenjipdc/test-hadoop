package top.chenjipdc;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/31 2:46 下午
 */
public class WcMapper extends Mapper<Object, Text, Text, IntWritable> {

    private final Text text = new Text();
    private final IntWritable intWritable = new IntWritable(1);

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        System.out.println(value.toString());
        String[] split = value.toString()
                .split(" ");
        for (String s : split) {
            text.set(s);
            context.write(text, intWritable);
        }
    }
}
