package com.easyorder.common.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.easyorder.common.beans.EasyResponse;
import com.easyorder.common.constant.Constants;
import com.easyorder.common.enums.EasyResponseEnums;
import com.easyorder.common.utils.OSSUtils;
import com.easyorder.common.utils.StringUtils;
import com.easyorder.modules.product.entity.ProductPicture;
import com.easyorder.modules.product.service.ProductPictureService;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/upload")
public class UploadController extends BaseController {
	private static String bucketName = Global.getConfig("oss.access.bucket");
	// OSS系统文件根目录
	public static String OSS_ROOT = Global.getConfig("oss.access.root");
	// 存储商品相关文件的文件夹
	public static String PRODUCT_DIC = Global.getConfig("oss.access.product");
	// 存储品牌相关文件的文件夹
	public static String BRAND_DIC = Global.getConfig("oss.access.brand");
	
	@Autowired
	private ProductPictureService productPictureService;

	@RequestMapping(value = "product/file", method = RequestMethod.POST)
	@ResponseBody
	public EasyResponse<String> uploadFile(MultipartFile file, String productId) {
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			logger.error("Did not find the supplier.[supplierId : {}]", supplierId);
			return EasyResponse.buildByEnum(EasyResponseEnums.NOT_FOUND_SUPPLIER);
		}
		ProductPicture productPicture = null;
		String uploadUrl = null;
		String fileName = null;
		try(OSSUtils ossUtils = new OSSUtils()) {
			fileName = file.getOriginalFilename();
			String suffix = ""; 
			int lastIndexOf = fileName.lastIndexOf(".");
			if(StringUtils.isNotEmpty(fileName) && lastIndexOf != -1) {
				suffix = fileName.substring(fileName.lastIndexOf("."));
			}
			String path = "";
			path = OSS_ROOT + PRODUCT_DIC.replace("{supplierId}", supplierId);
			String uploadFileName = System.currentTimeMillis() + suffix;
			uploadUrl = ossUtils.uploadStream(bucketName, path, uploadFileName, file.getInputStream());
			if(StringUtils.isEmpty(uploadUrl)) {
				return EasyResponse.buildError();
			}
			
			if(StringUtils.hasText(productId)) {
				productPicture = new ProductPicture();
				productPicture.setProductId(productId);
				productPicture.setUrl(uploadUrl);
				productPicture.setIsMain(Constants.NO);
				productPictureService.save(productPicture);
			}
			
		} catch (IOException e) {
			logger.error("reading the product file error.[fileName : {}]", fileName,e);
			return EasyResponse.buildError("文件读取异常，请联系管理员。");
		} catch (Exception e) {
			logger.error("upload the product file error.[fileName : {}]", fileName,e);
			return EasyResponse.buildError();
		}
		return EasyResponse.buildSuccess(uploadUrl, "文件上传成功");
	}

	@RequestMapping(value = "image", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> uploadFile(MultipartFile file, HttpServletResponse response, String utype, String productId, String productBrandId) {
		Map<String, String> resultMap = new HashMap<>();
		String supplierId = UserUtils.getUser().getSupplierId();
		if(com.easyorder.common.utils.StringUtils.isEmpty(supplierId)) {
			buildUEErrorResult(resultMap, EasyResponseEnums.NOT_FOUND_SUPPLIER.message);
			return resultMap;
		}
		String fileName = null;
		String uploadUrl = null;
		try(OSSUtils ossUtils = new OSSUtils()) {
			fileName = file.getOriginalFilename();
			String suffix = ""; 
			int lastIndexOf = fileName.lastIndexOf(".");
			if(StringUtils.isNotEmpty(fileName) && lastIndexOf != -1) {
				suffix = fileName.substring(fileName.lastIndexOf("."));
			}
			String path = "";
			if("product".equals(utype)) {
				path = OSS_ROOT + PRODUCT_DIC.replace("{supplierId}", supplierId);
			} else if("brand".equals(utype)) {
				path = OSS_ROOT + BRAND_DIC.replace("{supplierId}", supplierId);
			}
			String uploadFileName = System.currentTimeMillis() + suffix;
			uploadUrl = ossUtils.uploadStream(bucketName, path, uploadFileName, file.getInputStream());
			if(StringUtils.isEmpty(uploadUrl)) {
				buildUEErrorResult(resultMap);
				return resultMap;
			}
			if(StringUtils.hasText(productId)) {
				ProductPicture productPicture = new ProductPicture();
				productPicture.setProductId(productId);
				productPicture.setUrl(uploadUrl);
				productPicture.setIsMain(Constants.NO);
				productPictureService.save(productPicture);
			}
		} catch (IOException e) {
			buildUEErrorResult(resultMap);
			logger.error("An error occurred while reading the file.[fileName : {}]", fileName);
			return resultMap;
		} catch(Exception e) {
			buildUEErrorResult(resultMap);
			logger.error("An error occurred while uploading the file.[fileName : {}]", fileName);
			return resultMap;
		}
		resultMap.put("state", "SUCCESS");
		resultMap.put("url", uploadUrl);
		resultMap.put("title", fileName);
		resultMap.put("original", fileName);
		return resultMap;

	}
	
	/**
	 * 构建用于UEditor的返回结果
	 * @param resultMap
	 */
	private void buildUEErrorResult(Map<String, String> resultMap) {
		resultMap.put("state", "file upload error.");
		resultMap.put("url", Constants.EMPTY_STRING);
		resultMap.put("title", Constants.EMPTY_STRING);
		resultMap.put("original", Constants.EMPTY_STRING);
	}
	
	/**
	 * 构建用于UEditor的返回结果
	 * @param resultMap
	 */
	private void buildUEErrorResult(Map<String, String> resultMap, String errorMsg) {
		resultMap.put("state", errorMsg);
		resultMap.put("url", Constants.EMPTY_STRING);
		resultMap.put("title", Constants.EMPTY_STRING);
		resultMap.put("original", Constants.EMPTY_STRING);
	}
}
