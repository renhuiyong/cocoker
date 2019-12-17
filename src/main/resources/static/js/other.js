var active = null,
    lastid, span;
mui(".mui-content").on("tap", "a", function () {
    var id = this.getAttribute("id");
    if (!active) {
        this.classList.add("active");
        if (id) {
            span = this.querySelector("span");
            span.classList.remove("mui-" + id);
            span.classList.add("mui-" + id + "-filled");
        }
        active = this;
    } else {
        active.classList.remove("active");
        if (lastid) {
            span.classList.remove("mui-" + lastid + "-filled");
            span.classList.add("mui-" + lastid);
        }

        this.classList.add("active");
        if (id) {
            span = this.querySelector("span");
            span.classList.remove("mui-" + id);
            span.classList.add("mui-" + id + "-filled");
        }

        active = this;
    }
    lastid = id;
});

//--------------------------------------------------------------------------
$(function () {


    //背景音乐
    // document.getElementById('notice').play();
    //历史佣金
    //历史
    $.ajax({
        url: "/cocoker/historyCommission",
        type: "GET",
        data: {"openid": getCookie('openid')},
        success: function (result) {
            if (result.historyCommission > 0) {
                $('.historyCommission').text(result.historyCommission);
            } else {
                $('.historyCommission').text(0);
            }

        }
    });
    //今天
    $.ajax({
        url: "/cocoker/historyCommissionByTime",
        type: "GET",
        data: {"openid": getCookie('openid')},
        success: function (result) {
            if (result.historyCommissionByTime > 0) {
                $('.historyCommissionByTime').text(result.historyCommissionByTime);
            } else {
                $('.historyCommissionByTime').text(0);
            }

        }
    });

    /**************************************时间格式化处理************************************/
    function dateFtt(fmt, date) { //author: meizz
        var o = {
            "M+": date.getMonth() + 1,                 //月份
            "d+": date.getDate(),                    //日
            "h+": date.getHours(),                   //小时
            "m+": date.getMinutes(),                 //分
            "s+": date.getSeconds(),                 //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    //下级总人数
    $.ajax({
        url: "/cocoker/proxyCount",
        type: "GET",
        data: {"openid": getCookie('openid')},
        success: function (result) {
            if (result.proxyCount > 0) {
                $('.proxyCount').text(result.proxyCount);
            } else {
                $('.proxyCount').text(0);
            }

        }
    });
    //在线人数
    lineNum();
    setInterval(lineNum, 10000);

    //点击数量m
    $('.btndiv button').click(function () {
        $('.btndiv button').removeAttr("check").removeClass('btnsdiv');
        $(this).attr("check", "check").addClass('btnsdiv');
        // $(".c2").text((Number(this.innerText.substring(2) * 6.89).toFixed(2)));
        $('.yjbt').text((Number($(this).text() * 1.85).toFixed(2)));
    });

    //刚进来看看有没有赢的订单未结算
    function checkOrder() {
        $.ajax({
            url: "/cocoker/order/info",
            data: {"openid": getCookie('openid'), "first": true, "currentMoney": currentMoney, "oid": ""},
            type: "GET",
            success: function (result) {
                if (result.code == 0) {
                    $('.money').text(result.data.usermoney)
                }
            }
        });
    }

    checkOrder();
    setTimeout(checkOrder, 31 * 1000)

    //
    mui.init({
        swipeBack: true //启用右滑关闭功能
    });

    //提交订单
    $('.subBtn').click(function () {
        if ($('.btndiv button[check=check]').length < 1) {
            popup({type: 'tip', msg: "请选择数量", delay: 1500});
            return
        }
        // if ($('.customNum').length < 5) {
        //     popup({type: 'tip', msg: "大于5", delay: 1500});
        //     return
        // }

        var money = currentMoney;

        //判断余额是否够
        var curNum = $('.money')[0].innerText.substring(0);
        var tarNum = $('.btndiv button[check=check]')[0].innerText;
        if (parseInt(curNum) > parseInt(tarNum)) {
            priceDatas.push(money);
            choosePrice = money;//选中
            //console.log("时间1："+currentDate);
        }
        dsqks = true;
        $.ajax({
            url: "/cocoker/order",
            data: {
                "openid": getCookie('openid'),
                "num": $('.btndiv button[check=check]')[0].innerText,
                "flag": flag,
                "index": money,
                "currentDate": currentDate
            },
            success: function (result) {
                if (result.code == -1) {
                    $('#modal-4').removeClass('md-show');
                    priceDatas = [];
                }
                //console.log("时间2："+currentDate);
                //关闭选择框
                $('.goumoney').hide();
                //显示买入时的值
                $('.mrzs').text(money);
                $('.ycjg').text("?");
                $('.zzzs').text("         ?         ");
                if (result.code == 0) {
                    //30秒之后查询
                    /*setTimeout(function () {
                        $.ajax({
                            url: "/cocoker/order/info",
                            data: {"openid": getCookie('openid'),"first":false,"currentMoney":currentMoney},
                            type: "GET",
                            success: function (result) {
                            	//console.log("结果："+currentMoney);
                               // console.log(result);
                                if (result.code == 0) {
                                    // popup({type: 'tip', msg: "竞猜结束", delay: 1500});
                                    $('.money').text(result.data.usermoney);
                                    //查询订单
                                    setTimeout(function(){
                                        $.ajax({
                                            url: "/cocoker/order/orderDetail",
                                            data: {"openid": getCookie('openid')},
                                            type: "GET",
                                            success: function (result) {
                                                $('.jyfx').text(result.data.direction);
                                                $('.gmsj').text(result.data.createTime.substring(0, 10));
                                                $('.jyjg').text(result.data.oindex);
                                                $('.zzjg').text(result.data.ofinal);
                                                $('.tr').text(result.data.money);
                                                $('.fh').text(result.data.result == '盈' ? result.data.money * 1.85 : 0);
                                                $('.ycjg').text(result.data.result);
                                                $('.zzzs').text(result.data.ofinal);
                                                $('#showMe').click();
                                            }
                                        });
                                    },1000);
                                }
                            }
                        });
                    }, 30 * 1000)*/

                    // myChart.
//                    clearInterval(s1);
//                    const cm = money;
//
//                    let s30 = 29;
//                    var s2 = setInterval(function () {
//                        loadEcharts(cm);
//                        if (s30 < 1) {
//                            clearInterval(s2);
//                            s30 = 29;
//                            s1 = setInterval(function () {
//                                loadEcharts('');
//                            }, 1000);
//                        }
//                        s30--;
//                    }, 1000);


                    //计时
                    /* let s = 30;
                     var flag = setInterval(function () {
                         if (s < 1) {
                             clearInterval(flag);
                             s = 31;
                         }
                         $('.sec').text(s -= 1);
                     }, 1000);*/

                    //弹窗
                    popup({type: 'success', msg: "订单提交成功", delay: 1000});

                    //更新数据
                    var user = result.data;
                    $('.money').text(user.usermoney);
                    xdOid.push(user.oid);//保存订单id
                    //console.log(xdOid);
                    setTimeout(checkOrder, 31 * 1000)
                } else {
                    if (dsq) {//下单失败  清除定时器
                        clearTimeout(dsq);
                        clearInterval(flag);
                        s = 31;
                        $('.sec').text(s -= 1);
                    }
                    popup({type: 'tip', msg: result.msg, delay: 1500});
                }
            }
        });

    });


//--------fun - -- - - ----------
    function lineNum() {
        $.ajax({
            url: "/cocoker/num",
            success: function (res) {
                $('.linenum').text(res);
            }
        })
    }
})
$(".newbtnparent button").on("tap", function (event) {
    event.stopPropagation();
});

$(function () {
    //获取mn成交数据
    getTurnover();

    setInterval(getTurnover, 13000)

    function getTurnover() {
        $.ajax({
            url: "/cocoker/getTurnover",
            success: function (res) {
                $("#dataNums").rollNumDaq({
                    deVal: res
                });
            }
        })
    }
});



