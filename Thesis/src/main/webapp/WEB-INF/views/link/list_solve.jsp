<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="left" class="form-table" >
    	<tr>
		<td>提问时间：</td><td><font color="blue"><b>${date1}</b></font></td>
		</tr>
		<tr>
		<td>问题标题：</td><td><font color="blue"><b>${question.questionTitle}</b></font></td>
		</tr>
		<tr>
			<td>问题详情：</td>
			<td>
				<textarea disabled="disabled" style="width:500px;height:180px;" id="" name="">${question.questionContent}</textarea>
			</td>
		</tr>
		<c:if test="${question.reply!=''}">
			<tr>
				<td>回复时间：</td><td><font color="blue"><b>${date}</b></font></td>
			</tr>
		</c:if>
		<tr>		
			<td>回复：</td>
			<td>
				<textarea  style="width:500px;height:180px;"  name="reply"  class="jq-validatebox"  data-options="required:true"
				<c:if test="${question.reply!=''}"> disabled="disabled"</c:if>
				>${question.reply}</textarea>
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
	<input name="id"  type="hidden" value="${question.id}"  >
</form>