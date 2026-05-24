const { getRequest } = require("../../utils/request");

// pages/login/login.js
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },

  goReg(){
    wx.navigateTo({
      url: '/pages/register/register',
    })
  },
  /**
   * 登陆
   */
  login: function (e) {
    let username = e.detail.value.username;
    let pwd = e.detail.value.pwd;
    if (username == '' || username == null) {
      wx.showToast({
        title: '账号不能为空',
        icon: 'none'
      })
      return;
    }
    if (pwd == '' || pwd == null) {
      wx.showToast({
        title: '密码不能为空',
        icon: 'none'
      })
      return;
    }

    wx.request({
     url: 'https://www.benben-x.cn/api/user/loginV2?username='+username+'&pwd='+pwd,
     //url: 'http://localhost:8080/api/user/loginV2?username='+username+'&pwd='+pwd,
      method: "get",
      success: (res) => {
        let rest = res.data;
        if (res.data.status == 0) {
          wx.hidenM
          // 存储用户信息
          wx.setStorage({
            key: 'userInfo',
            data: rest.data
          })
          // 存储token
          wx.setStorage({
            key: 'token',
            data: rest.data.token
          })
          
          wx.switchTab({
            url: '/pages/index/index',
          })
        } else {
          wx.showToast({
            title:  res.data.msg,
            icon: 'none'
          });
        }
      }
    })
  }
})