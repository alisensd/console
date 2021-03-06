/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.easyorder.modules.supplier.web;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.easyorder.common.utils.BeanUtils;
import com.easyorder.modules.supplier.entity.Supplier;
import com.easyorder.modules.supplier.service.SupplierService;
import com.easyorder.modules.supplier.vo.SupplierVO;
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
 * 供货商Controller
 * @author qiudequan
 * @version 2017-04-22
 */
@Controller
@RequestMapping(value = "${adminPath}/supplier")
public class SupplierController extends BaseController {

	@Autowired
	private SupplierService supplierService;

	@ModelAttribute
	public SupplierVO get(@RequestParam(required=false) String id) {
		SupplierVO entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = supplierService.getById(id);
		}
		if (entity == null){
			entity = new SupplierVO();
		}
		return entity;
	}

	/**
	 * 供货商列表页面
	 */
	@RequiresPermissions("supplier:supplier:list")
	@RequestMapping(value = {"list", ""})
	public String list(Supplier supplier, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Supplier> page = supplierService.findPage(new Page<Supplier>(request, response), supplier); 
		model.addAttribute("page", page);
		return "easyorder/supplier/supplierList";
	}

	/**
	 * 查看，增加，编辑供货商表单页面
	 */
	@RequiresPermissions(value={"supplier:supplier:view","supplier:supplier:add","supplier:supplier:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SupplierVO supplierVo, Model model, String action) {
		model.addAttribute("supplier", supplierVo);
		model.addAttribute("action", action);
		return "easyorder/supplier/supplierForm";
	}

	/**
	 * 保存供货商
	 */
	@RequiresPermissions(value={"supplier:supplier:add","supplier:supplier:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(SupplierVO supplierVo, Model model, RedirectAttributes redirectAttributes, String type) throws Exception{
		if (!beanValidator(model, supplierVo)){
			return form(supplierVo, model, "save");
		}
		if(!supplierVo.getIsNewRecord()){//编辑表单保存
			Supplier t = supplierService.get(supplierVo.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(supplierVo, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			supplierService.save(t);//保存
		}else{//新增表单保存
			Supplier supplier = new Supplier();
			BeanUtils.consoleCopy(supplierVo, supplier);
			supplierService.save(supplier);//保存
		}
		
		if("store".equals(type)) {
			addMessage(redirectAttributes, "保存店铺信息成功");
			return "redirect:" + Global.getAdminPath() + "/supplier/store";
		}
		addMessage(redirectAttributes, "保存供货商成功");
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 删除供货商
	 */
	@RequiresPermissions("supplier:supplier:del")
	@RequestMapping(value = "delete")
	public String delete(Supplier supplier, RedirectAttributes redirectAttributes) {
		supplierService.delete(supplier);
		addMessage(redirectAttributes, "删除供货商成功");
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 批量删除供货商
	 */
	@RequiresPermissions("supplier:supplier:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			supplierService.delete(supplierService.get(id));
		}
		addMessage(redirectAttributes, "删除供货商成功");
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("supplier:supplier:export")
	@RequestMapping(value = "export", method=RequestMethod.POST)
	public String exportFile(Supplier supplier, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "供货商"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
			Page<Supplier> page = supplierService.findPage(new Page<Supplier>(request, response, -1), supplier);
			new ExportExcel("供货商", Supplier.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出供货商记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("supplier:supplier:import")
	@RequestMapping(value = "import", method=RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Supplier> list = ei.getDataList(Supplier.class);
			for (Supplier supplier : list){
				try{
					supplierService.save(supplier);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条供货商记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条供货商记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入供货商失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 下载导入供货商数据模板
	 */
	@RequiresPermissions("supplier:supplier:import")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "供货商数据导入模板.xlsx";
			List<Supplier> list = Lists.newArrayList(); 
			new ExportExcel("供货商数据", Supplier.class, 1).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/supplier/?repage";
	}

	/**
	 * 供应商自行查看店铺信息
	 */
	@RequiresPermissions(value={"supplier:supplier:view"})
	@RequestMapping(value = "store")
	public String store(SupplierVO supplierVo, Model model) {
		String supplierId = UserUtils.getUser().getSupplierId();
		if(StringUtils.isBlank(supplierId)) {
			logger.error("When querying store information, the parameter[supplierId] is empty.");
			addMessage(model, "未找到供应商");
			return "easyorder/supplier/storeForm";
		}
		SupplierVO supplier = supplierService.getById(supplierId);
		model.addAttribute("supplier", supplier);
		return "easyorder/supplier/storeForm";
	}
	
	/**
	 * 供应商自行查看店铺信息
	 */
	@RequiresPermissions(value={"supplier:supplier:edit"})
	@RequestMapping(value = "infoEdit")
	public String editSupplierInfo(SupplierVO supplierVo, Model model) {
		String supplierId = UserUtils.getUser().getSupplierId();
		if(StringUtils.isBlank(supplierId)) {
			logger.error("When querying store information, the parameter[supplierId] is empty.");
			addMessage(model, "未找到供应商");
			return "easyorder/supplier/supplierInfoEdit";
		}
		SupplierVO supplier = supplierService.getById(supplierId);
		model.addAttribute("supplier", supplier);
		return "easyorder/supplier/supplierInfoEdit";
	}


}