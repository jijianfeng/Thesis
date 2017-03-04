<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="left" class="form-table" style="margin:10px 50px">
    	<tr>
    	    <td style="width:100px">大学：</td>
    	    <td>
    	       <input type="text" disabled="true" id="universityNamea" name="universityName" style="width:160px" />
			   <button class="btn btn-xs btn-primary" id="look_university" type="button">查找</button>
			   <input type="hidden" id ="universityaa" name="universitya" value="">
    	    </td>
    	</tr>
    	<tr>
    	    <td>学院：</td>
    	    <td>
    	        <select class="jq-combobox" id="collegelista" name="collegea" style="width:160px" data-options="{
    	        	required:true,
					method:'post',
					editable:true,
<!-- 					url: '${z:u('public/collegelistwhere_nodefault')}' -->
					}">			
    	        </select>
    	        <a id="userDetail" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>查看负责人</a>
    	    </td>
    	</tr>
    	<a style="color: red;font-size:30;">注意：送审码可以查看负责人信息打电话索取，如账号未激活请直接填对方手机号码！！</a>	
		<tr>
			<td>
			<span>送审码/手机号码:</span></td>
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
	$("#look_university").on("click", function() {
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=6')}",
			width: 800,
			height: 600
		});
	});
	//联动
	
	$("#universitylist").combobox({
		editable:false,
		onChange:function(){
			var b = $("#universitylist").combobox("getValue");
			$('#collegelista').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
			$('#collegelista').combobox('setValues','0');
		}
	});
	
	$("#collegelista").combobox({
		editable:false,
		onChange:function(){
			var a = $("#universitya").val();
			var b = $("#collegelista").combobox("getValue");
			var hads = $("#had").val();
			var ids = $("#ids").val();
// 			alert();
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
		var universityId = $("#universityaa").val();;
		var collegeId = $("#collegelista").combobox("getValue");
//  		alert(universityId+":"+collegeId);
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
	
	$(document).ready(function(){  
	    $("#form").bind("submit", function(){  
	    	$('.jq-datagrid').datagrid('clearSelections');
	    	return true;
	    })
	})

</script>