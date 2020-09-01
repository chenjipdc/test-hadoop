package top.chenjipdc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/9/1 11:36 上午
 */
public class HBaseTest1 {

    private Configuration conf;
    private Connection conn;
    private Admin admin;
    private Table table;
    TableName tableName = TableName.valueOf("hbase_test");

    @Before
    public void init() throws IOException {
        conf = HBaseConfiguration.create();
        // 配置zookeeper
        conf.set("hbase.zookeeper.quorum","node01,node02,node03");
        // 获取连接
        conn = ConnectionFactory.createConnection(conf);
        // 获取admin
        admin = conn.getAdmin();
        // 获取table
        table = conn.getTable(tableName);
    }

    @After
    public void after() {
        try {
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            admin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createTable() throws IOException {
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName);

        ColumnFamilyDescriptorBuilder cf1 = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("cf1"));
        builder.setColumnFamily(cf1.build());
        if (admin.tableExists(tableName)){
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        }
        admin.createTable(builder.build());
    }

    @Test
    public void insert() throws IOException {
        Put put = new Put(Bytes.toBytes("1"));

        byte[] cf = Bytes.toBytes("cf1");
        put.addColumn(cf, Bytes.toBytes("name"), Bytes.toBytes("pdc"));
        put.addColumn(cf, Bytes.toBytes("age"), Bytes.toBytes("18"));
        put.addColumn(cf, Bytes.toBytes("sex"), Bytes.toBytes("老baby"));
        table.put(put);
    }

    @Test
    public void get() throws IOException {
        byte[] cf = Bytes.toBytes("cf1");
        byte[] qName = Bytes.toBytes("name");
        byte[] qAge = Bytes.toBytes("age");
        byte[] qSex = Bytes.toBytes("sex");

        Get get = new Get(Bytes.toBytes("1"));
        Result result = table.get(get);
        get.addColumn(cf, qName);
        get.addColumn(cf, qAge);
        get.addColumn(cf, qSex);

        Cell name = result.getColumnLatestCell(cf,
                qName);
        Cell age = result.getColumnLatestCell(cf,
                qAge);
        Cell sex = result.getColumnLatestCell(cf,
                qSex);

        System.out.println("name: " + Bytes.toString(CellUtil.cloneValue(name)));
        System.out.println("age: " + Bytes.toString(CellUtil.cloneValue(age)));
        System.out.println("sex: " + Bytes.toString(CellUtil.cloneValue(sex)));
    }

    @Test
    public void scan() throws IOException {
        byte[] cf = Bytes.toBytes("cf1");
        ResultScanner scanner = table.getScanner(cf);

        for (Result result : scanner) {
            Cell name = result.getColumnLatestCell(cf,
                    Bytes.toBytes("name"));
            Cell age = result.getColumnLatestCell(cf,
                    Bytes.toBytes("age"));
            Cell sex = result.getColumnLatestCell(cf,
                    Bytes.toBytes("sex"));

            System.out.println("name: " + Bytes.toString(CellUtil.cloneValue(name)));
            System.out.println("age: " + Bytes.toString(CellUtil.cloneValue(age)));
            System.out.println("sex: " + Bytes.toString(CellUtil.cloneValue(sex)));
            System.out.println("-------------------------------");
        }
    }

    @Test
    public void batchInsert() throws IOException {
        List<Put> puts = new ArrayList<Put>();
        byte[] cf = Bytes.toBytes("cf1");
        for (int i = 0; i < 1000; i++) {
            Put put = new Put(Bytes.toBytes("" + i));
            put.addColumn(cf, Bytes.toBytes("name"), Bytes.toBytes("pdc" + i));
            put.addColumn(cf, Bytes.toBytes("age"), Bytes.toBytes("" + (i%10 + 10)));
            put.addColumn(cf, Bytes.toBytes("sex"), Bytes.toBytes("老baby" + i));
            puts.add(put);
        }
        table.put(puts);
    }

    @Test
    public void scanByCondition() throws IOException {
        byte[] cf = Bytes.toBytes("cf1");
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes("1"));
        scan.withStopRow(Bytes.toBytes("101"));
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            Cell name = result.getColumnLatestCell(cf,
                    Bytes.toBytes("name"));
            Cell age = result.getColumnLatestCell(cf,
                    Bytes.toBytes("age"));
            Cell sex = result.getColumnLatestCell(cf,
                    Bytes.toBytes("sex"));

            System.out.println("name: " + Bytes.toString(CellUtil.cloneValue(name)));
            System.out.println("age: " + Bytes.toString(CellUtil.cloneValue(age)));
            System.out.println("sex: " + Bytes.toString(CellUtil.cloneValue(sex)));
            System.out.println("-------------------------------");
        }
    }

    @Test
    public void testByFilter() throws IOException {
        byte[] cf = Bytes.toBytes("cf1");
        Scan scan = new Scan();

        FilterList filters = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        // rowkey以什么开头
        filters.addFilter(new PrefixFilter(Bytes.toBytes("99")));

        // 指定哪个列族,哪一列过滤
        filters.addFilter(new SingleColumnValueFilter(cf, Bytes.toBytes("age"), CompareOperator.GREATER, Bytes.toBytes("18")));

        scan.setFilter(filters);

        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {
            Cell name = result.getColumnLatestCell(cf,
                    Bytes.toBytes("name"));
            Cell age = result.getColumnLatestCell(cf,
                    Bytes.toBytes("age"));
            Cell sex = result.getColumnLatestCell(cf,
                    Bytes.toBytes("sex"));

            System.out.println("name: " + Bytes.toString(CellUtil.cloneValue(name)));
            System.out.println("age: " + Bytes.toString(CellUtil.cloneValue(age)));
            System.out.println("sex: " + Bytes.toString(CellUtil.cloneValue(sex)));
            System.out.println("-------------------------------");
        }
    }
}
