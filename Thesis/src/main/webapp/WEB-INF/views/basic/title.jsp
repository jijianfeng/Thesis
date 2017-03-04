<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		<div id="grid-toolbar" class="clearfix p5">
			<c:if test="${sessionScope.userType==3}">
            <a id="add" class="btn btn-sm btn-success"><i class="icon icon-add"></i>新增</a>
            <a id="edit" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>编辑</a>
            <a id="delete" class="btn btn-sm btn-danger"><i class="icon icon-delete"></i>删除</a>
            </c:if>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid J_grid" fit="true" id="case_grid" data-options="{
 			url: '${z:u('basic/title')}', 
			method:'post',
			columns: [[
				{field:'id',checkbox:true},
				{field:'titlesName',title:'职称',width:150},
				{field:'titleMark',title:'备注',width:150},
			]],
			onLoadSuccess: gridLoad
		}">
		</table>
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
				App.popup('${z:u("/basic/title_edit")}?id='+row.id,{
					title:"编辑",
					width: 400,
					height: 200
				});
			}else if(eleId=="delete"){
	            App.ajax('${z:u("/basic/title_delete")}?id='+row.id,{
	            	msg: "确定要删除所选数据？",
		            type: "POST"
	            });
			}
		}
	});
	
	$("#add").click(function(){
		App.popup('${z:u('basic/title_add')}',{
			title:"职称添加",
			width: 400,
			height: 200
		});
	});
	
	
</script>