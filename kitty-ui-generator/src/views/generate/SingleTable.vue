<template>
  <div class="page-container">
    <div class="left-container">
      <div class="toolbar"> 
        <el-button size="medium" style="width:99%; padding-right:25px;" @click="getTables" :loading="tableLoading"
          icon="fa fa-hand-o-right">
          选择要生成的表
        </el-button>
        <el-input placeholder="关键字过滤" v-model="filterText" size="medium" style="padding-top:5px;padding-bottom:5px;">
        </el-input>
      </div>
      <div class="left-tree">
        <el-tree class="tree" v-loading="treeLoading" :data="treeData" element-loading-text="拼命加载中"  
          ref="tree" size="medium" @node-click="selectTableChange" :filter-node-method="filterNode">
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span> <li class="fa fa-table fa-lg"></li> {{ data.name }} </span>
          </span>
        </el-tree>
      </div>   
    </div>
    
    <div class="right-container">
      <!-- 表描述 -->
      <div class="base-info">
        <el-form ref="tableForm" class="tableForm" :model="tableModel" :inline="true" label-width="100px" size="small">
          <el-form-item label="表名">
            <el-input v-model="tableModel.name" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="类名">
            <el-input v-model="tableModel.className"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="tableModel.description"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <!-- 列描述列表 -->
      <div class="column-info">
        <el-table :data="tableModel.columns" class="right-table" size="mini" height="375" max-height="375" stripe>
          <el-table-column prop="name" label="" width="32">
            <template slot-scope="scope">
              <span v-if="scope.row.primaryKey">
                <i class="fa fa-key" style="color:#CBA623;"></i>
              </span>
              <span v-else-if="isUniqueKey(scope.row)">
                <i class="fa fa-paperclip" style="color:#0055ff;"></i>
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="字段名" width="110"></el-table-column>
          <el-table-column prop="fieldName" label="属性名" width="100"></el-table-column>
          <el-table-column prop="dataType" label="数据类型" width="80"></el-table-column>
          <el-table-column prop="javaType" label="Java类型" width="90"></el-table-column>
          <el-table-column prop="length" label="长度" width="50"></el-table-column>
          <!-- <el-table-column prop="precision" label="精度" width="80"></el-table-column> -->
          <el-table-column prop="nullable" label="非空" width="50">
            <template slot-scope="scope">
              <span v-if="!scope.row.nullable" type="success">✔</span>
              <span v-else type="danger">✘</span>
            </template>           
          </el-table-column>
          <el-table-column prop="description" label="描述" width="100"></el-table-column>
          <el-table-column prop="search" label="是否查询" width="75" >
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.search" v-if="isNotExclude(scope.row,'src')" ></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column prop="dictModel" label="是否字典" width="75">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.dictModel.dict" v-if="isNotExclude(scope.row,'src')"  ></el-checkbox>
              <el-link type="primary" v-if="scope.row.dictModel.dict" size="small" @click="setDict(scope.row)">字典</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="foreignKeyModel" label="是否外键" width="75">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.foreignKeyModel.foreignKey" v-if="isNotExclude(scope.row,'src')" ></el-checkbox>
              <el-link type="primary" v-if="scope.row.foreignKeyModel.foreignKey" size="small" @click="setForeignKey(scope.row)">外键</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 外键属性设置 -->
      <el-dialog title="外键属性设置" width="45%" :visible.sync="foreign_DialogVisible" :close-on-click-modal="false" >
        <el-form label-width="150px" size="small" >
          <el-form-item label="本表">
            <el-input v-model="tableModel.description" :readonly="true" ></el-input>
          </el-form-item>
          <el-form-item label="本表字段" >
            <el-input v-model="columnModel.description" :readonly="true" ></el-input>
          </el-form-item>
          <el-form-item label="关联表" >
            <el-select style="width:100%" v-model="columnModel.foreignKeyModel.tableName" @change="changeColumns"  
                  clearable filterable placeholder="请选择关联表" >
              <el-option v-for="item in treeData" :key="item.objectName" :label="item.description" :value="item.objectName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联字段" >
            <el-select style="width:100%" v-model="columnModel.foreignKeyModel.columnId" clearable filterable placeholder="请选择关联字段" >
              <el-option v-for="item in options_columns" v-show="isNotExclude(item)" 
                  :key="item.fieldName" :label="item.description" :value="item.fieldName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目标翻译字段" >
            <el-select style="width:100%" v-model="columnModel.foreignKeyModel.columnName" clearable filterable placeholder="请选择目标翻译字段" >
              <el-option v-for="item in options_columns" v-show="isNotExclude(item)" 
                  :key="item.fieldName" :label="item.description" :value="item.fieldName"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" size="small" @click.native="foreign_DialogVisible = false">确定</el-button>
        </div>
      </el-dialog>
      <!-- 字典设置 -->
      <el-dialog title="字典设置" width="45%" :visible.sync="Dict_DialogVisible" :close-on-click-modal="false" >
        <el-form label-width="150px" size="small" class="dict-dialog">
          <el-form-item label="本表字段">
            <el-input size="small" v-model="columnModel.description" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="字典类型">
            <el-input size="small" v-model="columnModel.dictModel.dictType"></el-input>
          </el-form-item>
          <el-form-item label="自定义字典">
            <el-button type="success" size="small" @click="addDictRow()" style="float:left;">添加行</el-button>
          </el-form-item>
          <el-form-item v-for="(item,index) in columnModel.dictModel.kvList" :key="index" class="dict-item">
            名称 <el-input v-model="item.label" class="dict-label" size="small" ></el-input>  
            值 <el-input v-model="item.value" class="dict-value"  size="small" ></el-input>
            <el-button type="danger" size="small" @click="deleteDictRow(item,index)">删除行</el-button>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" size="small" @click.native="Dict_DialogVisible = false">确定</el-button>
        </div>
      </el-dialog>
      <!-- 代码生成设置 -->
      <div class="option-info">
        <el-form ref="optionForm" class="optionForm" :inline="true" label-width="70px"
          size="small">
          <span>
            <el-form-item label="包名">
              <el-input v-model="generateModel.basePackage" placeholder="如：com.hhlx.kitty.admin">
                <el-button type="file" slot="append" icon="fa fa-folder fa-lg" @click="chooseBasePackage"></el-button>
              </el-input>
            </el-form-item>
            <el-form-item label="路径">
              <el-input v-model="generateModel.outPutFolderPath" placeholder="生成代码存放目录">
                <el-button type="file" slot="append" icon="fa fa-folder fa-lg" @click="chooseOutputFolder"></el-button>
              </el-input>
            </el-form-item>
          </span>
          <span style="float:right; padding-right:20px;">
            <el-button size="small" type="primary" :disabled="disabledGenerateBtn" :loading="generateLoading" @click="generateCode">生成代码</el-button>
          </span>
        </el-form>
      </div>
    </div>
    <!--数据源配置界面-->
    <datasource-dialog ref="datasourceDialog" v-if="datasourceVisible"></datasource-dialog>
    <!--表格数据选择界面-->
    <select-table-dialog title="选择要生成的表" ref="selectTableDialog" v-if="selectTableDialogVisible"
      :data="selectTableData" :columns="selectTableColumns" @selectionChange="tableSelectionChange"
      :showHeader="true">
    </select-table-dialog>
  </div>
