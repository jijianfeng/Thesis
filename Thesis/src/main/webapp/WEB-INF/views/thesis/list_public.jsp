<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
        <div id="grid-toolbar" class="clearfix p5">
            <span>送审大学:</span>
            <input type="text" disabled="true" id="universityName" name="universityName" style="width:130px" />
		    <button class="btn btn-xs btn-primary" id="look_university" type="button">查找</button>
		    <input type="hidden" id ="universitya" name="universitya" value="">
            <span class="ml10">论文名称：</span>
			<input class="mr10 w100" type="text" id="thesisName" placeholder="输入论文名称搜索" />
			<span class="ml10">论文序号：</span>
			<input class="mr10 w100" type="text" id="thesisCode" placeholder="输入论文序号搜索" />
			<span class="ml10">送审日期[起]：</span>
			<input class="jq-datebox" style="width:100px" id="timeStart"></input>
			<span class="ml10">送审日期[止]：</span>
			<input class="jq-datebox" style="width:100px" id="timeEnd"></input>
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <button class="btn btn-sm btn-danger" id="empty">清空</button>
            <a id="download" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载论文</a>
            <c:if test="${sessionScope.userType!=3&&sessionScope.userType!=4}">
            	<a id="accept" class="btn btn-sm btn-success"><i class="icon icon-accept"></i>接收</a>
            </c:if>
            <c:if test="${sessionScope.userType==3||sessionScope.userType==4}">
            	<a id="upAdmin" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>送审</a>
            </c:if>
            <a id="export" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>Excel导出</a>
<!--             <a id="importent" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>设为重点项目</a> -->
        </div>
    </div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="list_public" data-options="{
			url: '${z:u('thesis/list_public')}',
			method:'post',
			singleSelect:false,
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
				{field:'universityId',title:'学校名称',width:100,sortable:'true'},
				{field:'collegeId',title:'学院名称',width:100,sortable:'true'},
				{field:'majorTwo',title:'专业名称',width:100,sortable:'true'},
				{field:'thesisCode',title:'论文序号',width:80,sortable:'true'},
				{field:'thesisName',title:'论文标题',width:100,sortable:'true'},
				{field:'thesisType',title:'论文类型',width:100,sortable:'true'},
				{field:'researchDirection',title:'研究方向',width:100 ,sortable:'true'},
				{field:'studyType',title:'攻读方式',width:100 ,sortable:'true'},
  			    {field:'uploadTime',title:'上传时间',width:80,sortable:'true'}, 
  			    {field:'returnTime',title:'返回时间',width:80,sortable:'true',formatter:formatPrice},
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
	
	$("#download").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请选择一条记录","warning");
		}else{
			window.location.href = '${z:u("/thesis/download_result")}?id='+row.id;
		}
	});
	$("#search").on("click", function() {
// 		var areaId = $("#arealist").combobox("getValue");
		var universityId = $("#universitya").val();
     	var thesisCode = $("#thesisCode").val();
     	var thesisName = $("#thesisName").val();
     	var timeStart = $("#timeStart").datebox("getValue");
		var timeEnd = $("#timeEnd").datebox("getValue");
    	$("#list_public").datagrid({
			url:'${z:u("/thesis/list_public")}?'+"&universityId="+universityId+"&thesisCode="+thesisCode+"&thesisName="+thesisName+"&timeStart="+timeStart+"&timeEnd="+timeEnd
		});
	});
	
	$("#upAdmin").on("click", function () {
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}else{
			var strIds=[];
			for(var i=0;i<row.length;i++){
				strIds.push(row[i].id);
			}
			var ids=strIds.join(",");
	        App.popup('${z:u("/thesis/list_up_admin")}?id='+ids, {
	            title: "送审",
	            width: 400,
				height: 400
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
	
	$("#accept").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length == null){
			App.alert("请先选择一条记录","warning");
		}
		else{
			var strIds=[];
			for(var i=0;i<row.length;i++){
				strIds.push(row[i].id);
			}
			var ids=strIds.join(",");
		    App.ajax('${z:u("thesis/accept")}?id='+ ids, {
		        msg: "确定要收为自己的论文吗？<br>注意：来自公共的平台的论文一旦接收将不能退回！",
		        type: "POST",
		        success : function(data) {
	            	$('.jq-datagrid').datagrid('clearSelections');
				},
				error : function(err) {
					alert("错就是错");
				}
		    });
		}
	});

	$("#export").click(function(){
		window.location.href = '${z:u("/thesis/export_excel")}';
	});
	
	//四级联动
	$("#arealist").combobox({
		editable:false,
		onChange:function(){
			var v = $("#arealist").combobox("getValue");
			$('#universitylist').combobox('reload', '${z:u('public/universitylist')}'+"?areaId="+v);
			$('#universitylist').combobox('setValues','0');
			$('#collegelist').combobox('setValues','0');
			$('#majorlist').combobox('setValues','0');
		}
	});
	
	$("#universitylist").combobox({
		editable:false,
		onChange:function(){
			var b = $("#universitylist").combobox("getValue");
			$('#collegelist').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
			$('#collegelist').combobox('setValues','0');
			$('#majorlist').combobox('setValues','0');
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
	function formatPrice(val,row){
		if (row.red == 1){
			return '<span style="color:red;">'+val+'</span>';
		} 
		else if (row.red == 2){
			return '<span style="color:#FF8C00;">'+val+'</span>';
		}
		else if (row.red == 3){
			return '<span style="color:black;">'+val+'</span>';
		}
		else{
			return val;
		}
	}
</script>
<script type="text/javascript">
		//jquery 日期选择
		function myformatter(date){
			var y = date.getFullYear();
			var m = date.getMonth()+1;
			var d = date.getDate();
			return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
		}
		function myparser(s){
			if (!s) return new Date();
			var ss = s.split('-');
			var y = parseInt(ss[0],10);
			var m = parseInt(ss[1],10);
			var d = parseInt(ss[2],10);
			if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
				return new Date(y,m-1,d);
			} else {
				return new Date();
			}
		}
	</script>