package com.hhlx.kitty.generator.service;

import java.io.IOException;
import java.util.List;

import com.hhlx.kitty.dbms.model.Table;
import com.hhlx.kitty.dbms.vo.ConnParam;
import com.hhlx.kitty.generator.vo.GenerateModel;

/**
 * 代码生成服务
 * @author Louis
 * @date Nov 9, 2018
 */
public interface GenerateService {

	/**
	 * 是否可以连接
	 * @param connParam
	 * @return
	 */
	public boolean testConnection(ConnParam connParam);

	/**
	 * 反向查找数据表
	 * @param connParam
	 * @return
	 */
	public List<Table> getTables(ConnParam connParam);

	/**
	 * 获取代码生成数据模型
	 * @param tables
	 * @return
	 */
	public GenerateModel getGenerateModel(GenerateModel generateModel);

	/**
	 * 生成代码文件
	 * @param generateModel
	 * @return
	 * @throws IOException 
	 */
	public boolean generateModels(GenerateModel generateModel) throws Exception;


}
