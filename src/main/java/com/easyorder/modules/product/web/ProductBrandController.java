/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.easyorder.modules.product.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.easyorder.common.beans.EasyResponse;
import com.easyorder.common.enums.EasyResponseEnums;
import com.easyorder.modules.product.entity.ProductBrand;
import com.easyorder.modules.product.service.ProductBrandService;
import com.google.common.collect.Lists;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 商品品牌Controller
 * @author qiudequan
 * @version 2017-06-09
 */
@Controller
@RequestMapping(value = "${adminPath}/productManager/productBrand")
public class ProductBrandController extends BaseController {

	@Autowired
	private ProductBrandService productBrandService;

	@ModelAttribute
	public ProductBrand get(@RequestParam(required=false) String id) {
		ProductBrand entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = productBrandService.get(id);
		}
		if (entity == null){
			entity = new ProductBrand();
		}
		return entity;
	}

	/**
	 * 商品品牌列表页面
	 */
	@RequiresPermissions("product:productBrand:list")
	@RequestMapping(value = {"list", ""})
	public String list(ProductBrand productBrand, HttpServletRequest request, HttpServletResponse response, Model model) {
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			addMessage(model, EasyResponseEnums.NOT_FOUND_SUPPLIER.message);
			return "easyorder/product/productBrandList";
		}
		productBrand.setSupplierId(supplierId);
		Page<ProductBrand> page = productBrandService.findPage(new Page<ProductBrand>(request, response), productBrand); 
		model.addAttribute("page", page);
		return "easyorder/product/productBrandList";
	}

	/**
	 * 查看，增加，编辑商品品牌表单页面
	 */
	@RequiresPermissions(value={"product:productBrand:view","product:productBrand:add","product:productBrand:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ProductBrand productBrand, Model model) {
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			addMessage(model, EasyResponseEnums.NOT_FOUND_SUPPLIER.message);
			return "easyorder/product/productBrandForm";
		}
		productBrand.setSupplierId(supplierId);
		model.addAttribute("productBrand", productBrand);
		return "easyorder/product/productBrandForm";
	}

	/**
	 * 保存商品品牌
	 */
	@RequiresPermissions(value={"product:productBrand:add","product:productBrand:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(ProductBrand productBrand, Model model, RedirectAttributes redirectAttributes) throws Exception{
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			addMessage(redirectAttributes, EasyResponseEnums.NOT_FOUND_SUPPLIER.message);
			return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
		}
		productBrand.setSupplierId(supplierId);
		if (!beanValidator(model, productBrand)){
			return form(productBrand, model);
		}
		if(!productBrand.getIsNewRecord()){//编辑表单保存
			ProductBrand t = productBrandService.get(productBrand.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(productBrand, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			productBrandService.save(t);//保存
		}else{//新增表单保存
			productBrandService.save(productBrand);//保存
		}
		addMessage(redirectAttributes, "保存商品品牌成功");
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}

	/**
	 * 删除商品品牌
	 */
	@RequiresPermissions("product:productBrand:del")
	@RequestMapping(value = "delete")
	public String delete(ProductBrand productBrand, RedirectAttributes redirectAttributes) {
		productBrandService.delete(productBrand);
		addMessage(redirectAttributes, "删除商品品牌成功");
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}

	/**
	 * 批量删除商品品牌
	 */
	@RequiresPermissions("product:productBrand:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			productBrandService.delete(productBrandService.get(id));
		}
		addMessage(redirectAttributes, "删除商品品牌成功");
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("product:productBrand:export")
	@RequestMapping(value = "export", method=RequestMethod.POST)
	public String exportFile(ProductBrand productBrand, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "商品品牌"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
			Page<ProductBrand> page = productBrandService.findPage(new Page<ProductBrand>(request, response, -1), productBrand);
			new ExportExcel("商品品牌", ProductBrand.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出商品品牌记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("product:productBrand:import")
	@RequestMapping(value = "import", method=RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ProductBrand> list = ei.getDataList(ProductBrand.class);
			for (ProductBrand productBrand : list){
				try{
					productBrandService.save(productBrand);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条商品品牌记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条商品品牌记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入商品品牌失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}

	/**
	 * 下载导入商品品牌数据模板
	 */
	@RequiresPermissions("product:productBrand:import")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "商品品牌数据导入模板.xlsx";
			List<ProductBrand> list = Lists.newArrayList(); 
			new ExportExcel("商品品牌数据", ProductBrand.class, 1).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/productManager/productBrand/?repage";
	}


	/**
	 * 保存商品品牌
	 */
	@RequiresPermissions(value={"product:productBrand:list"})
	@RequestMapping(value = "all")
	@ResponseBody
	public EasyResponse<List<ProductBrand>> listAll(ProductBrand productBrand, Model model, RedirectAttributes redirectAttributes) throws Exception{
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			return EasyResponse.buildByEnum(EasyResponseEnums.NOT_FOUND_SUPPLIER);
		}
		productBrand.setSupplierId(supplierId);
		List<ProductBrand> productBrands = productBrandService.findList(productBrand);
		return EasyResponse.buildSuccess(productBrands);
	}
	
	/**
	 * 保存商品品牌
	 */
	@RequiresPermissions(value={"product:productBrand:add","product:productBrand:edit"},logical=Logical.OR)
	@RequestMapping(value = "async/save")
	@ResponseBody
	public EasyResponse<String> saveAsync(ProductBrand productBrand, Model model, RedirectAttributes redirectAttributes) throws Exception{
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			return EasyResponse.buildByEnum(EasyResponseEnums.NOT_FOUND_SUPPLIER);
		}
		productBrand.setSupplierId(supplierId);
		if (!beanValidator(model, productBrand)){
			return EasyResponse.buildByEnum(EasyResponseEnums.REQUEST_PARAM_ERROR);
		}
		if(!productBrand.getIsNewRecord()){//编辑表单保存
			ProductBrand t = productBrandService.get(productBrand.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(productBrand, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			productBrandService.save(t);//保存
		}else{//新增表单保存
			productBrandService.save(productBrand);//保存
		}
		return EasyResponse.buildSuccess(productBrand.getId(), "保存成功");
	}

}