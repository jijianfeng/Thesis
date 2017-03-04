<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" >
    	<tr>
			<td>学校名称：</td>
			<td colspan="3">
				<input name="universityName" value="${university.universityName}" class="jq-validatebox" type="text" data-options="required:true">
			</td>
		</tr>
		<tr>			
			<td class="tr">学校代码：</td>
			<td>
				<input name="universityCode" class="jq-validatebox" value="${university.universityCode}"  type="text" data-options="required:true" >
			</td>
		</tr>
		<tr>			
			<td class="tr">联系人：</td>
			<td>
				<input name="linkMan" value="${university.linkMan }"  class="jq-validatebox" type="text" data-options="required:true" >
			</td>
		</tr>
		<tr>		
			<td>联系电话：</td>
			<td>
				<input name="linkTel" class="jq-validatebox" value="${university.linkTel}"   type="text" data-options="required:true"  >
			</td>
		</tr>
		<tr>
			<td class="tr">联系地址：</td>
			<td>
				<input name="linkAddress" class="jq-validatebox" value="${university.linkAddress}"   type="text" data-options="required:true"  >
			</td>
		</tr>
		<tr>
			<td class="tr">是否为985院校</td>
			<td>
				 <input type="radio" id="i2" name="isNine" value="0"
	           <c:if test="${university.isNine==0}">
	           	checked="checked"
			    </c:if>
	           >不是
	           <input type="radio" id="i1" name="isNine"
	           <c:if test="${university.isNine==1}">
	           	checked="checked"
			    </c:if>
	            value="1">是
			</td>
		</tr>
		<tr>
			<td class="tr">是否为211院校</td>
			<td>
				 <input type="radio" id="i2" name="isTwo" value="0"
	           <c:if test="${university.isTwo==0}">
	           	checked="checked"
			    </c:if>
	           >不是
	           <input type="radio" id="i1" name="isTwo"
	           <c:if test="${university.isTwo==1}">
	           	checked="checked"
			    </c:if>
	            value="1">是
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
	<input name="id"  type="hidden" value="${university.id}" >
</form>
<!-- <script> -->
<!-- 	App.ajaxForm("#form"); -->
<!-- </script> -->