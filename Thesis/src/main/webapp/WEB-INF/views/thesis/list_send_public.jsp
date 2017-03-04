<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" style="margin:40px 50px">
		<tr>
			<td><label>截止日期：</label></td>
			<td><input class="jq-datebox"  style="width:150px" name="returnTime" data-options="required:true"/></td>
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
<script type="text/javascript">
	$(document).ready(function(){  
	    $("#form").bind("submit", function(){  
	    	$('.jq-datagrid').datagrid('clearSelections');
	    	return true;
	    })
	})
</script>