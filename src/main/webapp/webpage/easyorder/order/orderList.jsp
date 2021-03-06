<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>订货单管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" href="${ctxStatic}/easy-selector/easy-selector.css">
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>订货单列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="order" action="${ctx}/orderManager/order/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<label>订货单号：</label>
			<form:input path="orderNo" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
		 </div>	
		 <div class="form-group">
			<label>订货状态：</label>
			<div class="display-ib">
				<div id="orderStatus" data-text="请选择订单状态" data-value="${order.mtOrderStatusCd}" data-name='mtOrderStatusCd'></div>
			</div>
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<shiro:hasPermission name="order:order:add">
				<table:addRow url="${ctx}/orderManager/order/form?action=add" title="订货单"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="order:order:edit">
			    <table:editRow url="${ctx}/orderManager/order/form?action=edit" title="订货单" id="contentTable"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="order:order:del">
				<table:delRow url="${ctx}/orderManager/order/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="order:order:import">
				<table:importExcel url="${ctx}/orderManager/order/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="order:order:export">
	       		<table:exportExcel url="${ctx}/orderManager/order/export"></table:exportExcel><!-- 导出按钮 -->
	       	</shiro:hasPermission>
	       <button class="btn btn-white btn-sm " data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		
			</div>
		<div class="pull-right">
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="search()" ><i class="fa fa-search"></i> 查询</button>
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()" ><i class="fa fa-refresh"></i> 重置</button>
		</div>
	</div>
	</div>
	
	<!-- 表格 -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th> <input type="checkbox" class="i-checks"></th>
				<th  class="sort-column orderNo">订货单号</th>
				<th >客户</th>
				<th  class="sort-column mtOrderStatusCd">订单状态</th>
				<th  class="sort-column totalPrice">金额</th>
				<th  class="sort-column actualPrice">实付金额</th>
				<th  class="sort-column updateDate">更新时间</th>
				<th  class="sort-column remarks">备注</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:choose>
			<c:when test="${empty page or empty page.list }">
				<tr>
					<td colspan="9" align="center">暂无订货单信息</td>
				</tr>
			</c:when>
			<c:otherwise>
				<c:forEach items="${page.list}" var="order">
					<tr>
						<td> <input type="checkbox" id="${order.id}" class="i-checks"></td>
						<td><a  href="${ctx}/orderManager/order/form?action=view&id=${order.id}">
							${order.orderNo}
						</a></td>
						<td>
							${order.customerName}
						</td>
						<td>
							${fns:getDictLabel(order.mtOrderStatusCd, 'mtOrderStatusCd', '-')}
						</td>
						<td>
							${order.totalPrice}
						</td>
						<td>
							${order.actualPrice}
						</td>
						<td>
							<fmt:formatDate value="${order.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
						</td>
						<td>
							${order.remarks}
						</td>
						<td>
							<shiro:hasPermission name="order:order:view">
								<a href="${ctx}/orderManager/order/form?action=view&id=${order.id}" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
							</shiro:hasPermission>
							<shiro:hasPermission name="order:order:edit">
		    					<a href="${ctx}/orderManager/order/form?action=edit&id=${order.id}" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
		    				</shiro:hasPermission>
		    					<a href="javascript:void(0);" onclick="toPrintPreview('${order.id}')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 打印</a>
		    				<shiro:hasPermission name="order:order:del">
								<a href="${ctx}/orderManager/order/delete?id=${order.id}" onclick="return confirmx('确认要删除该订货单吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
							</shiro:hasPermission>
						</td>
					</tr>
				</c:forEach>
			</c:otherwise>
		</c:choose>
		</tbody>
	</table>
	
		<!-- 分页代码 -->
	<table:page page="${page}"></table:page>
	<br/>
	<br/>
	</div>
	</div>
</div>
<script type="text/javascript" src="${ctxStatic}/easy-selector/easy-selector.js"></script>
<script type="text/javascript" src="${ctxStatic}/common/contabs.js"></script>
<script type="text/javascript">
function toPrintPreview(id) {
	openTab('${ctx}/orderManager/order/form?id=' + id + '&printPreview=Y', '订单打印预览');
}

/**
 * 获取订单状态
 */
function getOrderStatus() {
	var items = [];
	$.ajax({
		url : ctx + "/sys/dict/listData",
		async : false,
		type : "GET",
		data : {"type" : "mtOrderStatusCd"},
		dataType : "json",
		success : function(data) {
			if(data && data.length > 0) {
				$.each(data, function(index, status) {
					var item = {};
					item.value = status.value;
					item.text = status.label;
					items.push(item);
				});
			}
		}
	});	
	return items;
}
var items = getOrderStatus();
// 构建订单状态选择器
$("#orderStatus").easySelector({
	type : 'select',
	maxHeight : 250,
	defaults: {
		value : '',
		text : '请选择订单状态',
		selected: true
	},
	items : items
});
</script>
</body>
</html>