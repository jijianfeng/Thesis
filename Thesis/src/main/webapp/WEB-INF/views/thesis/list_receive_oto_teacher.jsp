<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
        <div id="grid-toolbar" class="clearfix p5">
        	<span>所属学校:</span>
			<input type="text" disabled="true" id="universityName" name="universityName" style="width:130px" />
		    <button class="btn btn-xs btn-primary" id="look_university" type="button">查找</button>
		    <input type="hidden" id ="universitya" name="universitya" value="">
            <span class="ml10" >论文名称：</span>
			<input class="mr10 w100" type="text" id="thesisName" placeholder="输入论文名称搜索" />
			<span class="ml10">论文序号：</span>
			<input class="mr10 w100" type="text" id="thesisCode" placeholder="输入论文序号搜索" />
			<span class="ml10">截止日期[起]：</span>
			<input class="jq-datebox" style="width:100px" id="timeStart"></input>
			<span class="ml10">截止日期[止]：</span>
			<input class="jq-datebox" style="width:100px" id="timeEnd"></input>
<!-- 			<span>论文状态:</span> -->
<!--         	<select class="jq-combobox" panelHeight="auto" style="width:100px" id="status"> -->
<!-- 				<option value="0">全部</option> -->
<!-- 				<option value="1">未评审完成</option> -->
<!-- 				<option value="2">评审完成</option> -->
<!-- 			</select> -->
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <button class="btn btn-sm btn-info" id="empty">清空</button>
<!--             <a id="download" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载论文</a>  -->
			<a id="detail" class="btn btn-sm btn-success">查看评审状况</a>
            <a id="export" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>导出Excel</a>
        </div>
    </div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="receivelist" data-options="{
			url: '${z:u('thesis/list_receive_oto_teacher')}',
			method:'post',
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
				{field:'universityId',title:'所属大学',width:100,sortable:'true'},
				{field:'collegeId',title:'所属学院',width:100,sortable:'true'},
				{field:'majorId',title:'所属专业',width:100,sortable:'true'},
				{field:'thesisType',title:'论文类型',width:100,sortable:'true'},
				{field:'uploadPeople',title:'送审人',width:100 ,sortable:'true'},
				{field:'thesisCode',title:'论文序号',width:100 ,sortable:'true'},
				{field:'thesisName',title:'论文标题',width:100 ,sortable:'true'},
				{field:'researchDirection',title:'研究方向',width:100 ,sortable:'true'},
				{field:'studyType',title:'攻读方式',width:100 ,sortable:'true'},
 				{field:'uploadTime',title:'论文上传时间',width:100,sortable:'true'},
 				{field:'returnTime',title:'截止时间',width:100,sortable:'true'},
 				{field:'status',title:'论文状态',width:100,sortable:'true'},
			]]}">
		</table>
	</div>
</div>
<script type="text/javascript">
	$("#look_university").on("click", function() {
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=7')}",
			width: 800,
			height: 600
		});
	});
	
	$("#export").click(function(){
		window.location.href = '${z:u("/thesis/export_excel")}';
	});
	
	$("#detail").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length != 1){
			App.alert("请选择一条记录","warning");
		}else{
			App.popup('${z:u("/thesis/list_result_detail")}?id='+row[0].id, {
				title: "论文详情",
				width: 500,
				height: 200
			});
		}
	});
	
	$("#empty").on("click", function() {
		document.getElementById('universityName').value='';
		document.getElementById('universitya').value='';
		document.getElementById('thesisName').value='';
		document.getElementById('thesisCode').value='';
		$('#timeStart').datebox("setValue", "");
		$('#timeEnd').datebox("setValue", "");
	});
	
	$("#search").on("click", function() {
		var universityId = $("#universitya").val();
     	var thesisCode = $("#thesisCode").val();
     	var thesisName = $("#thesisName").val();
     	var timeStart = $("#timeStart").datebox("getValue");
		var timeEnd = $("#timeEnd").datebox("getValue");
    	$("#receivelist").datagrid({
			url:'${z:u("/thesis/list_receive_oto_teacher")}?'+"&universityId="+universityId+"&thesisCode="+thesisCode+"&thesisName="+thesisName+"&timeStart="+timeStart+"&timeEnd="+timeEnd
		});
	});
	
	//四级联动
	
</script>