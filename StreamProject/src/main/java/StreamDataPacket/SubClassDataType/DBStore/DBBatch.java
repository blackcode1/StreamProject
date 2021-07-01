package StreamDataPacket.SubClassDataType.DBStore;

import scala.Tuple2;
import scala.Tuple3;

import java.util.List;

public class DBBatch {
    List<Tuple2<String, String>> rowInfo;
    List<List<String>> colValue;
    int colNum;
    int rowNum;

    public DBBatch(List<Tuple2<String, String>> rowInfo) {
        this.rowInfo = rowInfo;
    }
}
