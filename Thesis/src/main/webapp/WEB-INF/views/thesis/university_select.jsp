<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z" %>

<div class="jq-layout rel" data-options="fit:true">
    <div data-options="region:'north',border:false">
        <div class="tools fix" style="padding-bottom: 5px;">
            <ul class="fl">
                <li>
                    <span>学校名称：</span>
                    <input type="text" id="wd2"/>
                    <span>学校代码：</span>
                    <input type="text" id="wd3"/>
                    <button class="btn btn-primary btn-sm" id="search2">搜索</button>
                </li>
            </ul>
            <ul class="toolbar fr">
                <li id="add_to_form">
                    <button class="btn btn-sm btn-success">将选择的学校带回表单</button>
                </li>
            </ul>
        </div>
    </div>
    <div data-options="region:'center',border:false">
        <table id="grtid2" title="学校列表" fit="true" class="jq-datagrid" data-options="{
			url:'${z:u('public/university_select')}',
			method:'post',
			columns: [[
				{field:'id',checkbox:true},
				{field:'universityName',title:'学校名称',width: 200},
				{field:'universityCode',title:'学校代码',width: 200},
			]]
			}">
        </table>
    </div>
    <input type="hidden" value="${type}" id="hid">
</div>
<script type="text/javascript">
    // 搜索
    $("#search2").on("click", function () {
        var val = $("#wd2").val();
        var vall = $("#wd3").val();
        $("#grtid2").datagrid({
            url: '${z:u("/public/university_select")}?name=' + val +'&code='+vall
        });
    });
    // 将对应的内容带回表单
    $("#add_to_form").on("click", function () {
    	//alert(row.id);
        var row = $("#grtid2").datagrid("getSelected");
        if (row == null) {
            App.alert("请先选择一个学校", "warning");
            return false;
        }
        var type = $("#hid").val();
//         alert(row.id+"::::"+type);
        if(type==1){
        	$("#universityOne").val(row.universityName);
            $("#unOne").val(row.id);
            $('#collegelistOne').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+row.id);
            $('#collegelistOne').combobox('setValues','0');
        }
        else if(type==2){
        	$("#universityTwo").val(row.universityName);
            $("#unTwo").val(row.id);
            $('#collegelistTwo').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+row.id);
            $('#collegelistTwo').combobox('setValues','0');
        }
        else if(type==3){
        	$("#universityThree").val(row.universityName);
            $("#unThree").val(row.id);
            $('#collegelistThree').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+row.id);
            $('#collegelistThree').combobox('setValues','0');
            
        }
        else if(type==4){
        	$("#universityFour").val(row.universityName);
            $("#unFour").val(row.id);
            $('#collegelistFour').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+row.id);
            $('#collegelistFour').combobox('setValues','0');
        }
        else if(type==5){
        	$("#universityFive").val(row.universityName);
            $("#unFive").val(row.id);
            $('#collegelistFive').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+row.id);
            $('#collegelistFive').combobox('setValues','0');
        }
        else if(type==6){
        	$("#universityNamea").val(row.universityName);
            $("#universityaa").val(row.id);
//             alert(row.id);
            var n = row.id;
			$('#collegelista').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+n);
 			$('#collegelista').combobox('setValues','');
			$('#majorlista').combobox('setValues','');
			$('#teacherlista').combobox('setValues','');
        }
        else if(type==7){
//         	alert("哈哈哈");
        	$("#universityName").val(row.universityName);
            $("#universitya").val(row.id);    
            $(".panel-tool-close:eq(0)").click();
        }
        else if(type==8){
//         	alert("哈哈哈");
        	$("#universityNamea").val(row.universityName);
            $("#universityaa").val(row.id);    
        }
        // 关闭弹出层
        $(".panel-tool-close:eq(1)").click();
    });
</script>