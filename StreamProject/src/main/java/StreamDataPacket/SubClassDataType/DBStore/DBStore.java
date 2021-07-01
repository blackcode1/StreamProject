package StreamDataPacket.SubClassDataType.DBStore;

import StreamDataPacket.DataType;

import java.util.List;

public class DBStore extends DataType {
    String tableName;
    List<DBSQL> sqls;
    Boolean batchStore;

    public DBStore(String tableName, List<DBSQL> sqls) {
        super("DBStore");
        this.tableName = tableName;
        this.sqls = sqls;
        this.batchStore = false;
    }

    public DBStore(String tableName, List<DBSQL> sqls, Boolean batchStore) {
        super("DBStore");
        this.tableName = tableName;
        this.sqls = sqls;
        this.batchStore = batchStore;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DBSQL> getSqls() {
        return sqls;
    }

    public void setSqls(List<DBSQL> sqls) {
        this.sqls = sqls;
    }

    public void addSql(DBSQL sql) {
        this.sqls.add(sql);
    }

    public Boolean getBatchStore() {
        return batchStore;
    }

    public void setBatchStore(Boolean batchStore) {
        this.batchStore = batchStore;
    }
}
