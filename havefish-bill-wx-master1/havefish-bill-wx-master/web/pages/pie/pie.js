import * as echarts from '../../ec-canvas/echarts';
import {
  AssayModel
} from '../../models/assay';
let consume_grids = [{
  id: 1,
  image: "/images/account/eat.png",
  text: "三餐"
}, {
  id: 2,
  image: "/images/account/shopping.png",
  text: "购物"
}, {
  id: 3,
  image: "/images/account/sock.png",
  text: "零食"
}, {
  id: 4,
  image: "/images/account/fruit.png",
  text: "水果"
}, {
  id: 5,
  image: "/images/account/plane.png",
  text: "出行"
}, {
  id: 6,
  image: "/images/account/car.png",
  text: "修车"
}, {
  id: 7,
  image: "/images/account/education.png",
  text: "学习"
}, {
  id: 8,
  image: "/images/account/children.png",
  text: "小孩"
},
{
  id: 9,
  image: "/images/account/gift.png",
  text: "送礼"
},
{
  id: 10,
  image: "/images/account/pet.png",
  text: "宠物"
}
];
let income_grids = [{
id: 13,
image: "/images/account/salary.png",
text: "工资"
}, {
id: 14,
image: "/images/account/bonus.png",
text: "奖金"
}, {
id: 15,
image: "/images/account/financing.png",
text: "理财"
}, {
id: 16,
image: "/images/account/lifefee.png",
text: "生活费"
}, {
id: 17,
image: "/images/account/vicejob.png",
text: "兼职"
}, {
id: 18,
image: "/images/account/wipeout.png",
text: "报销"
}, {
id: 19,
image: "/images/account/refund.png",
text: "退款"
}, {
id: 20,
image: "/images/account/gift.png",
text: "礼金"
}, ];
var chart = null;

function initChart(canvas, width, height) {
  chart = echarts.init(canvas, null, {
    width: width,
    height: height
  }); 
  canvas.setChart(chart);
  chart.on('click', (parmas) => {
    console.log(parmas)
  })
  return chart;
}

Page({

  data: {
    show_network_status: false,
    show_data_status: false,
    ec: {
      onInit: initChart
    }
  },

  async onLoad(options) {
    let date = options.date;
    try {
      let data = await AssayModel.assay2(date, 0, 0)
      this._handleSuccess(data)
    } catch (error) {
      this.setData({
        show_network_status: true
      })
    }
  },

  _handleSuccess(data) {
    let chartDataList = [];
    if (!data || data.length == 0) {
      this.setData({
        show_data_status: true
      })
      return
    }
    for (let i in data) {
      let chartData = {};
      chartData['value'] = data[i].percent;
      chartData['name'] = `${data[i].name}:${data[i].percent}%`;
      chartDataList.push(chartData);
    }
    this.setData({
      items: data,
      show_network_status: false
    })

    var option = {
      backgroundColor: "#ffffff",
      color: ["#37A2DA", "#32C5E9", "#67E0E3", "#91F2DE", "#FFDB5C", "#FF9F7F"],
      series: [{
        label: {
          show: true,
        },
        type: 'pie',
        center: ['50%', '50%'],
        radius: [0, '55%'],
        data: chartDataList,
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 2, 2, 0.3)'
          }
        }
      }]
    };
    setTimeout(() => {
      chart.clear()
      chart.setOption(option);
    }, 50)
  },

  onRefresh() {
    this.onLoad()
  }

});