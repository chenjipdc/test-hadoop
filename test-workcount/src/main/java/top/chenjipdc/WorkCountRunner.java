package top.chenjipdc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/31 2:39 下午
 */
public class WorkCountRunner {

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");

        Configuration configuration = new Configuration(true);
        Job job = Job.getInstance(configuration);
        job.setJobName("works counter");

        // 本地调试需要设置jar包路径
        job.setJar("/Users/pdc/data/projects/bigdata/mytest/test-hadoop/test-workcount/target/test-workcount-1.0-SNAPSHOT.jar");
        job.setJarByClass(WorkCountRunner.class);

        // input file
        TextInputFormat.addInputPath(job, new Path("/test-map-reduce/data/input/workcount.txt"));
        // output file
        TextOutputFormat.setOutputPath(job, new Path("/test-map-reduce/data/output/workcount-result/"));

        // mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setMapperClass(WcMapper.class);

        // reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setReducerClass(WcReducer.class);

        // 排序
//        job.setSortComparatorClass(WcSort.class);

        // wait
        job.waitForCompletion(true);
    }

//    public void setConf(Configuration configuration) {
//
//        this.configuration = configuration;
//    }
//
//    public Configuration getConf() {
//        return this.configuration;
//    }
}
