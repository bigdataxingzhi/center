Date.prototype.format = function(format){ 
    var o =  { 
    "M+" : this.getMonth()+1, //month 
    "d+" : this.getDate(), //day 
    "h+" : this.getHours(), //hour 
    "m+" : this.getMinutes(), //minute 
    "s+" : this.getSeconds(), //second 
    "q+" : Math.floor((this.getMonth()+3)/3), //quarter 
    "S" : this.getMilliseconds() //millisecond 
    };
    if(/(y+)/.test(format)){ 
    	format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
    }
    for(var k in o)  { 
	    if(new RegExp("("+ k +")").test(format)){ 
	    	format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
	    } 
    } 
    return format; 
};

var E3 = {
	// 编辑器参数
	kingEditorParams : {
		//指定上传文件参数名称
		filePostName  : "uploadFile",
		//指定上传文件请求的url。
		uploadJson : '/upload/photo',
		//上传类型，分别为image、flash、media、file
		dir : "image"
	},
	// 格式化时间
	formatDateTime : function(val,row){
		var now = new Date(val);
    	return now.format("yyyy-MM-dd hh:mm:ss");
	},

    init : function(){
    	// 初始化图片上传组件
    	this.initOnePicUpload();

    },
   

    //将一个textarea显示为富文本编辑器的样式.
    createEditor : function(select){
    	return KindEditor.create(select, E3.kingEditorParams);
    },
    

    
    /**
     * 初始化单图片上传组件 <br/>
     * 选择器为：.onePicUpload <br/>
     * 上传完成后会设置input内容以及在input后面追加<img> 
     */
    //单图片上传组件
    initOnePicUpload : function(){
    	$(".onePicUpload").click(function(){
    		//_self 表示a标签.
			var _self = $(this);
			KindEditor.editor(E3.kingEditorParams).loadPlugin('image', function() {
				this.plugin.imageDialog({
					showRemote : false,
					clickFn : function(url, title, width, height, border, align) {
						var input = _self.siblings("input");
						//将与a在同一td中的所有img元素删除.
						input.parent().find("img").remove();
						//将input隐藏域的value值设置为图片上传后的地址.
						input.val(url);
						var perfix = "http://p3pklhgqi.bkt.clouddn.com/";
						//动态显示图片.
						
						input.after("<a href='"+url+"' target='_blank'><img src='"+url+"' width='80' height='50' /></a>");
						this.hideDialog();
					}
				});
			});
		});
    }
};
