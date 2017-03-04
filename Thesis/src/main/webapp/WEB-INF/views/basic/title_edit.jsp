<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" >
		<tr>			
			<td>职称：</td>
			<td>
				<input name="titlesName"  type="text" value="${title.titlesName}" class="jq-validatebox"  data-options="required:true" >
			</td>
		</tr>
		<tr>		
			<td>备注：</td>
			<td>
				<input name="titleMark" class="jq-validatebox"  value="${title.titleMark}"  type="text" data-options="required:true"  >
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
	<input name="id"  type="hidden"  value="${title.id}" >
</form>