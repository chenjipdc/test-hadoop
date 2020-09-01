package top.chenjipdc;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/9/1 11:01 上午
 */
public class WcSort extends WritableComparator {

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        Text k1 = (Text) a;
        Text k2 = (Text) b;
        return k1.toString().compareTo(k2.toString());
    }
}
