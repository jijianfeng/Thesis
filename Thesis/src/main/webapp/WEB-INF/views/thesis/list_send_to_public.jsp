<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" style="margin:40px 50px">
    	<tr>
	   		<td><span>所属区域:</span></td>
	   		<td>
	       	<select class="jq-combobox" id="arealist" name="area" style="width:150px" data-options="{
	       			required:true,
					method:'post',
					editable:true,
					url: '${z:u('public/arealist_nodefault')}'}">
			</select>
			</td>
		</tr>
		<tr>
	       	<td><span>所属学校:</span></td> 
	       	<td>
			<select class="jq-combobox" require="true" id="universitylist" name="university" style="width:150px" data-options="{
					required:true,
					method:'post',
					editable:true,
					}">
			</select>
			<a id="userDetail" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>查看负责人</a>
			</td>
		</tr>
		<tr>
			<td><span>所属院系:</span></td>
	       	<td><select class="jq-combobox" id="collegelist" name="college" style="width:150px" data-options="{
	       			required:true,
					method:'post',
					editable:false,
					}">
			</select>
			</td>
		</tr>
		<tr>
			<td><span>送审码:</span></td>
	       	<td>
	       		<input type="text" name="sendCode" class="mr10 w140">
			</td>
		</tr>
		<tr>
			<td><label>截止日期：</label></td>
			<td><input class="jq-datebox"  style="width:150px" name="returnTime" data-options="required:true"/></td>
		</tr>
		<tr>
			<td>&nbsp;
			<input type="hidden" value="${had}" name="had" id="had">
			<input type="hidden" value="${ids}" name="ids" id="ids">
			</td>
			<td>
				<button type="submit" class="btn btn-small btn-success">确定</button>
				<a class="btn btn-primary btn-small" href="">返回</a>
			</td>
		</tr>
	</table>
</form>
<script type="text/javascript">

	//联动
	$("#arealist").combobox({
		editable:false,
		onChange:function(){
			var v = $("#arealist").combobox("getValue");
			$('#universitylist').combobox('reload', '${z:u('public/universitylist_nodefault')}'+"?areaId="+v);
			$('#universitylist').combobox('setValues','');
		}
	});
	
	$("#universitylist").combobox({
		editable:false,
		onChange:function(){
			var b = $("#universitylist").combobox("getValue");
			$('#collegelist').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
			$('#collegelist').combobox('setValues','');
		}
	});
	
	$("#collegelist").combobox({
		editable:false,
		onChange:function(){
			var a = $("#universitylist").combobox("getValue");
			var b = $("#collegelist").combobox("getValue");
			var hads = $("#had").val();
			var ids = $("#ids").val();
			if(!isNaN(b)){
				//已经送审的信息
				var had= new Array(); //定义一数组 
				had=hads.split("||"); //字符分割 
				//当前信息
				var id= new Array(); //定义一数组 
				id=ids.split(","); //字符分割 
				var number = 0;
				for(var i=0;i<had.length ;i++ ){ 
					//alert(had[i]);
					for(var j=0;j<id.length ;j++ ){ 
						var check = id[j]+","+a+","+b;
						//alert("check"+check+"::::;"+had[i]);
						if(check == had[i]){
							number = number + 1;
						}
					}
				}
				if(number!=0){
					$.messager.alert('重复送审！','您已经送审过 '+number+' 篇论文到该学校学院！(您可以重复送审！,本消息仅做提醒！)','warning');
				}
			}
		}
	});
	
	$("#userDetail").click(function(){
		var universityId = $("#universitylist").combobox("getValue");
		var collegeId = $("#collegelist").combobox("getValue");
 		//alert(universityId+":"+collegeId);
		if(isNaN(collegeId)){
			alert("请至少选择一个学校学院！");
		}
		else{
			App.dialog({
				title: "详情",
				href: "${z:u('thesis/fand_user')}?universityIda=" + universityId+"&collegeIda="+collegeId,
				width: 800,
				height: 400
			});
		}
	});

</script>