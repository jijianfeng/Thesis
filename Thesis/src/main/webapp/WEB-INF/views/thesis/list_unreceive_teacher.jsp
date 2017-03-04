<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
        <div id="grid-toolbar" class="clearfix p5">
            <span>所属大学:</span>
            <input type="text" disabled="true" id="universityName" name="universityName" style="width:130px" />
		    <button class="btn btn-xs btn-primary" id="look_university" type="button">查找</button>
		    <input type="hidden" id ="universitya" name="universitya" value="">
            <span class="ml10">论文名称：</span>
			<input class="mr10 w100" type="text" id="thesisName" placeholder="输入论文名称搜索" />
			<span class="ml10">论文序号：</span>
			<input class="mr10 w100" type="text" id="thesisCode" placeholder="输入论文序号搜索" />
			<span class="ml10">截止日期[起]：</span>
			<input class="jq-datebox" style="width:100px" name="timeStart" id="timeStart"></input>
			<span class="ml10">截止日期[止]：</span>
			<input class="jq-datebox" style="width:100px" name="timeEnd" id="timeEnd"></input>
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <button class="btn btn-sm btn-info" id="empty">清空</button>
            <a id="refuse" class="btn btn-sm btn-danger"><i class="icon icon-edit"></i>退回</a>
            <c:if test="${sessionScope.userType==0||sessionScope.userType==1}">             
            <a id="up" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>送审</a>
            </c:if>
            <c:if test="${sessionScope.userType==3||sessionScope.userType==4}">             
            <a id="upAdmin" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>送审</a>
            </c:if>
            <c:if test="${sessionScope.userType==2}">
            <a id="comments" class="btn btn-sm btn-success"><i class="icon icon-edit"></i>评论</a>
            <a id="downloadUser" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载对方评审模板</a>
            </c:if>
            <c:if test="${sessionScope.userType==0||sessionScope.userType==1||sessionScope.userType==2}">
            <a id="download" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载论文</a>
            </c:if>
            <a id="export" class="btn btn-sm btn-primary"><i class="icon icon-edit"></i>Excel导出</a>
        </div>
    </div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="unreceivelist" data-options="{
			url: '${z:u('thesis/list_unreceive_teacher')}',
			singleSelect:false,
			pageList:[20,50,100,500,1000,3000],
			method:'post',
			columns: [[
				{field:'id',checkbox:true},
				{field:'universityId',title:'所属大学',width:100 ,sortable:'true'},
				{field:'collegeId',title:'所属学院',width:100 ,sortable:'true'},
				{field:'majorTwo',title:'所属专业',width:100 ,sortable:'true'},
				{field:'thesisType',title:'论文类型',width:100 ,sortable:'true'},
				{field:'sendUser',title:'送审人',width:100 ,sortable:'true'},
				{field:'thesisCode',title:'论文序号',width:100 ,sortable:'true'},
				{field:'thesisName',title:'论文标题',width:100 ,sortable:'true'},
				{field:'researchDirection',title:'研究方向',width:100 ,sortable:'true'},
				{field:'studyType',title:'攻读方式',width:100 ,sortable:'true'},
 				{field:'uploadTime',title:'论文上传时间',width:100 ,sortable:'true'},
 				{field:'returnTime',title:'要求返回时间',width:100,formatter:formatPrice ,sortable:'true'},
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
	
	$("#up").on("click", function () {
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}else{
			var strIds=[];
			for(var i=0;i<row.length;i++){
				strIds.push(row[i].id);
			}
			var ids=strIds.join(",");
	        App.popup('${z:u("/thesis/list_up")}?id='+ids, {
	            title: "送审",
	            width: 450,
				height: 350
	        });
		}
    });
	
	$("#upAdmin").on("click", function () {
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请选择一条记录","warning");
		}else{
	        App.popup('${z:u("/thesis/list_up_admin")}?id='+row.id, {
	            title: "送审",
	            width: 400,
				height: 400
	        });
		}
    });
	
	$("#comments").click(function(){
		    var row = $(".jq-datagrid").datagrid("getSelections");
			if(row.length !=1){
				App.alert("请选择一条记录","warning");
				//alert(row.length);
			}else{
				if(row[0].isSendUrl==0){
					App.popup('${z:u("/thesis/list_comments")}?id='+row[0].id, {
						title: "评审",
						width: 700,
						height: 600
					});
					//$('.jq-datagrid').datagrid('clearSelections');
				}
				else{
					App.popup('${z:u("/thesis/list_comments_diy")}?id='+row[0].id, {
						title: "评审",
						width: 350,
						height: 120
					});
					//$('.jq-datagrid').datagrid('clearSelections');
				}
			}
	});
	
	$("#downloadUser").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请选择一条记录","warning");
		}else{
			window.location.href = '${z:u("/thesis/download_user")}?id='+row.id;
		}
});
	
	$("#empty").on("click", function() {
		document.getElementById('thesisName').value='';
		document.getElementById('thesisCode').value='';
		$('#timeStart').datebox("setValue", "");
		$('#timeEnd').datebox("setValue", "");
		$('#arealist').combobox('setValues','0');
		$('#universitylist').combobox('setValues','0');
	});

	$("#search").on("click", function() {
		var universityId = $("#universitya").val();
     	var thesisCode = $("#thesisCode").val();
     	var thesisName = $("#thesisName").val();
     	var timeStart = $("#timeStart").datebox("getValue");
		var timeEnd = $("#timeEnd").datebox("getValue");
    	$("#unreceivelist").datagrid({
			url:'${z:u("/thesis/list_unreceive")}?universityId='+universityId+"&thesisCode="+thesisCode+"&thesisName="+thesisName+"&timeStart="+timeStart+"&timeEnd="+timeEnd
		});
	});
	$("#refuse").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelections");
		var strIds=[];
		for(var i=0;i<row.length;i++){
			strIds.push(row[i].id);
		}
		var ids=strIds.join(",");
		if(row.length == 0){
			App.alert("请至少选择一条记录","warning");
		}else{
			App.ajax('${z:u("/thesis/list_refuse")}?id='+ids,{
 	        msg: "是否要拒绝该"+row.length+"论文",
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
	
	//四级联动
	
	$("#download").click(function(){
// 		var row = $(".jq-datagrid").datagrid("getSelected");
// 		if(row == null){
// 			App.alert("请选择一条记录","warning");
// 		}else{
// 			window.location.href = '${z:u("/thesis/download_result")}?id='+row.id;
// 		}
		var row = $(".jq-datagrid").datagrid("getSelections");
		if(row.length != 1){
			App.alert("请选择一条记录","warning");
		}else{
			window.location.href = '${z:u("/thesis/download_result")}?id='+row[0].id;
		}
	});
	
	$("#export").click(function(){
		window.location.href = '${z:u("/thesis/export_excel")}';
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