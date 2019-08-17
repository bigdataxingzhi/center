<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div>
      <!-- 标识为一棵树 -->
	 <ul id="contentCategory" class="easyui-tree">
    </ul>
</div>
<!-- 右键点击弹出菜单 -->
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
$(function(){
	$("#contentCategory").tree({
		url : '/rest/content/category',
		animate: true,
		method : "GET",
		onContextMenu: function(e,node){
			/*阻止浏览器的默认点击事件*/
            e.preventDefault(); 
			/*选中点击的节点*/
            $(this).tree('select',node.target);
			/*弹出菜单*/
            $('#contentCategoryMenu').menu('show',{
            	/*指定菜单弹出的位置*/
                left: e.pageX,
                top: e.pageY
            });
        },
        /*
        *为树添加编辑结束事件
        */
        onAfterEdit : function(node){
        	var _tree = $(this);
        	//节点的创为0,代表新增节点
        	if(node.id == 0){
        		// 新增节点
        		$.post("/rest/content/category",{parentId:node.parentId,name:node.text},function(data){
        			_tree.tree("update",{
        				target : node.target,
        				id : data.id  //更新节点id,为后台返回的数据
        			});
        		});
        	}else{
        		$.ajax({
        			   type: "PUT",
        			   url: "/rest/content/category",
        			   data: {id:node.id,name:node.text},
        			   success: function(msg){
        				   $.messager.alert('提示','重命名成功!');
        			   },
        			   error: function(){
        				   $.messager.alert('提示','重命名失败!');
        			   }
        			});
        	}
        }
	});
});

/*
 * 菜单的点击事件
 *
 */
function menuHandler(item){
	//得到树
	var tree = $("#contentCategory");
	//得到数中选中的节点
	var node = tree.tree("getSelected");
	//通过名字区分点击了哪项菜单
	if(item.name === "add"){
		tree.tree('append', {
			//指定父节点为当前选中的节点
            parent: (node?node.target:null),
            data: [{
                text: '新建分类',
                id : 0,  //指定新增节点的创为0,数据库中并没有,页面的假象而已.
                parentId : node.id
            }]
        }); 
		//找到新增的节点
		var _node = tree.tree('find',0);
		//选中新增的节点,并且开始编辑
		tree.tree("select",_node.target).tree('beginEdit',_node.target);
	}else if(item.name === "rename"){
		tree.tree('beginEdit',node.target);
	}else if(item.name === "delete"){
		$.messager.confirm('确认','确定删除名为 '+node.text+' 的分类吗？',function(r){
			if(r){
				$.ajax({
     			   type: "POST",
     			   url: "/rest/content/category",
     			   data : {parentId:node.parentId,id:node.id,"_method":"DELETE"},
     			   success: function(msg){
     				  tree.tree("remove",node.target);
     			   },
     			   error: function(){
     				   $.messager.alert('提示','删除失败!');
     			   }
     			});
			}
		});
	}
}
</script>