</template>

<script>
import axios from "axios"
import DatasourceDialog from "@/views/Datasource/DatasourceDialog"
import SelectTableDialog from "@/components/SelectTableDiaog"
export default {
  components: {
    DatasourceDialog,
    SelectTableDialog
  },
  data() {
    return {
      treeLoading: false,
      tableLoading: false,
      generateLoading: false,
      datasourceVisible: false,
      selectTableDialogVisible: false,
      disabledGenerateBtn: true,
      baseUrl: this.global.baseUrl,
      filterText: "",
      selectTableData: null,
      tableModel: {},
      generateModel: {
        basePackage:'',
        outPutFolderPath:'',
        connParam:null,
        tableModels:null
      },
      treeData: null,
      selectTableColumns: [
        {
          prop: "name",
          label: "名称"
        },
        {
          prop: "description",
          label: "描述"
        }
      ],

      // 业务扩展
      foreign_DialogVisible: false,
      Dict_DialogVisible: false,
      exclude_fields:['createBy','createTime','lastUpdateBy','lastUpdateTime','delFlag'],
      columnModel: {
        foreignKeyModel:{}, dictModel: {}
      },
      options_columns: [],
      
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  methods: {
    // 打开数据源配置界面
    configDatasource() {
      this.datasourceVisible = true
      this.$nextTick(() => {
        this.$refs.datasourceDialog.init()
      })
    },
    // 过滤表显示
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    // 获取要生成的表
    getTables() {
      let dsStr = localStorage.getItem("datasource")
      if (dsStr == null) {
        this.$message({ message: '请先配置数据源', type: 'error' })
        return
      }
      this.tableLoading = true
      dsStr = localStorage.getItem("datasource")
      axios.post(this.baseUrl + "/getTables", JSON.parse(dsStr)).then(res => {
        res = res.data
        if (res.code == 200) {
          this.selectTableDialogVisible = true
          this.selectTableData = res.data
          this.$nextTick(() => {
            this.$refs.selectTableDialog.init()
          })
        } else {
          this.$message({ message: res.msg, type: "error" })
        }
        this.tableLoading = false
      })
    },
    // 选择要生成的表
    tableSelectionChange(selections) {
      this.treeLoading = true
      let dsStr = localStorage.getItem("datasource")
      let params = {
        connParam: JSON.parse(dsStr),
        tableModels: selections
      };
      axios.post(this.baseUrl + "/getGenerateModel", params).then(res => {
        res = res.data
        if (res.code == 200) {
          this.generateModel = res.data
          this.treeData = this.generateModel.tableModels
          this.generateModel.outPutFolderPath = 'X:\\draft\\kitty-generator\\out'
          this.disabledGenerateBtn = false
        } else {
          this.$message({ message: res.msg, type: "error" })
        }
        this.treeLoading = false
      })
    },
    // 选择查看表信息
    selectTableChange(data) {
      this.tableModel = data
    },
    // 选择代码输出目录
    chooseBasePackage() {
      this.generateModel.basePackage = 'com.hhlx.kitty.admin'
    },
    // 选择代码输出目录
    chooseOutputFolder() {
      this.generateModel.outPutFolderPath = 'X:\\draft\\kitty-generator\\out'
    },
    // 生成代码
    generateCode() {
      this.generateLoading = true;
      axios.post(this.baseUrl + "/generateModels", this.generateModel).then(res => {
        res = res.data
        if (res.code == 200) {
          this.$message({ message: '代码生成完成', type: 'success' })
        } else {
          this.$message({ message: res.msg, type: "error" })
        }
        this.generateLoading = false
      })
    },
    // 排除字段： createBy、createTime、lastUpdateBy、lastUpdateTime、delFlag
    isNotExclude(row,src) {
      if(src == 'src' && row.fieldName == 'id')
        return false;
      return this.exclude_fields.indexOf(row.fieldName) == -1;
    },
    // 是否是唯一索引键
    isUniqueKey(row) {
      if(!this.tableModel.uniqueKeys || this.tableModel.uniqueKeys.length == 0)
        return false;
      for(let i=0,j=this.tableModel.uniqueKeys.length;i<j;i++) {
        if(row.name == this.tableModel.uniqueKeys[i].name)
          return true;
      }
      return false;
    },
    // 表名改变时，动态改变 字段下拉列表
    changeColumns(selTablename){
      if(!selTablename)
        return
      for(let i=0,j=this.treeData.length;i<j;i++){
        if(this.treeData[i].objectName == selTablename) {
          this.options_columns = this.treeData[i].columns;
          break;
        }
      }
    },
    // 外键设置
    setForeignKey(row) {
      this.foreign_DialogVisible = true;
      this.columnModel = row;
      this.changeColumns(this.columnModel.foreignKeyModel.tableName);
    },
    // 字典设置
    setDict(row) {
      this.Dict_DialogVisible = true;
      this.columnModel = row;
    },
    // 添加自定义字典 行
    addDictRow(){
      let that = this;
      that.columnModel.dictModel.kvList.push({label:'',value:''});
    },
    // 删除自定义字典 行
    deleteDictRow(item,index){
      this.columnModel.dictModel.kvList.splice(index,1)
    },
  }
}
</script>

<style>
.page-container {
  position: absolute;
  top: 35px;
  bottom: 5px;
  width: 99%;
  padding: 4px;
  /* background-color: rgb(21, 41, 97); */
}
.left-container {
  padding: 4px;
  float: left;
  width: 20%;
  height: 100%;
}
.right-container {
  padding-top: 1px;
  padding-bottom: 6px;
  left: 200px;
  float: right;
  width: 78%;
  height: 100%;
}
.page-container > div {
  border-color: rgba(173, 180, 180, 0.5);
  border-width: 1px;
  border-style: solid;
}
.base-info,
.column-info,
.option-info {
  padding-top: 15px;
  border-color: rgba(173, 180, 180, 0.2);
  border-width: 1px;
  border-style: solid;
}
.base-info,
.column-info,
.option-info {
  margin: 5px;
}
.tableForm,
.optionForm {
  text-align: left;
}
.tree {
  padding-top: 10px;
}

.column-info .el-input--small>input.el-input__inner {
    height: 23px;
    line-height: 23px;
}
.dict-dialog .dict-item {
  margin-left: -150px;
}
.dict-dialog .dict-item .dict-label {
  width: 100px;
  margin-right: 15px;
  margin-left: 3px;
}
.dict-dialog .dict-item .dict-value {
  width: 100px;
  margin-left: 3px;
  margin-right: 10px;
}
</style>