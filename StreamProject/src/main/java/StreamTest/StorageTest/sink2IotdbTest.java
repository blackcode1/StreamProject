package StreamTest.StorageTest;

import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
//import org.apache.iotdb.tsfile.write.record.RowBatch;
import org.apache.iotdb.tsfile.write.record.Tablet;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.apache.iotdb.tsfile.write.schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class sink2IotdbTest {

    public static void main(String[] args) throws Exception {

        Session session = new Session("223.99.13.54", 17167, "root", "root");
        //session = new Session("192.168.70.171", 6667, "root", "root");
        session.open();

        Long startT = System.currentTimeMillis();
        List<MeasurementSchema> schema = new ArrayList<>();
        schema.add(new MeasurementSchema("Channel27", TSDataType.INT32, TSEncoding.RLE));
        //schema.add(new MeasurementSchema("Channel25", TSDataType.INT32, TSEncoding.RLE));
        Tablet tablet = new Tablet("root.J053102.T001.V4.group27", schema, 20);

        long[] timestamps = tablet.timestamps;
        Object[] values = tablet.values;
        int[] sensor = (int[]) values[0];
      //  int[] sensor2 = (int[]) values[1];
        for(int i = 0; i < 20; i++){
            int row = tablet.rowSize++;
            timestamps[row] = 1598583001000L+i;
            sensor[i] = i;
           // sensor2[i] = i;
        }
        Long endT = System.currentTimeMillis();
        System.out.println(endT-startT);
        //for(int i = 0; i < 64; i++){
        session.insertTablet(tablet);
        //}

        endT = System.currentTimeMillis();
        System.out.println(endT-startT);
        session.close();


    }
}
