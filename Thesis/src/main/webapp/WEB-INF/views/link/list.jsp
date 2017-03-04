<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
        <div id="grid-toolbar" class="clearfix p5">
        	<span class="ml10">问题名称：</span>
			<input class="mr10" id="title" type="text"  placeholder="输入问题名称搜索" />
			<span>状态:</span>
        	<select class="jq-combobox" panelHeight="auto" style="width:100px" id="status">
				<option value="0">不限</option>
				<option value="1">未解决</option>
				<option value="2">已解决</option>
			</select>
			<a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
			<a id="question" class="btn btn-sm btn-info">我要提问</a>
        	<a id="detail" class="btn btn-sm btn-warning">查看与回复</a>
        	<c:if test="${sessionScope.userType==3}">
            <a id="delete" class="btn btn-sm btn-danger">删除</a>
            </c:if>
        </div>
    </div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="list" data-options="{
 			url: '${z:u('link/list')}',
			method:'post',
			columns: [[
				{field:'id',checkbox:true},
				{field:'questionTitle',title:'标题',width:20},
				{field:'questionContent',title:'内容',width:20},
				{field:'questioner',title:'提问人',width:20},
				{field:'uploadTime',title:'提问时间',width:20},
				{field:'isSolved',title:'已解决',width:20,formatter:App.statusFmt},
			]]}">
		</table>
	</div>
</div>
<script type="text/javascript">
	
	$("#search").on("click", function() {
		var status = $("#status").combobox("getValue");
	 	var title = $("#title").val();
		$("#list").datagrid({
			url:'${z:u("/link/list")}?title='+title+"&status="+status
		});
	});
	
	$("#question").click(function(){
		App.popup('${z:u('link/list_add')}',{
			title:"我要提问",
			width: 800,
			height: 500
		});
	});

	$("#detail").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
	if(row==null){
		$.messager.alert("提示","请先选择一条记录","warning");
	}else{
 			var eleId = $(this).attr("id");
 			App.popup('${z:u('link/list_solve')}?id='+row.id, {
 				title:"我要回答",
			width: 800,
     		height: 550
			});
		}
 	});
	
	$("#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","info");
		}else{
			App.ajax('${z:u("/link/list_delete")}?id='+row.id,{
	            msg: "确定要删除此条回复吗？",
	            type: "POST"
	            });
		}
	});
 	

	
</script>