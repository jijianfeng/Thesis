<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" style="margin:10px 50px">
    	<tr>
			<td  style="width:100px"><label>对方学校级别：</label></td>
			<td><select class="jq-combobox" panelHeight="auto" style="width:150px" id="status" name="schoolLevel">
				<option value="0">985，211院校</option>
				<option value="1">211院校</option>
				<option value="2">普通院校</option>
			</select>
			<a style="color: red;font-size:30;">注意：985院校一定是211院校，211院校不一定是985院校！</a>
			</td>
		</tr>
		<tr>
			<td><label>送审次数：</label></td>
			<td><select class="jq-combobox" panelHeight="auto" style="width:100px" id="status" name="requireNumber">
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
			</select>
			</td>
		</tr>
		<tr>
			<td style="color: red;font-size:30;" >注意：</td>
			<td style="color: red;font-size:30;" >希望院校可填可不填，如有填会按1~5的顺序优先选择希望院校，跨级无效，但不保证一定被希望院校所评审！</td>
		</tr>
		<tr>
			<td>
			<label>希望院校一：</label></td>
			<td>
<!-- 				<span>所属区域:</span> -->
<!-- 	        	<select class="jq-combobox" id="arealistOne" style="width:100px" data-options="{ -->
<!-- 						method:'post', -->
<!-- 						editable:true, -->
<!-- 						url: '${z:u('public/arealist')}'}"> -->
<!-- 				</select> -->
				<span>所属大学:</span>
	        	<input type="text" disabled="true" id="universityOne" style="width:130px" />
				<button class="btn btn-xs btn-primary" id="look_enterprise1" type="button">查找</button>
				<input type="hidden" id ="unOne" name="unOne">
				<span>所属学院:</span>
	        	<select class="jq-combobox" id="collegelistOne"  name="collegeOne" style="width:150px" data-options="{
						method:'post',
						editable:false,
						}">
				</select>
			</td>
		</tr>
		<tr>
			<td><label>希望院校二：</label></td>
			<td>
<!-- 				<span>所属区域:</span> -->
<!-- 	        	<select class="jq-combobox" id="arealistTwo" style="width:100px" data-options="{ -->
<!-- 						method:'post', -->
<!-- 						editable:true, -->
<!-- 						url: '${z:u('public/arealist')}'}"> -->
<!-- 				</select> -->
				<span>所属大学:</span>
	        	<input type="text" disabled="true" id="universityTwo" style="width:130px" />
				<button class="btn btn-xs btn-primary" id="look_enterprise2" type="button">查找</button>
				<input type="hidden" id ="unTwo" name="unTwo">
				<span>所属学院:</span>
	        	<select class="jq-combobox"  id="collegelistTwo"  name="collegeTwo" style="width:150px" data-options="{
						method:'post',
						editable:false,
						}">
				</select>
			</td>
		</tr>
		<tr>
			<td><label>希望院校三：</label></td>
			<td>
<!-- 				<span>所属区域:</span> -->
<!-- 	        	<select class="jq-combobox" id="arealistThree" style="width:100px" data-options="{ -->
<!-- 						method:'post', -->
<!-- 						editable:true, -->
<!-- 						url: '${z:u('public/arealist')}'}"> -->
<!-- 				</select> -->
				<span>所属大学:</span>
	        	<input type="text" disabled="true" id="universityThree" style="width:130px" />
				<button class="btn btn-xs btn-primary" id="look_enterprise3" type="button">查找</button>
				<input type="hidden" id ="unThree" name="unThree">
				<span>所属学院:</span>
	        	<select class="jq-combobox"  id="collegelistThree" name="collegeThree" style="width:150px" data-options="{
						method:'post',
						editable:false,
						}">
				</select>
			</td>
		</tr>
		<tr>
			<td><label>希望院校四：</label></td>
			<td>
<!-- 				<span>所属区域:</span> -->
<!-- 	        	<select class="jq-combobox" id="arealistFour" style="width:100px" data-options="{ -->
<!-- 						method:'post', -->
<!-- 						editable:true, -->
<!-- 						url: '${z:u('public/arealist')}'}"> -->
<!-- 				</select> -->
				<span>所属大学:</span>
	        	<input type="text" disabled="true" id="universityFour" style="width:130px" />
				<button class="btn btn-xs btn-primary" id="look_enterprise4" type="button">查找</button>
				<input type="hidden" id ="unFour" name="unFour">
				<span>所属学院:</span>
	        	<select class="jq-combobox" id="collegelistFour" name="collegeFour" style="width:150px" data-options="{
						method:'post',
						editable:false,
						}">
				</select>
			</td>
		</tr>
		<tr>
			<td><label>希望院校五：</label></td>
			<td>
<!-- 				<span>所属区域:</span> -->
<!-- 	        	<select class="jq-combobox" id="arealistFive" style="width:100px" data-options="{ -->
<!-- 						method:'post', -->
<!-- 						editable:true, -->
<!-- 						url: '${z:u('public/arealist')}'}"> -->
<!-- 				</select> -->
				<span>所属大学:</span>
	        	<input type="text" disabled="true" id="universityFive" style="width:130px" />
				<button class="btn btn-xs btn-primary" id="look_enterprise5" type="button">查找</button>
				<input type="hidden" id = "unFive" name="unFive">
				<span>所属学院:</span>
	        	<select class="jq-combobox" id="collegelistFive" name="collegeFive" style="width:150px" data-options="{
						method:'post',
						editable:false,
						}">
				</select>
			</td>
		</tr>
		<tr>
			<td><label>截止日期：</label></td>
			<td><input class="jq-datebox"  style="width:150px" name="returnTime" data-options="required:true"/></td>
		</tr>
		<tr>
			<td>备注：</td>
			<td>
				<textarea style="width:450px;height:70px;"  name="remark"  class="jq-validatebox" ></textarea>
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
<script>
	$("#look_enterprise1").on("click", function() {
		//alert("查找院校");
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=1')}",
			width: 800,
			height: 600
		});
	});
	$("#look_enterprise2").on("click", function() {
		//alert("查找院校");
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=2')}",
			width: 800,
			height: 600
		});
	});
	$("#look_enterprise3").on("click", function() {
		//alert("查找院校");
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=3')}",
			width: 800,
			height: 600
		});
	});
	$("#look_enterprise4").on("click", function() {
		//alert("查找院校");
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=4')}",
			width: 800,
			height: 600
		});
	});
	$("#look_enterprise5").on("click", function() {
		//alert("查找院校");
		App.dialog({
			title: "选择学校",
			href: "${z:u('public/university_select?id=5')}",
			width: 800,
			height: 600
		});
	});
	
	$(document).ready(function(){  
	    $("#form").bind("submit", function(){  
	    	$('.jq-datagrid').datagrid('clearSelections');
	    	return true;
	    })
	})
</script>