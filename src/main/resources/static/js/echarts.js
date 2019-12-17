var myChart = echarts.init(document.getElementById('main'));

$(function () {
    //页面进来加载一次
    loadEcharts('');
});

var choosePrice = '';

var s1 = setInterval(function () {
    if (dsqks) {//定时器放这边   解决异步问题
        //setTimeout(function(){
        var curNum = $('.money')[0].innerText.substring(0);
        var tarNum = $('.btndiv button[check=check]')[0].innerText;
        if (parseInt(curNum) > parseInt(tarNum)) {
            $('.sec').text(29);
            beginDSQ();
            dsqks = false;
        }

        // },1)
    }
    loadEcharts('')
}, 1500);

var currentMoney;
var currentDate;

var currentDate2;

function cul(a, b, f) {
    a = parseFloat(a);
    b = parseFloat(b);

    if (f == "+") {
        return (a + b).toFixed(4);
    } else if (f == "-") {
        return (a - b).toFixed(4);
    } else if (f == "*") {
        return (a * b).toFixed(4);
    } else if (f == "/") {
        return (a / b).toFixed(4);
    }
}

function isPhone() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}

var dsqks = false;
var dsq;//弹窗定时器
var xdOid = [];

//30秒之后查询
function beginDSQ() {
    //计时
    let s = isPhone() ? 29 : 28;
    // console.log("延时："+currentMoney);
    var flag = setInterval(function () {
        if (s < 1) {
            clearInterval(flag);
            priceDatas = priceDatas.slice(1);
            s = 31;
            choosePrice = '';
            //onsole.log(xdOid);
            let oid = xdOid[0];//取出先存进来的id
            xdOid.splice(0, 1);//将该id从数组中删除
            //console.log(xdOid);
            $.ajax({
                url: "/cocoker/order/info",
                data: {"openid": getCookie('openid'), "first": false, "currentMoney": currentMoney, "oid": oid},
                type: "GET",
                success: function (result) {
                    if (result.code == 0) {
                        $('.money').text(result.data.usermoney);
                        //查询订单
                        setTimeout(function () {
                            $.ajax({
                                url: "/cocoker/order/orderDetail",
                                data: {"openid": getCookie('openid'), "oid": oid},
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
                                    //隐藏倒计时圈圈
                                    // $(".d1").css("display", "none")
                                    // $(".d2").css("display", "none")
                                    // alert(result.data.result);
                                    // if(result.data.result == '盈'){
                                    //     $(".d1").css("display","none")
                                    //     $(".d2").css("display","none")
                                    // }else{
                                    //    $(".d1").css("display","none")
                                    //    $(".d2").css("display","none")
                                    // }
                                    $('#showMe').click();
                                }
                            });
                        }, 1000);
                    }
                }
            });
        }
        $('.sec').text(s -= 1);
    }, 1000);
}

