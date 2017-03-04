<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>
<form id="form" action="${__url__}" method="post" enctype="multipart/form-data" >
	<table align="center"  class="form-table" style="font-size:15px" bgcolor="white" ; >
		<input  name="resultId" type="hidden" readonly="readonly" value="${result.id}" data-options="required:true," />
			<tr>
				<td><input type="file" name="file"></td>
				<td><button type="submit" class="btn btn-small btn-success">确定评审</button></td>
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