/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.easyorder.modules.product.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.easyorder.modules.product.entity.ProductBrand;

/**
 * 商品品牌DAO接口
 * @author qiudequan
 * @version 2017-06-09
 */
@MyBatisDao
public interface ProductBrandDao extends CrudDao<ProductBrand> {

	
}