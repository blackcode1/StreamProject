package StreamTest.StorageTest;

import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.write.record.RowBatch;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.apache.iotdb.tsfile.write.schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class sink2IotdbTest {

    public static void main(String[] args) throws Exception {

        Session session = new Session("192.168.3.31", 6667, "root", "root");
        session.open();

        Long startT = System.currentTimeMillis();
        Schema schema = new Schema();
        schema.registerMeasurement(new MeasurementSchema("U16AS2Pressure", TSDataType.DOUBLE, TSEncoding.RLE));
        schema.registerMeasurement(new MeasurementSchema("U16CvPressure", TSDataType.DOUBLE, TSEncoding.RLE));

        RowBatch rowBatch = schema.createRowBatch("root.QD2000.Q200.Head.Carriage12", 100000);

        long[] timestamps = rowBatch.timestamps;
        Object[] values = rowBatch.values;
        double[] sensor = (double[]) values[0];
        double[] sensor2 = (double[]) values[1];
        for(int i = 0; i < 100000; i++){
            int row = rowBatch.batchSize++;
            timestamps[row] = 1598583001000L+i;
            sensor[i] = i;
            sensor2[i] = i;
        }

        session.insertBatch(rowBatch);

        Long endT = System.currentTimeMillis();
        System.out.println(endT-startT);
        session.close();


    }
}
