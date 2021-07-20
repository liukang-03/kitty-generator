package com.hhlx.kitty.generator.vo;

/**
 * @Description: TODO
 * @Author: liuk
 * @Date: 2021/7/20 12:07
 */
public class ForeignKeyModel {
    /* 是否外键 */
    private boolean isForeignKey = false;
    /* 关联表实例名 */
    private String tableName;
    /* 关联表中：关联字段属性名 */
    private String columnId;
    /* 关联表中：需要被翻译的字段属性名 */
    private String columnName;

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
