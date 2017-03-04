<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		
	</div>
	<div data-options="region:'center',border:false">
	<form>
		<table width="800px" style="margin:10px;">
<!-- 		data-options="{ -->
<!-- 			url: '${z:u('enterprise/list')}', -->
<!-- 			method:'post', -->
<!-- 			columns: [[ -->
<!-- 				{field:'id',checkbox:true}, -->
<!-- 				{field:'enterpriseName',title:'职称',width:150}, -->
<!-- 				{field:'department',title:'备注',width:150}, -->
<!-- 				{field:'represent',title:'添加时间',width:100}, -->
				
<!-- 			]], -->
<!-- 			onLoadSuccess: gridLoad -->
<!-- 		}"> -->
        <tr>
           <td width="100px">联系人:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="admin" id="search_text" name="search_text" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
           <td width="100px">联系电话:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="0571-86919142" id="search_text" name="search_text" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
           <td width="100px">传真:</td>
           <td>                    
           <input class="ml10 w200" type="text" value=" " id="search_text" name="search_text" />
           </td>
        </tr>
        <tr><td></td><tr> 
        <tr>
           <td width="50px">E-mail:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="hh@hdu.edu.cn" id="search_text" name="search_text" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
           <td width="50px">地址:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="浙江省杭州市下沙高教园区2号大街1158号杭州电子科技大学研究生院学位办" id="search_text" name="search_text" style="width:700px"/>
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
        <td></td>
        <td style="float:left;">
        &nbsp;&nbsp;&nbsp;
        <a id="edit" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>保存</a>
        <a id="delete" class="btn btn-sm btn-danger"><i class="icon icon-delete"></i>取消</a>
        </td>
        </tr>
		</table>
		</form>
	</div>
</div>
<script type="text/javascript">
function gridLoad() {
	$(".J_imgShow").each(function() {
		$(this).fancybox();
	});
}
function imgShow(v, r) {
	if (v == '') {
		return '/';
	} else {
		return '<a class="J_imgShow" href='+v+'><img src='+v+' style="height: 30px;"/></a>';
	}
}
$("#search_btn").on("click", function() {
    var wd = $("#search_text").val();
    if (wd.length == '') {
        App.alert("请输入搜索内容！", "info");
        return false;
    }
    $("#case_grid").datagrid({
        url: '${z:u('enterprise/list')}?wd='+wd
    });
});

$("#edit,#delete").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","info");
		}else{
			var eleId = $(this).attr("id");
			if(eleId=="edit"){
				App.popup('${z:u("/enterprise/edit")}?id='+row.id,{
					title:"编辑",
					width: 600,
					height: 500
				});
			}else if(eleId=="delete"){
	            App.ajax('${z:u("/enterprise/delete")}?id='+row.id,{
	            type: "POST"
	            });
				
			}
		}
	});
	
	$("#enable").click(function(){
		var row = $(".jq-datagrid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一条记录","warning");
		}else{
	            App.ajax('${z:u("/enterprise/enable")}?id='+row.id,{
	            type: "POST"
				});
		}
	});
	
	$("#detail").on("click", function() {
		var row = $("#case_grid").datagrid("getSelected");
		if(row == null){
			App.alert("请先选择一个项目","warning");
		}else{
			var id = row.id;
			App.popup('${z:u("/enterprise/details")}?id='+id,"查看详情");	
		}
	});
	
	$("#add").click(function(){
		App.popup('${z:u('enterprise/list_add')}',{
			title:"企业添加",
			width: 800,
			height: 600
		});
	});
	
	
</script>