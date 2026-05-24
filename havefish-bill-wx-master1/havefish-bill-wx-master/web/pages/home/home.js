import {
  BillModel
} from '../../models/bill.js'

import {
  Util
} from '../../utils/utils.js'

import deviceUtil from "../../miniprogram_npm/lin-ui/utils/device-util"

var WxNotificationCenter = require('../../utils/wx-notify.js')

var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    show_status_page: false,
    show_status_network: false,
    show_loading: true,
    loadingType: "loading",
    loadingShow: false,
    bills: [],
    date: Util.dateFormat("YYYY-mm", new Date()),
  },

  onGoTally() {
    wx.navigateTo({
      url: '/pages/tally/tally',
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    console.log('WxNotificationCenter:', WxNotificationCenter); // 确认 WxNotificationCenter 正确引入
    this.initTop();
    this.initBottom();
    const capsuleBarHeight = deviceUtil.getTitleBarHeight()
    console.log(capsuleBarHeight)
    this.setData({
      capsuleBarHeight
    })
    // 绑定 didNotification 到当前实例
    WxNotificationCenter.addNotification('refresh', this.didNotification, this)


  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 清除定时器
    clearInterval(this.refreshInterval);
    // 移除通知监听
    WxNotificationCenter.removeNotification('refresh', this);
  },

  //通知处理
// 通知处理
didNotification: function (notificationName, info) {
  console.log('didNotification called for notification:', notificationName, 'with info:', info);
  // 更新数据
  this.initTop();
  this.initBottom();
},
  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 每次页面显示时更新数据
    this.initTop();
    this.initBottom();

    // 调用 didNotification 来处理通知
    // this.didNotification();
  },
  async initTop() {
    try {
      let billAmount = await BillModel.getBillAmount(this.data.date);
      this.setData({
        billAmount,
        show_loading: false,
        show_status_network: false
      })
    } catch (error) {
      if (error.errMsg.includes("request")) {
        this.setData({
          show_loading: false,
          show_status_network: true
        })
      }
    }
  },

  async initBottom() {
    let billPaging = BillModel.getBillPaging(this.data.date);
    let data = await billPaging.getMoreData();
    if (!data) {
      return
    }
    if (data.items.length == 0) {
      this.setData({
        show_status_page: true
      })
    } else {
      this.setData({
        show_status_page: false
      })
    }
    this.setData({
      billPaging,
      bills: data.items,
      loadingType: "loading"
    })
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  async onReachBottom() {
    let billPaging = this.data.billPaging;
    let data = await billPaging.getMoreData();
    if (!data) {
      return
    }
    if (!data.moreData) {
      this.setData({
        loadingType: "end"
      })
    }
    let bills = this._uniteItems(data.items);
    this.setData({
      bills
    })
  },

  //下拉刷新时，如果当前最后一条数据与新数据的日期相同，进行合并，不然会出现重复日期的问题
  _uniteItems(items) {
    let bills = this.data.bills;
    for (let i in items) {
      if (items[i].date == bills[bills.length - 1].date) {
        bills[bills.length - 1].items = bills[bills.length - 1].items.concat(items[i].items)
      } else {
        bills.push(items[i])
      }
    }
    return bills;
  },

  onDateChange(event) {
    this.setData({
      date: event.detail
    })
    this.onRefresh();
  },

  onRefresh() {
    this.initTop();
    this.initBottom();
  },
})