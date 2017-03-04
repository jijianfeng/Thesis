<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
        <div id="grid-toolbar" class="clearfix p5">
	        <span class="ml10">所属学院：</span>
	        <select class="jq-combobox" id="collegelistwhere" name="collegelistwhere" style="width:100px" data-options="{
						required:true,
						method:'post',
						editable:true,
						url: '${z:u('public/collegelistwhere')}'}">
			</select>
	        <span class="ml10">所属专业：</span>
	        <select class="jq-combobox" id="majorlistwhere" name="collegelistwhere" style="width:100px" data-options="{
						required:true,
						method:'post',
						editable:false,
						url: '${z:u('public/major_list')}'
						}">
						</select>
            <span class="ml10">论文名称：</span>
			<input class="mr10 w100" type="text" id="thesisName" name="thesisName" placeholder="输入论文名称搜索" />
			<span class="ml10">论文序号：</span>
			<input class="mr10 w100" type="text" id="thesisCode" name="thesisCode" placeholder="输入论文序号搜索" />
			<span class="ml10">上传日期[起]：</span>
			<input class="jq-datebox" style="width:100px" id="timeStart" name="timeStart"></input>
			<span class="ml10">上传日期[止]：</span>
			<input class="jq-datebox" style="width:100px" id="timeEnd" name="timeEnd"></input>
			<span class="ml10">送审状态：</span>
			<select class="jq-combobox" panelHeight="auto" style="width:100px" id="status" name="status">
				<option value="0">全部</option>
				<option value="1">已送审</option>
				<option value="2">未送审</option>
			</select>
            <a id="search"  class="btn btn-sm btn-success"style="margin-left:10px;"><i class="icon icon-search"></i>搜索</a>          
            <button class="btn btn-sm btn-info" id="empty" >清空</button>
<!--             <a id="add" class="btn btn-sm btn-success"><i class="icon icon-add"></i >新增</a> -->
<!--             <a id="edit" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>编辑</a> -->
            <a id="detail" class="btn btn-sm btn-success">查看详细</a>
            <a id="delete" class="btn btn-sm btn-danger"><i class="icon icon-delete"></i>删除</a>
            <a id="send" class="btn btn-sm btn-success"><i class="icon icon-edit"></i>送审</a>
            <a id="sendpublic" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>送审到公共平台</a>
<!--             <a id="sendshortcut" class="btn btn-sm btn-success"><i class="icon icon-edit"></i>一键送审</a> -->
            <a id="process" class="btn btn-sm btn-warning"><i class="icon icon-edit"></i>查看送审状态</a>
            <c:if test="${sessionScope.userType==1}">
              <a id="shared" class="btn btn-sm btn-success">共享给研究生部</a>
            </c:if>
            <c:if test="${sessionScope.userType==0||sessionScope.userType==1}">            
              <a id="download" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载论文</a>
            </c:if>
            <a id="export" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>Excel导出</a>
        </div>
    </div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="thesisList" data-options="{
 			url: '${z:u('thesis/list_oto')}', 
			method:'post',
			singleSelect:false,
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
				{field:'thesisCode',title:'学号',width:100 ,sortable:'true'},
				{field:'thesisName',title:'论文名称',width:100 ,sortable:'true'},
				{field:'thesisType',title:'学位级别',width:100 ,sortable:'true'},	
				{field:'collegeId',title:'所属学院',width:100 ,sortable:'true'},
				{field:'majorOne',title:'一级专业',width:100 ,sortable:'true'},
				{field:'majorTwo',title:'二级专业',width:100 ,sortable:'true'},
				{field:'researchDirection',title:'研究方向',width:100 ,sortable:'true'},
				{field:'studyType',title:'攻读方式',width:100 ,sortable:'true'},
 				{field:'uploadTime',title:'上传日期 ',width:80 ,sortable:'true'},
 				{field:'hadCom',title:'已经评审次数',width:100 ,sortable:'true'},
 				{field:'sendNumber',title:'已经送审次数',width:100 ,sortable:'true'},
 				<c:if test="${sessionScope.userType==1}">
 				{field:'sendType',title:'有无共享给研究生部',width:100,formatter:App.statusFmt ,sortable:'true'},
 				</c:if>
 				<c:if test="${sessionScope.userType==0}">
 				{field:'sendType',title:'是否来自学院共享',width:100,formatter:App.statusFmt ,sortable:'true'},
 				</c:if>
			]]}">
		</table>
	</div>
</div>
<script type="text/javascript">
	$("#search").on("click", function() {
// 		var wd = $("#departmentId").combobox("getValue");
		var collegeId = $("#collegelistwhere").combobox("getValue");
		var majorId = $("#majorlistwhere").combobox("getValue");
		//alert(collegeId+";"+majorId);
		var thesisName = $("#thesisName").val();
		var thesisCode = $("#thesisCode").val();
		var timeStart = $("#timeStart").datebox("getValue");
		var timeEnd = $("#timeEnd").datebox("getValue");
		var status = $("#status").combobox("getValue");
		//alert(timeStart+":::"+timeEnd);
		var date1 = new Date(timeStart);
		var date2 = new Date(timeEnd);
		//alert(date.getTime()/1000);
		if(date1.getTime()>=date2.getTime()){
			alert("起始时间必须大于截止时间");
			return false;
		}
		$("#thesisList").datagrid({
			url:'${z:u("/thesis/list_oto")}?thesisName='+thesisName+"&thesisCode="+thesisCode+"&timeStart="+timeStart+"&timeEnd="+timeEnd+"&status="+status+"&collegeId="+collegeId+"&majorId="+majorId
		});
	});
	
	$("#empty").on("click", function() {
		document.getElementById('thesisName').value='';
		document.getElementById('thesisCode').value='';
		$('#timeStart').datebox("setValue", "");
		$('#timeEnd').datebox("setValue", "");
		$('#status').combobox('setValues','0');
		$('#collegelistwhere').combobox('setValues','0');
		$('#majorlistwhere').combobox('setValues','0');
	});
	
	$("#send").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
