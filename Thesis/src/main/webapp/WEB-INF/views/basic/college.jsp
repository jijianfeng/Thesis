<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		<div id="grid-toolbar" class="clearfix p5">
			<span>所属大学:</span>
			<input type="text" disabled="true" id="universityName" name="universityName" style="width:160px" />
		    <button class="btn btn-xs btn-primary" id="look_university" type="button">查找</button>
		    <input type="hidden" id ="universitya" name="universitya" value="">
			<span>学院名称:</span>
            <input class="ml10 w200" type="text" placeholder="输入学院名称搜索" id="college" name="search_text" />
            <a id="search" class="btn btn-sm btn-success"><i class="icon icon-search"></i>搜索</a>
            <button class="btn btn-sm btn-info" id="empty" >清空</button>
            <c:if test="${sessionScope.userType==3}">
            <a id="add" class="btn btn-sm btn-success"><i class="icon icon-add"></i>新增</a>
            <a id="edit" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>编辑</a>
            <a id="delete" class="btn btn-sm btn-danger"><i class="icon icon-delete"></i>删除</a>
            </c:if>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid J_grid" fit="true" id="list" data-options="{
 			url: '${z:u('basic/college')}', 
			method:'post',
			pageList:[20,50,100,500,1000,3000],
			columns: [[
				{field:'id',checkbox:true},
				{field:'universityCode',title:'所属学校代码',width:15},
				{field:'university',title:'所属学校',width:15},
				{field:'collegeName',title:'学院名称',width:15},
				{field:'linkMan',title:'学院联系人',width:15},
				{field:'linkAddress',title:'联系地址',width:25},
				{field:'linkTel',title:'联系方式',width:15},
			]],
 			onLoadSuccess: gridLoad 
		}">
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
	
	$("#search").on("click", function() {
     	var universityId = $("#universitya").val();
     	var college = $("#college").val();
    	$("#list").datagrid({
			url:'${z:u("/basic/college")}?universityId='+universityId+"&college="+college
		});
	});

	$("#edit,#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","info");
		}else{
			var eleId = $(this).attr("id");
			if(eleId=="edit"){
				App.popup('${z:u("/basic/college_edit")}?id='+row.id,{
					title:"编辑",
					width: 400,
					height: 400
				});
			}else if(eleId=="delete"){
	            App.ajax('${z:u("/basic/college_delete")}?id='+row.id,{
	            	msg:"你确定要删除所选中的数据吗？<br>删除学院会同时删除学院下的所有专家！",
	            	type: "POST"
	            });
				
			}
		}
	});
	
	
	$("#add").click(function(){
		App.popup('${z:u('basic/college_add')}',{
			title:"学院添加",
			width: 400,
			height: 350
		});
	});
	
	$("#arealist").combobox({
		editable:false,
		onChange:function(){
			var v = $("#arealist").combobox("getValue");
			$('#universitylist').combobox('reload', '${z:u('public/universitylist')}'+"?areaId="+v);
			$('#universitylist').combobox('setValues','0');
		}
	});
	
	$("#empty").on("click", function() {
		document.getElementById('universityName').value='';
		document.getElementById('college').value='';
		document.getElementById('universitya').value='';
		document.getElementById('thesisCode').value='';
	});

</script>