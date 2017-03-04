<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" >
    	<tr>
    	    <td>所属省份：</td>
    	    <td>
    	        <select class="jq-combobox" id="arealist1" name="areaId" data-options="{
    	        	required:'true',
					method:'post',
					editable:true,
					url: '${z:u('public/arealist_nodefault')}'}">
    	        </select>
    	    </td>
    	</tr>
    	<tr>
			<td>所属学校：</td>
			<td>
				<select class="jq-combobox" id="universitylist1" name="universityId" data-options="{
					required:'true',
					method:'post',
					editable:true,
					}">
				</select>
			</td>
		</tr>
		<tr>
			<td>所属学院：</td>
			<td>
				<select class="jq-combobox" id="collegelist1" name="collegeId" data-options="{
					required:'true',
					method:'post',
					editable:true,
					}">
				</select>
			</td>
		</tr>
		<tr>
			<td class="tr">专业名称：</td>
			<td>
				<input name="majorName" type="text" class="jq-validatebox"  data-options="required:true"  >
			</td>
		</tr>
		<tr>			
			<td class="tr">联系人：</td>
			<td>
				<input name="linkMan"  type="text"  class="jq-validatebox"  data-options="required:true"  >
			</td>
		</tr>
		<tr>		
			<td>联系电话：</td>
			<td>
				<input name="linkTel" class="jq-validatebox"  type="text" data-options="required:true"  >
			</td>
		</tr>
		<tr>
			<td class="tr">联系地址：</td>
			<td>
				<input name="linkAddress" class="jq-validatebox"  type="text" data-options="required:true"  >
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<button type="submit" class="btn btn-small btn-success">确定</button>
				<a class="btn btn-primary btn-small" href="">返回</a>
			</td>
		</tr>
	</table>
	<input name="id"  type="hidden"  >
</form>
<script>
$("#arealist1").combobox({
	editable:false,
	onChange:function(){
		var v = $("#arealist1").combobox("getValue");
		$('#universitylist1').combobox('reload', '${z:u('public/universitylist_nodefault')}'+"?areaId="+v);
		$('#universitylist1').combobox('setValues','');
	}
});
$("#universitylist1").combobox({
	editable:false,
	onChange:function(){
		var b = $("#universitylist1").combobox("getValue");
		$('#collegelist1').combobox('reload', '${z:u('public/collegelist_nodefault')}'+"?universityId="+b);
		$('#collegelist1').combobox('setValues','');
	}
});
</script>