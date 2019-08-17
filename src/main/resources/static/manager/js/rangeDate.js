$(function(){

	/**
	 * format (String)：Moment的日期格式。点击这里查看Moment文档。
	 *separator (String)：日期字符串之间的分隔符。
	 *language (String)：预定义的语言是"en"和"cn"。你可以使用这个参数自定义语言。也可以设置为"auto"来让浏览器自己检测语言。
	 *startOfWeek (String)："sunday" 或 "monday"。
	 *getValue (Function)：当从DOM元素中获取日期范围时会调用该函数，函数的上下文被设置为datepicker DOM。
	 *setValue (Function)：当向DOM元素中写入日期范围时调用该函数。
	 *startDate (String or false)：定义用户允许的最早日期，格式和format相同。
	 *endDate (String or false)：定义用户允许的最后日期，格式和format相同。
	 *minDays (Number) ：该参数定义日期范围的最小天数，如果设置为0，表示不限制最小天数。
	 *maxDays (Number)：该参数定义日期范围的最大天数，如果设置为0，表示不限制最大天数。
	 *showShortcuts (Boolean) ：先生或隐藏shortcuts区域。
	 *time (Object)：如果允许该参数就会添加时间的范围选择。
	 *shortcuts (Object)：定义快捷键按钮。
	 *customShortcuts (Array)：定义自定义快捷键按钮。
	 *inline (Boolean)：使用inline模式渲染该日期选择器，而不是overlay模式。如果设置为true，则要一起设置container参数。
	 *container (String, css selector || DOM Object) ：要进行渲染的日期选择器DOM元素。
	 *alwaysOpen (Boolean)：如果使用inline模式，你可能希望在页面加载时就渲染日期选择器。该参数设置为true时会隐藏"close"按钮。
	 *singleDate (Boolean)：设置为true可以选择单个的日期。
	 *batchMode (false / 'week' / 'month')：自动批处理模式。

	 */
	
	$('#date-range').dateRangePicker({
			
			format: 'YYYY-MM-DD',
		    separator: '~',
		    language: 'auto',
		    startOfWeek: 'sunday',// or monday
		    getValue: function()
		    {
		        return this.value;
		    },
		    setValue: function(s)
		    {
		        this.value = s;
		    },
		    startDate: false,
		    endDate: false,
		    minDays: 0,
		    maxDays: 0,
		    showShortcuts: true,
		    time: {
		        enabled: false
		    },
		    shortcuts:
		    {
		        //'prev-days': [1,3,5,7],
		        'next-days': [3,5,7],
		        //'prev' : ['week','month','year'],
		        'next' : ['week','month','year']
		    },
		    customShortcuts : [],
		    inline:false,
		    container: 'body',
		    alwaysOpen:false,
		    singleDate:false,
		    batchMode:false,
		    beforeShowDay: [],
		    dayDivAttrs: [],
		    dayTdAttrs: [],
		    applyBtnClass: ''
}).bind('datepicker-change',function(event,obj){

})
.bind('datepicker-apply',function(event,obj)
{

 // dateRange = obj.value;
})
.bind('datepicker-close',function(event,obj)
{

});   
	
});

