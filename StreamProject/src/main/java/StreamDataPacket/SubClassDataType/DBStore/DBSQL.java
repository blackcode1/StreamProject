package StreamDataPacket.SubClassDataType.DBStore;

import java.util.List;

public class DBSQL {
    String rowKey;
    Long timeStamp;
    List<DBCondition> conditons;

    public DBSQL(String rowKey, Long timeStamp, List<DBCondition> conditons) {
        this.rowKey = rowKey;
        this.timeStamp = timeStamp;
        this.conditons = conditons;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<DBCondition> getConditons() {
        return conditons;
    }

    public void setConditons(List<DBCondition> conditons) {
        this.conditons = conditons;
    }

    public void addtConditon(DBCondition conditon){
        this.conditons.add(conditon);
    }
}
