package StreamSink;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.utils.Binary;

import org.apache.iotdb.tsfile.write.record.Tablet;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import java.util.ArrayList;
import java.util.List;

public class IotDBBatchSink extends RichSinkFunction<DataType> {
    String ip;
    String port;
    String user;
    String password;
    Session session;

    public IotDBBatchSink(String ip, String user, String password) {
        this.ip = ip.split(":")[0];
        this.port = ip.split(":")[1];
        this.user = user;
        this.password = password;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        session = new Session(ip, port, user, password);
        session.open();
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (session != null) {
            session.close();
        }
    }

    public Tablet createRowBatch(DBSQL dbsql, int batchSize, String tableName){
        List<DBCondition> dbConditions = dbsql.getConditons();
        List<MeasurementSchema> schemas = new ArrayList<>();
        for (DBCondition condition : dbConditions) {
            if (condition.getDataType().equals("Int")) {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.INT32, TSEncoding.RLE));
            } else if (condition.getDataType().equals("Long")) {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.INT64, TSEncoding.RLE));
            } else if (condition.getDataType().equals("Float")) {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.FLOAT, TSEncoding.GORILLA));
            } else if (condition.getDataType().equals("Double")) {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.DOUBLE, TSEncoding.GORILLA));
            } else if (condition.getDataType().equals("Boolean")) {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.BOOLEAN, TSEncoding.RLE));
            } else {
                schemas.add(new MeasurementSchema(condition.getColName(), TSDataType.TEXT, TSEncoding.PLAIN));
            }
        }
        String table = tableName;
        if (dbsql.getRowKey() != null) {
            table = table + "." + dbsql.getRowKey();
        }
        Tablet tablet = new Tablet(table, schemas, batchSize);
        return tablet;
    }

    public void setRowBatchValue(Tablet tablet, List<DBCondition> dbConditions, Long time){
        long[] timestamps = tablet.timestamps;
        Object[] values = tablet.values;
        int row = tablet.rowSize++;
        timestamps[row] = time;
        for (int j = 0; j < dbConditions.size(); j++) {
            DBCondition condition = dbConditions.get(j);
            if (condition.getDataType().equals("Int")) {
                int[] sensor = (int[]) values[j];
                sensor[row] = Integer.parseInt(dbConditions.get(j).getValue());
            } else if (condition.getDataType().equals("Long")) {
                long[] sensor = (long[]) values[j];
                sensor[row] = Long.parseLong(dbConditions.get(j).getValue());
            } else if (condition.getDataType().equals("Float")) {
                float[] sensor = (float[]) values[j];
                sensor[row] = Float.parseFloat(dbConditions.get(j).getValue());
            } else if (condition.getDataType().equals("Double")) {
                double[] sensor = (double[]) values[j];
                sensor[row] = Double.parseDouble(dbConditions.get(j).getValue());
            } else if (condition.getDataType().equals("Boolean")) {
                boolean[] sensor = (boolean[]) values[j];
                sensor[row] = Boolean.parseBoolean(dbConditions.get(j).getValue());
            } else {
                Binary[] sensor = (Binary[]) values[j];
                sensor[row] = new Binary(dbConditions.get(j).getValue());
            }
        }
    }

    @Override
    public void invoke(DataType value, Context context) throws Exception {
        if(value.streamDataType.equals("DBStore")) {
            DBStore dbStore = (DBStore) value;
            if (dbStore.getBatchStore()) {
                List<DBSQL> dbsqls = dbStore.getSqls();
               // RowBatch rowBatch = createRowBatch(dbsqls.get(0), dbsqls.size(), dbStore.getTableName());
                Tablet tablet = createRowBatch(dbsqls.get(0), dbsqls.size(), dbStore.getTableName());
                for (DBSQL dbsql: dbsqls) {
                    setRowBatchValue(tablet, dbsql.getConditons(), dbsql.getTimeStamp());
                }
                if(session == null){
                    session = new Session(ip, port, user, password);
                    session.open();
                }
                try {
                    session.insertTablet(tablet);
                } catch (Exception e){
                    session = new Session(ip, port, user, password);
                    session.open();
                    session.insertTablet(tablet);
                }

            }
            else {
                for(DBSQL dbsql: dbStore.getSqls()){
                    Tablet tablet = createRowBatch(dbsql, 1, dbStore.getTableName());
                    setRowBatchValue(tablet, dbsql.getConditons(), dbsql.getTimeStamp());
                    if(session == null){
                        session = new Session(ip, port, user, password);
                        session.open();
                    }
                    try {
                        session.insertTablet(tablet);
                    } catch (Exception e){
                        session = new Session(ip, port, user, password);
                        session.open();
                        session.insertTablet(tablet);
                    }
                }
            }

        }
    }
}
