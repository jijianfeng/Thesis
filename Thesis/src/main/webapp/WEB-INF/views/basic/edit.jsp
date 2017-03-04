<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="left" class="form-table" >
    	<tr>
			<td>企业名称：</td>
			<td colspan="4" >
				<input class="w500" style="width:280px" name="name" class="jq-validatebox" value="${enterpriseEdit.name }" type="text" data-options="required:true">
			</td>
		</tr>
		<tr>
			<td>所属部门：</td>
			<td>
				<select class="jq-combobox" name="departmentId" data-options="{
					method:'post',
					editable:true,
					url: '${z:u('public/departmentlist')}'}">
				</select>
			</td>
			<td class="tr">企业法人：</td>
			<td>
				<input name="represent"  value="${enterpriseEdit.represent }" type="text" >
			</td>
		</tr>
		<tr>
			<td>联系人：</td>
			<td>
				<input name="contact"  value="${enterpriseEdit.contact }" type="text" >
			</td>
			<td class="tr">联系电话：</td>
			<td>
				<input name="contactphone"  value="${enterpriseEdit.contactphone }" type="text" >
			</td>
		</tr>
		<tr>
			<td>企业地址：</td>
			<td>
				<input name="address" class="jq-validatebox" value="${enterpriseEdit.address }" type="text" data-options="required:true"  >
			</td>
			<td class="tr">备注：</td>
			<td>
				<input name="introduce" class="jq-validatebox" value="${enterpriseEdit.introduce }" type="text" data-options="required:true"  >
			</td>
		</tr>
		<tr>
			<td>企业介绍：</td>
			<td colspan="4" ><textarea class="jq-validatebox" id="content" name="comment"
					style="width: 400px;height: 150px;" >${enterpriseEdit.comment }</textarea>
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
	<input name="id" value="${enterpriseEdit.id }" type="hidden"  >
</form>
<script>
	KindEditor.create("#editor",{
		uploadJson:'${z:u("public/upload")}'
	});
	App.ajaxForm("#form");
</script>