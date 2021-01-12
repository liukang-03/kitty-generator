package com.hhlx.kitty.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhlx.kitty.core.http.HttpResult;
import com.hhlx.kitty.dbms.vo.ConnParam;
import com.hhlx.kitty.generator.service.GenerateService;
import com.hhlx.kitty.generator.vo.GenerateModel;

/**
 * 代码生成控制器
 * @author Louis
 * @date Nov 9, 2018
 */
@RestController
@RequestMapping("")
public class GenerateController {

	@Autowired
	GenerateService generatorService;
	
	@PostMapping("/testConnection")
	public HttpResult testConnection(@RequestBody ConnParam connParam) {
		boolean success = generatorService.testConnection(connParam);
		if(success) {
			return HttpResult.ok(generatorService.testConnection(connParam));
		}
		return HttpResult.error("连接失败,请检查数据库及连接。");
	}

	@PostMapping("/getTables")
	public HttpResult getTables(@RequestBody ConnParam connParam) {
		return HttpResult.ok(generatorService.getTables(connParam));
	}
	
	@PostMapping("/getGenerateModel")
	public HttpResult getGenerateModel(@RequestBody GenerateModel generateModel) {
		return HttpResult.ok(generatorService.getGenerateModel(generateModel));
	}
	
	@PostMapping("/generateModels")
	public HttpResult generateModels(@RequestBody GenerateModel generateModel) throws Exception {
		return HttpResult.ok(generatorService.generateModels(generateModel));
	}
	
}
