<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="left" class="form-table" >
		<tr>
			<td>标题：</td>
			<td>
				<input name="title"  type="text" class="jq-validatebox" data-options="required:true" style="width:500px;">
			</td>
		</tr>
		<tr>
			<td>发送给：</td>
			<td>
				<select class="jq-combobox" name="sendType" panelHeight="auto" style="width:100px" id="type">
					<option value="0">全部</option>
					<option value="1">研究生部</option>
					<option value="2">学院</option>
					<option value="3">老师</option>
					<option value="4">管理员</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>内容：</td>
			<td>
				<textarea style="width:500px;height:300px;" id="" name="content" class="jq-validatebox" data-options="required:true" ></textarea>
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
</form>