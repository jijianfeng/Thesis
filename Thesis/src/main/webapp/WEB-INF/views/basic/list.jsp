<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		<div id="grid-toolbar" class="clearfix p5">
			<span class="ml10">学校代码：</span>
			<input class="ml10 w100" type="text" placeholder="输入学校代码搜索" id="universityCode" name="universityCode" />
			<span class="ml10">学校名称：</span>
			<input class="ml10 w200" type="text" placeholder="输入学校名称搜索" id="university" name="university" />
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <c:if test="${sessionScope.userType==3}">
            <a id="add" class="btn btn-sm btn-success"><i class="icon icon-add"></i>新增</a>
            <a id="edit" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>编辑</a>
            <a id="delete" class="btn btn-sm btn-danger"><i class="icon icon-delete"></i>删除</a>
            </c:if>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid J_grid" fit="true" id="list" data-options="{
 			url: '${z:u('basic/list')}', 
			method:'post',
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
 				{field:'universityCode',title:'学校代码',width:60}, 
				{field:'universityName',title:'学校名称',width:150},
				{field:'linkMan',title:'联系人',width:60},
				{field:'linkAddress',title:'联系地址',width:100},
				{field:'linkTel',title:'联系电话',width:60},
				{field:'isNine',title:'是否是985院校',width:60,formatter:App.statusFmt},
				{field:'isTwo',title:'是否是211院校',width:60,formatter:App.statusFmt},
			]],
			onLoadSuccess: gridLoad
		}">
		</table>
	</div>
</div>
<script type="text/javascript">
	function gridLoad() {
		$(".J_imgShow").each(function() {
			$(this).fancybox();
		});
	}
	$("#search").on("click", function() {
// 		var areaId = $("#arealist").combobox("getValue");
     	var university = $("#university").val();
     	var universityCode = $("#universityCode").val();
    	$("#list").datagrid({
			url:'${z:u("/basic/list")}?university='+university +"&universityCode="+universityCode
		});
	});

	$("#edit,#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","info");
		}else{
			var eleId = $(this).attr("id");
			if(eleId=="edit"){
				App.popup('${z:u("/basic/list_edit")}?id='+row.id,{
					title:"编辑",
					width: 400,
					height: 400
				});
			}else if(eleId=="delete"){
	            App.ajax('${z:u("/basic/list_delete")}?id='+row.id,{
		            msg: "确定要删除所选数据？<br>注意：改学校下的所有学院专家均会被删除！",
		            type: "POST",
	                success : function(result) {
	 					//alert("已经发送短信到你绑定的手机上！");
	                	$('.jq-datagrid').datagrid('clearSelections');
	 				}
	            });
			}
		}
	});
	
	$("#enable").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","warning");
		}else{
	            App.ajax('${z:u("/enterprise/enable")}?id='+row.id,{
	            type: "POST"
				});
		}
	});
	
	$("#detail").on("click", function() {
		var row = $("#case_grid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一个项目","warning");
		}else{
			var id = row.id;
			App.popup('${z:u("/enterprise/details")}?id='+id,"查看详情");	
		}
	});
	
	$("#add").click(function(){
		App.popup('${z:u('basic/list_add')}',{
			title:"学校添加",
			width: 400,
			height: 440
		});
	});
	
	
</script>