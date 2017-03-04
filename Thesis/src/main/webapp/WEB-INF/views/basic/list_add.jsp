<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" >
<!--     	<tr> -->
<!--     	    <td>所属省份：</td> -->
<!--     	    <td> -->
<!--     	        <select class="jq-combobox" name="areaId" data-options="{ -->
<!--     	        	required:'true', -->
<!-- 					method:'post', -->
<!-- 					editable:true, -->
<!-- 					url: '${z:u('public/arealist_nodefault')}'}"> -->
<!--     	        </select> -->
<!--     	    </td> -->
<!--     	</tr> -->
    	<tr>
			<td>学校名称：</td>
			<td colspan="3">
				<input name="universityName" class="jq-validatebox" type="text" data-options="required:true">
			</td>
		</tr>
		<tr>			
			<td class="tr">学校代码：</td>
			<td>
				<input name="universityCode" class="jq-validatebox"  type="text" data-options="required:true" >
			</td>
		</tr>
		<tr>			
			<td class="tr">联系人：</td>
			<td>
				<input name="linkMan" class="jq-validatebox"    type="text" data-options="required:true" >
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
			<td class="tr">是否是985院校：</td>
			<td>
				<input type="radio" id="i2" name="isNine" value="0">不是
	            <input type="radio" id="i1" name="isNine" value="1">是
			</td>
		</tr>
		<tr>
			<td class="tr">是否是211院校：</td>
			<td>
				<input type="radio" id="i2" name="isTwo" value="0">不是
	            <input type="radio" id="i1" name="isTwo" value="1">是
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
<!-- <script> -->
<!-- 	App.ajaxForm("#form"); -->
<!-- </script> -->