var priceDatas = [];
// var echartsData;
var minMoney = 0/*6.7870*/, maxMoney = 0;//6.8030;
//var minMoney = 6.7870,maxMoney =6.8030;
function loadEcharts(line) {
    $.get('/cocoker/data/getEchartsDatas', function (data) {
        var choosePriceData = [];
        currentMoney = data[149][1];
        currentMoney2 = data[148][1];
        currentDate = data[149][0];
        //console.log("延时2："+currentMoney);
        //订单线条处理
        choosePriceData.push({yAxis: data[149][1]});
        for (var i = 0; i < priceDatas.length; i++) {
            choosePriceData.push({yAxis: priceDatas[i]});
        }
        data[149][1] = {
            "value": currentMoney,
            "symbol": "image://images/test1.png",
            "symbolSize": 20
        };

        var date = new Date();
        var hour = date.getHours();
        var minuts = date.getMinutes();
        var seconds = date.getSeconds();
        if (("" + hour).length == 1) {
            hour = "0" + hour;
        }

        for (let i = 0; i < 30; i++) {
            data.push([hour + ":" + minuts + ":" + seconds]);
        }
        if (currentMoney < currentMoney2) {
            $("#c1").text(currentMoney).css({"color": "green"});
            $('.c1').text(currentMoney).css({"color": "green"});
        } else {
            $("#c1").text(currentMoney).css({"color": "red"});
            $('.c1').text(currentMoney).css({"color": "red"});
        }
        //上限限控制
        var c = parseFloat(currentMoney);
        if (minMoney == 0) {//第一次进来
            var min = data[0][1], max = 0;
            for (i in data) {
                if (min > parseFloat(data[i][1])) {
                    min = data[i][1];
                }
                if (max < parseFloat(data[i][1])) {
                    max = data[i][1];
                }
            }
            if (cul(max, min, "-") < 0.016) {
                var x = cul(cul(0.016, cul(max, min, "-"), "-"), 2, "/");
                maxMoney = cul(max, x, "+");
                minMoney = cul(min, x, "-");
            }
            if (cul(max, min, "-") > 0.016) {
                minMoney = min;
                maxMoney = max;
            }

            //minMoney=cul(currentMoney,0.008,"-");
            //maxMoney=cul(currentMoney,0.008,"+");
        }
        if (c && c <= cul(minMoney, 0.002, "+")) {
            maxMoney = cul(maxMoney, cul(cul(minMoney, 0.002, "+"), c, "-"), "-");//这个必须放前面
            minMoney = cul(minMoney, cul(cul(minMoney, 0.002, "+"), c, "-"), "-");
        }
        if (c && c >= cul(maxMoney, 0.002, "-")) {
            minMoney = cul(minMoney, cul(c, cul(maxMoney, 0.002, "-"), "-"), "+");//这个必须放前面
            maxMoney = cul(maxMoney, cul(c, cul(maxMoney, 0.002, "-"), "-"), "+");
        }
        option = {
            title: {
//                    text: 'Beijing AQI'
            },
            grid: {
                left: '1',
                right: '10%',
                bottom: '1%',
                top: '2%',
                containLabel: true
            },
            tooltip: {
                trigger: 'axis'
            },
            xAxis: {
                data: data.map(function (item) {
                    return item[0];
                }),
                splitLine: {
                    show: true,
                    lineStyle: {
                        // type: 'dotted',//虚线
                        color: 'rgba(88,88,88,.6)',
                        width: 1,
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: 'rgba(88,88,88,1)',
                        width: 0,
                    }
                },
            },
            yAxis: [{
                // min: 6.7913,
                // max: 6.7995,
                min: minMoney,//6.7870,
                // type:'dotted',
                max: maxMoney,//6.8030,
                splitArea: {
                    show: false
                },
                splitLine: {
                    // show: false,//去除网格线
                    lineStyle: {
                        type: 'dotted',
                        color: 'rgba(88,88,88,.6)',
                        width: 1,

                    }
                },
                axisLine: {
                    lineStyle: {
                        color: 'rgba(88,88,88,1)',
                        width: 1,
                        type: 'dotted'
                    }
                },
            }],
            // yAxis: {
            //     splitLine: {
            //         // show: true
            //     }
            // },
            // dataZoom: [{
            //     startValue: data[50][0],
            // }, {
            //     type: 'inside'
            // }],
            visualMap: {
                show: false,
                top: 10,
                right: 10,
                precision: 2,
                outOfRange: {
                    color: '#fff'
                },
            },
            series: {
                name: 'USD',
                itemStyle: {
                    normal: {
                        color: '#FFF',
                        lineStyle: {
                            color: 'white',//'#fff',
                            width: 1,//设置线条粗细
                        }
                    }
                },
                type: 'line',
                data: data.map(function (item) {
                    return item[1];
                }),
                symbol: "none", //去掉小圆点
                // animation: false, //去掉动画
                animationDuration: 0,
                animationEasing: 'cubicInOut',
                animationDurationUpdate: 0,
                animationEasingUpdate: 'cubicInOut',
                showAllSymbol: true, //显示所有原点
                //smooth:true,//平滑
                markLine: {
                    symbol: 'none',
                    silent: true,
                    color: '#fff',
                    symbolSize: 3,
                    label: {
                        normal: {
                            padding: [-30, 0, -30, 120],
                            show: true,
                            position: 'middle',
                            formatter: '{c}',
                        },
                    },
                    lineStyle: {
                        type: 'dotted'
                    },
                    data: choosePriceData/*[{
                        yAxis: data[149][1],
                    }, {
                        yAxis: choosePrice,//line,
                    }

                    ]*/
                }
            }
        }

        var last = currentMoney;
        option.series.markLine.data[0].yAxis = last;
        myChart.setOption(option);
    });
}


//-----------------------------
//        myChart.setOption(option);
//        ---------------- - -------------------------