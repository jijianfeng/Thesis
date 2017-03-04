<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		<div id="grid-toolbar" class="clearfix p5">
           <span>专业名称:</span>
            <input class="ml10 w100" type="text" placeholder="输入专业名称" id="major" name="search_text" />
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <c:if test="${sessionScope.userType==3}">
            </c:if>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid J_grid" fit="true" id="case_grid" data-options="{
 			url: '${z:u('basic/major')}', 
			method:'post',
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
				{field:'majorName',title:'专业总称'},
				{field:'majorOneCode',title:'一级专业代码',width:100 },
				{field:'majorOneName',title:'一级专业名称',width:100 },
				{field:'majorTwoCode',title:'二级专业代码',width:100 },
				{field:'majorTwoName',title:'二级专业名称',width:100 },
			]],
			onLoadSuccess: gridLoad
		}">
		</table>
	</div>
</div>
<script type="text/javascript">

	$("#search").on("click", function() {
     	var major = $("#major").val();
	    $("#case_grid").datagrid({
	        url: '${z:u('basic/major')}?major='+major
	    });
	});

	$("#edit,#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","info");
		}else{
			var eleId = $(this).attr("id");
			if(eleId=="edit"){
				App.popup('${z:u("/basic/major_edit")}?id='+row.id,{
					title:"编辑",
					width: 400,
					height: 400
				});
			}else if(eleId=="delete"){
	            App.ajax('${z:u("/basic/major_delete")}?id='+row.id,{
	            msg:"你确定要删除所选数据吗？",
	            type: "POST"
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
		App.popup('${z:u('basic/major_add')}',{
			title:"企业添加",
			width: 400,
			height: 400
		});
	});
	//联动
	$("#arealist").combobox({
		editable:false,
		onChange:function(){
			var v = $("#arealist").combobox("getValue");
			$('#universitylist').combobox('reload', '${z:u('public/universitylist')}'+"?areaId="+v);
			$('#universitylist').combobox('setValues','0');
		}
	});
	
	$("#universitylist").combobox({
		editable:false,
		onChange:function(){
			var b = $("#universitylist").combobox("getValue");
			$('#collegelist').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
			$('#collegelist').combobox('setValues','0');
		}
	});
	
	$("#collegelist").combobox({
		editable:false,
		onChange:function(){
			var n = $("#collegelist").combobox("getValue");
			$('#majorlist').combobox('reload', '${z:u('public/majorlist')}'+"?collegeId="+n);
			$('#majorlist').combobox('setValues','0');
		}
	});
	
	
</script>