package com.hhlx.kitty.core.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhlx.kitty.common.utils.ReflectionUtils;

/**
 * MyBatis 分页查询助手
 * @author Louis
 * @date Aug 19, 2018
 */
public class MybatisPageHelper {

	public static final String findPage = "findPage";
	
	/**
	 * 分页查询, 约定查询方法名为 “findPage” 
	 * @param pageRequest 分页请求
	 * @param mapper Dao对象，MyBatis的 Mapper	
	 * @param args 方法参数
	 * @return
	 */
	public static PageResult findPage(PageRequest pageRequest, Object mapper) {
		return findPage(pageRequest, mapper, findPage);
	}
	
	/**
	 * 功能：使用map存储参数  <br>
	 * 作者：lk <br> 日期：2020年5月9日  <br>
	 *
	 * @param pageRequest
	 * @param mapper
	 * @return ：PageResult
	 */
	public static PageResult findPageByMap(PageRequest pageRequest, Object mapper) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,ColumnFilter> columnfilters = pageRequest.getColumnFilters();
		for(Map.Entry<String,ColumnFilter> entry:columnfilters.entrySet()) 
			paramMap.put(entry.getValue().getName(), entry.getValue().getValue());
		
		return findPage(pageRequest, mapper, findPage,paramMap);
	}
	
	/**
	 * 调用分页插件进行分页查询
	 * @param pageRequest 分页请求
	 * @param mapper Dao对象，MyBatis的 Mapper	
	 * @param queryMethodName 要分页的查询方法名
	 * @param args 方法参数
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PageResult findPage(PageRequest pageRequest, Object mapper, String queryMethodName, Object... args) {
		// 设置分页参数
		int pageNum = pageRequest.getPageNum();
		int pageSize = pageRequest.getPageSize();
		PageHelper.startPage(pageNum, pageSize);
		// 利用反射调用查询方法
		Object result = ReflectionUtils.invoke(mapper, queryMethodName, args);
		return getPageResult(pageRequest, new PageInfo((List) result));
	}

	/**
	 * 将分页信息封装到统一的接口
	 * @param pageRequest 
	 * @param page
	 * @return
	 */
	private static PageResult getPageResult(PageRequest pageRequest, PageInfo<?> pageInfo) {
		PageResult pageResult = new PageResult();
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotalSize(pageInfo.getTotal());
        pageResult.setTotalPages(pageInfo.getPages());
        pageResult.setContent(pageInfo.getList());
		return pageResult;
	}

}