// 		alert(row.length);
// 		alert(row[0].id);
		var strIds=[];
		for(var i=0;i<row.length;i++){
			strIds.push(row[i].id);
		}
		var ids=strIds.join(",");
// 		alert(ids);
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}else{
			App.popup('${z:u("/thesis/list_send_to")}?id='+ids, {
				title: "送审",
				width: 500,
				height: 320
			});
		}
	});
	
	$("#process").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length != 1){
			App.alert("请选择一条记录查看","warning");
		}else{
			if(row[0].sendNumber==0){
				App.alert("该论文还没有被送审","warning");
			}
			else{
				App.popup('${z:u("/thesis/list_process")}?id='+row[0].id, {
					title: "送审情况",
					width: 860,
					height: 600
				});
			}
		}
	});
	
	$("#sendpublic").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
// 		alert(row.length);
// 		alert(row[0].id);
		var strIds=[];
		for(var i=0;i<row.length;i++){
			strIds.push(row[i].id);
		}
		var ids=strIds.join(",");
		if(row.length==0){
			App.alert("请至少选择一条记录","warning");
		}else{
			App.popup('${z:u("/thesis/list_send_public")}?id='+ids, {
				title: "送审到公共平台",
				width: 350,
				height: 260
			});
		}
	});
	
	$("#sendshortcut").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
// 		alert(row.length);
// 		alert(row[0].id);
		var strIds=[];
		for(var i=0;i<row.length;i++){
			strIds.push(row[i].id);
		}
		var ids=strIds.join(",");
// 		alert(ids);
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}else{
			App.popup('${z:u("/thesis/list_send_shortcut")}?id='+ids, {
				title: "一键送审",
				width: 800,
				height: 570
			});
		}
	});
	
	$("#shared").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}
		var number= 0;
		for(var i=0;i<row.length;i++){
			if(row[i].isShared==1){
				number++;
			}
		}
		if(number!=0){
			App.alert("你选中的论文中已经有"+number+"篇被共享,请检查！","warning");
		}
		else{
			var strIds=[];
			for(var i=0;i<row.length;i++){
				strIds.push(row[i].id);
			}
			var ids=strIds.join(",");
			App.ajax('${z:u("/thesis/list_shared")}?id='+ids,{
	            type: "POST",
	            msg: "是否确认共享给研究生部？<br>注意：确认后不能取消！"
	            });
		}
	});
	
	$("#download").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length != 1){
			App.alert("请选择一条记录","warning");
		}else{
			window.location.href = '${z:u("/thesis/download")}?id='+row[0].id;
		}
	});
	
	$("#export").click(function(){
		window.location.href = '${z:u("/thesis/export_excel")}';
	});

	//四级联动
// 	$("#arealist").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var v = $("#arealist").combobox("getValue");
// 			$('#universitylist').combobox('reload', '${z:u('public/universitylist')}'+"?areaId="+v);
// 		}
// 	});
	
// 	$("#universitylist").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var b = $("#universitylist").combobox("getValue");
// 			$('#collegelist').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
// 		}
// 	});
	
// 	$("#collegelist").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var n = $("#collegelist").combobox("getValue");
// 			$('#majorlist').combobox('reload', '${z:u('public/majorlist')}'+"?collegeId="+n);
// 		}
// 	});
	
	$("#add").click(function(){
		App.popup('${z:u('thesis/list_add')}',{
			title:"论文添加",
			width: 800,
			height: 500
		});
	});
	
	$("#detail").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length != 1){
			App.alert("请只选择一条记录","warning");
		}else{
			App.popup('${z:u("/thesis/list_detail")}?id='+row[0].id, {
				title: "论文详情",
				width: 800,
				height: 490
			});
		}
	});                                                                                                                                                                                                                 
	
	$("#edit,#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length == 0){
			App.alert("请至少先选择一条记录","info");
		}
		else{
			var eleId = $(this).attr("id");
			if(eleId=="edit"){
				App.popup('${z:u("/thesis/list_edit")}?id='+row.id,{
					title:"编辑",
					width: 800,
					height: 500
				});
			}else if(eleId=="delete"){
				var strIds=[];
				for(var i=0;i<row.length;i++){
					strIds.push(row[i].id);
				}
				var ids=strIds.join(",");
	            App.ajax('${z:u("/thesis/list_delete")}?id='+ids,{
	            msg: "确定要删除所选数据？",
	            type: "POST",
	            success : function(data) {
	            	$('.jq-datagrid').datagrid('clearSelections');
				},
				error : function(err) {
					alert("错就是错");
				}
	            });
			}
		}
	});
</script>

